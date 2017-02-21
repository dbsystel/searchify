package de.db.searchify.service;

import com.google.common.collect.Iterators;
import com.google.common.io.Resources;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 21.02.17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("test")
public class HtmlToMetadataProcessorIntegrationTest {

    @Autowired
    Graph graph;

    @Autowired
    HtmlToMetadataProcessor metadataProcessor;

    @Test
    public void testRealData() throws Exception {
        loadData();
        metadataProcessor.run();

        assertEquals(7, Iterators.size(graph.vertices()));
        assertEquals(6,Iterators.size(graph.edges()));

        graph.close();
    }

    private void loadData() throws IOException {

        graph.edges().forEachRemaining(Element::remove);
        graph.vertices().forEachRemaining(Element::remove);

        Properties properties = new Properties();
        properties.load(Resources.getResource("vertex.properties").openStream());

        Vertex vertex = graph.addVertex();
        for(String name : properties.stringPropertyNames()) {
            vertex.property(name, properties.getProperty(name));
        }

        vertex.property("dbsearch_content_t", Resources.toString(Resources.getResource("confluence.html"), Charset.forName("UTF-8")));
    }

}
