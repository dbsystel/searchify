package de.db.searchify.processor;

import de.db.searchify.api.Processor;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 22.02.17.
 */
@Service
public class DecodingProcessor implements Processor {

    @Autowired
    Graph graph;

    @Override
    public void run() {
        graph.vertices().forEachRemaining(vertex -> {
            if(vertex.property("dbsearch_content_t").isPresent()) {
                vertex.property(VertexProperty.Cardinality.single, "dbsearch_content_t", decode(vertex.property("dbsearch_content_t").value())) ;
            }
        });
    }

    private String decode(Object dbsearch_content_t) {
        return new String(Base64.getDecoder().decode(String.valueOf(dbsearch_content_t)));
    }
}
