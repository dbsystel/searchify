package de.db.searchify.configuration;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 20.02.17.
 */
@Configuration
@EnableConfigurationProperties(GraphProperties.class)
public class DataBeanConfiguration {

    @Autowired
    private GraphProperties properties;

    @Bean
    public Graph gremlinGraph() {
        return GraphFactory.open(properties.getGraph());
    }

}
