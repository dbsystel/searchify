package de.db.searchify.service;

import de.db.searchify.api.Processor;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 21.02.17.
 */
@Service
public class LinkBuilderProcessor implements Processor {

    @Autowired
    Graph graph;

    @Override
    public void run() {
        graph.vertices().forEachRemaining(
                this::buildLinks
        );
    }

    private void buildLinks(Vertex vertex) {

    }


}
