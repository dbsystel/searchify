package de.db.searchify.service;

import de.db.searchify.model.Status;
import de.db.searchify.processor.HtmlToMetadataProcessor;
import de.db.searchify.processor.LinkBuilderProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 21.02.17.
 */
@Service
public class AsyncPipelineRunner {

    @Autowired
    HtmlToMetadataProcessor htmlToMetadataProcessor;

    @Autowired
    LinkBuilderProcessor linkBuilderProcessor;

    @Async
    public Future<Status> runAsync(Status status) throws InterruptedException {
        htmlToMetadataProcessor.run();
        status.setMessage("html preprocessed");
        linkBuilderProcessor.run();
        status.setMessage("links build");
        status.setMessage("Completed successfully");
        status.setState(Status.State.idle);
        return new AsyncResult<>(status);
    }

}
