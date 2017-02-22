package de.db.searchify.processor;

import de.db.searchify.api.Processor;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 21.02.17.
 */
@Service
public class LinkBuilderProcessor implements Processor {

    @Autowired
    Graph graph;

    @Value("${searchify.processor.html.base_url:https://expertfinder.db.redlink.io/confluence}")
    private String BASE_URL;

    @Override
    public void run() {
        graph.vertices().forEachRemaining(
                this::buildLinks
        );
    }

    private void buildLinks(Vertex vertex) {
        //subpages
        if(vertex.property("dbsearch_parent_id_s").isPresent()) {

            if(graph.traversal().V().has("id",vertex.property("dbsearch_parent_id_s").value()).hasNext()) {
                Vertex v = graph.traversal().V().has("id",vertex.property("dbsearch_parent_id_s").value()).next();
                vertex.addEdge("parent", v);
            }
        }
        //users
        if(vertex.property("dbsearch_author_username_s").isPresent()) {

            Vertex v = null;


            if(graph.traversal().V().has("dbsearch_doctype_s","Person").has(T.label, vertex.property("dbsearch_author_t").value()).hasNext()) {
                v = graph.traversal().V().has("dbsearch_doctype_s","Person").has(T.label, vertex.property("dbsearch_author_t").value()).next();
            }

            if(v == null) {
                v = createUserNode(
                        (String) vertex.property("dbsearch_author_username_s").value(),
                        (String) vertex.property("dbsearch_author_t").value(),
                        null//(String) vertex.property("dbsearch_author_email_s").value()
                );
            }

            //set email (if node was existing already)
            /*if(!v.property("dbsearch_email_s").isPresent()) {
                v.property("dbsearch_email_s", vertex.property("dbsearch_author_email_s").value());
            }*/

            vertex.addEdge("author",v);
        }

    }

    private synchronized Vertex createUserNode(String username, String displayName, String email) {
        return graph.addVertex(
                T.label, displayName,
                "id", createLinkForUsername(username),
                //"dbsearch_email_s", email,
                "dbsearch_link_s", createLinkForUsername(username),
                "dbsearch_title_s", displayName,
                "dbsearch_abstract_t", username,
                "dbsearch_doctype_s", "Person"
        );
    }

    private String createLinkForUsername(String username) {
        return BASE_URL + "/display/~" + username;
    }


}
