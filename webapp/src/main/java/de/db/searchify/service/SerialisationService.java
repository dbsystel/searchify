package de.db.searchify.service;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 21.02.17.
 */
@Service
public class SerialisationService {

    @Autowired
    private Graph graph;

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        builder.append("graph [\n");
        appendNodes(builder);
        appendEdges(builder);
        builder.append("]");
        return builder.toString();
    }

    private void appendNodes(StringBuilder builder) {
        graph.vertices().forEachRemaining(vertex -> {
            builder
                    .append("\tnode [\n")
                    .append("\t\tid ")
                    .append(vertex.id())
                    .append("\n")
                    .append("\t\tlabel \"")
                    .append(vertex.label())
                    .append("\"\n")
                    .append("\t\ttype ")
                    .append(vertex.property("dbsearch_doctype_s").isPresent() ? vertex.property("dbsearch_doctype_s").value() : "None")
                    .append("\n\t]\n");
        });
    }

    private void appendEdges(StringBuilder builder) {
        graph.edges().forEachRemaining(edge -> {
            builder
                    .append("\tedge [\n")
                    .append("\t\tid ")
                    .append(edge.id())
                    .append("\n")
                    .append("\t\tlabel \"")
                    .append(edge.label())
                    .append("\"\n")
                    .append("\t\tsource ")
                    .append(edge.inVertex().id())
                    .append("\n")
                    .append("\t\ttarget ")
                    .append(edge.outVertex().id())
                    .append("\n\t]\n");
        });
    }

}
