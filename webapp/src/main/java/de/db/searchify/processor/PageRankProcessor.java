package de.db.searchify.processor;

import de.db.searchify.api.Processor;
import org.apache.tinkerpop.gremlin.process.computer.ComputerResult;
import org.apache.tinkerpop.gremlin.process.computer.ranking.pagerank.PageRankVertexProgram;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 22.02.17.
 */
@Service
public class PageRankProcessor implements Processor {

    @Autowired
    Graph graph;

    @Override
    public void run() {
        try {
            ComputerResult result = graph.compute().program(PageRankVertexProgram.build().create(graph)).submit().get();

            //merge results
            result.graph().vertices().forEachRemaining(vertex -> {
                graph.vertices(vertex.id()).next().property("dbsearch_pagerank_d", vertex.property(PageRankVertexProgram.PAGE_RANK).value());
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
