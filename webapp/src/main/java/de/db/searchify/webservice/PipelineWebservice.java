package de.db.searchify.webservice;

import de.db.searchify.model.Status;
import de.db.searchify.service.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 21.02.17.
 */
@RestController
@RequestMapping("/pipeline")
public class PipelineWebservice {

    @Autowired
    PipelineService pipelineService;

    @RequestMapping("/run")
    public Status run() throws InterruptedException {
        return pipelineService.run();
    }

    @RequestMapping("/status")
    public Status status() throws ExecutionException, InterruptedException {
        return pipelineService.status();
    }

}
