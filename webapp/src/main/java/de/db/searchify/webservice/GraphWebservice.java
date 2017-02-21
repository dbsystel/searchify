package de.db.searchify.webservice;

import de.db.searchify.service.SerialisationService;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.structure.io.graphml.GraphMLWriter;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 21.02.17.
 */
@RestController
@RequestMapping("/graph")
public class GraphWebservice {

    @Autowired
    SerialisationService serialisationService;

    @RequestMapping("/export/gml")
    public void getGraph(HttpServletResponse response) throws IOException {

        // Get your file stream from wherever.
        InputStream myStream = new ByteArrayInputStream(serialisationService.serialize().getBytes());

        // Set the content type and attachment header.
        response.addHeader("Content-disposition", "attachment;filename=graph.gml");
        response.setContentType("txt/plain");

        // Copy the stream to the response's output stream.
        IOUtils.copy(myStream, response.getOutputStream());
        response.flushBuffer();
    }

}
