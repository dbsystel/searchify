/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package de.db.searchify.service;

import com.google.common.collect.Iterators;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 */
@Service
public class SolrIndexerService {

    private static final int BATCH_SIZE = 100;
    private static final int COMMIT_WITHIN = 500;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SolrClient solrClient;
    private final Graph gremlin;

    private final AtomicBoolean indexing;

    @Autowired
    public SolrIndexerService(SolrClient solrClient, Graph gremlin) {
        this.solrClient = solrClient;
        this.gremlin = gremlin;
        indexing = new AtomicBoolean(false);
    }

    public void index() {
        if (indexing.compareAndSet(false, true)) {
            try {
                solrClient.deleteByQuery("*:*");
                Iterators.partition(gremlin.vertices(), BATCH_SIZE)
                        .forEachRemaining(p -> {
                            final Set<SolrInputDocument> docs = p.stream()
                                    .map(this::vertex2SolrInputDocument)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toSet());
                            if (!docs.isEmpty()) {
                                try {
                                    solrClient.add(docs, COMMIT_WITHIN);
                                } catch (SolrServerException | IOException e) {
                                    log.error("Could not send {} documents to solr: {}", docs.size(), e.getMessage(), e);
                                }
                            }
                        });
                solrClient.commit();
            } catch (IOException | SolrServerException e) {
                log.warn("Error during solrClient.commit() - it could take a while until changes are visible", e);
            } finally {
                indexing.set(false);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.warn("Indexing already in progress", new Throwable("Indexing already in progress, callstack"));
            } else {
                log.warn("Indexing already in progress");
            }
        }
    }

    protected SolrInputDocument vertex2SolrInputDocument(Vertex v) {
        final SolrInputDocument doc = new SolrInputDocument();
        // TODO: create a SolrInputDocument from the Vertex
        doc.setField("id", T.id.apply(v));
        return doc;
    }

}
