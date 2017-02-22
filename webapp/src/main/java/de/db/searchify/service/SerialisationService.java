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
        builder.append("\tdirected 1\n");
        builder.append("\tcomment \"Searchify Content Graph\"");
        appendNodes(builder);
        appendEdges(builder);
        builder.append("]");
        return builder.toString();
    }

    private void appendNodes(StringBuilder builder) {
        graph.vertices().forEachRemaining(vertex -> {
            builder.append("\tnode [\n");
            appendProperty(builder, "id", vertex.id());
            appendProperty(builder, "label", vertex.label());
            appendProperty(builder, "type", vertex.property("dbsearch_doctype_s").isPresent() ? vertex.property("dbsearch_doctype_s").value() : "None");
            builder.append("\t]\n");
        });
    }

    private void appendEdges(StringBuilder builder) {
        graph.edges().forEachRemaining(edge -> {
            builder.append("\tedge [\n");
            appendProperty(builder, "id", edge.id());
            appendProperty(builder, "source", edge.inVertex().id());
            appendProperty(builder, "target", edge.outVertex().id());
            appendProperty(builder, "label", edge.label());
            builder.append("\t]\n");
        });
    }

    private void appendProperty(StringBuilder builder, String property, Object value) {
        builder.append("\t\t");
        builder.append(property).append(" ");
        if (value instanceof Number) {
            builder.append(value);
        } else {
            builder.append('"').append(String.valueOf(value).replaceAll("&", "&amp;").replaceAll("\"", "&quot;")).append('"');
        }
        builder.append("\n");
    }

}
