package de.db.searchify.service;

import de.db.searchify.model.Status;
import de.db.searchify.processor.HtmlToMetadataProcessor;
import de.db.searchify.processor.LinkBuilderProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 21.02.17.
 */
@Service
public class PipelineService {

    private Future<Status> future;

    private Status status;

    @Autowired
    AsyncPipelineRunner pipelineRunner;

    public Status run() throws InterruptedException {
        if(future == null || (future.isDone() || future.isCancelled())) {
            status = new Status(Status.State.running, "Started successfully");
            future = pipelineRunner.runAsync(status);
            return status;
        } else {
            return new Status(Status.State.running, "Process is already running");
        }
    }

    public Status status() throws ExecutionException, InterruptedException {
        if(future == null) {
            return new Status(Status.State.idle, "Not yet started");
        }



        if(future.isCancelled() || future.isDone()) {
            try {
                status = future.get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new InterruptedException(e.getLocalizedMessage());
            }
        }

        return status;
    }
}
