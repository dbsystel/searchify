package de.db.searchify.configuration;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 20.02.17.
 */
@Configuration
public class DataBeanConfiguration {

    @Value("searchify.graph.location:/tmp/location")
    private String location;

    @Bean
    public Graph tikerGraph() {
        BaseConfiguration configuration = new BaseConfiguration();
        configuration.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_GRAPH_LOCATION,location);
        configuration.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_GRAPH_FORMAT,"graphml");
        return TinkerGraph.open(configuration);
    }

}
