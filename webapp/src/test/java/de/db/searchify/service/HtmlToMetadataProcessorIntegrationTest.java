package de.db.searchify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterators;
import com.google.common.io.Resources;
import de.db.searchify.model.Status;
import de.db.searchify.processor.HtmlToMetadataProcessor;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
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

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    Graph graph;

    @Autowired
    PipelineService pipelineService;

    @Autowired
    SerialisationService serialisationService;

    @Test
    public void testRealData() throws Exception {
        loadData();

        pipelineService.run();

        while(pipelineService.status().getState().equals(Status.State.running)) {
            Thread.sleep(500);
        }

        assertEquals(42, Iterators.size(graph.vertices()));
        assertEquals(27,Iterators.size(graph.edges()));

        System.out.println(serialisationService.serialize());

        graph.close();
    }

    private void loadData() throws IOException {

        graph.edges().forEachRemaining(Element::remove);
        graph.vertices().forEachRemaining(Element::remove);

        HashMap<String,HashMap<String,Object>> map = mapper.readValue(Resources.getResource("export3.json"), HashMap.class);

        for(String id : map.keySet()) {
            Map<String,Object> properties = map.get(id);
            Vertex vertex = graph.addVertex(properties.get("id").toString());
            for(String key : properties.keySet()) {
                if(properties.get(key) != null) vertex.property(key, properties.get(key));
            }
        }

        //set content for one vertex
        graph.vertices().next().property("dbsearch_content_t", Resources.toString(Resources.getResource("confluence.html"), Charset.forName("UTF-8")));
    }

}
