package de.db.searchify.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 21.02.17.
 */
@ConfigurationProperties(prefix = "")
public class GraphProperties {

    private Map<String,String> graph;

    public Map<String, String> getGraph() {
        return graph;
    }

    public void setGraph(Map<String, String> graph) {
        this.graph = graph;
    }
}
