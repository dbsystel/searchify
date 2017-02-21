package de.db.searchify.service;

import com.google.common.io.Resources;
import de.db.searchify.processor.HtmlToMetadataProcessor;
import junit.framework.TestCase;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 20.02.17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HtmlToMetadataProcessorTest extends TestCase {

    private String BODY = "dbsearch_content_t";

    @Autowired
    HtmlToMetadataProcessor processor;

    @MockBean
    Graph graph;

    @Mock
    Vertex vertex;

    @Mock
    VertexProperty property;

    @Test
    public void testParsing() throws IOException {

        given(vertex.property(eq(BODY))).willReturn(property);
        given(property.isPresent()).willReturn(true);
        given(property.value()).willReturn(Resources.toString(Resources.getResource("confluence.html"), Charset.forName("UTF-8")));
        given(graph.vertices()).willReturn(Collections.singleton(vertex).iterator());
        given(graph.vertices(anyString(),anyString())).willReturn(Collections.singleton(vertex).iterator());

        processor.run();

        verify(graph).addVertex(
                "id", "https://expertfinder.db.redlink.io/confluence/display/EF/CompactCRM",
                T.label, "https://expertfinder.db.redlink.io/confluence/display/EF/CompactCRM");
    }

}