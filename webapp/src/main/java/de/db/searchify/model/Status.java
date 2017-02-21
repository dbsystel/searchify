package de.db.searchify.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Kurz (thomas.kurz@redlink.co)
 * @since 21.02.17.
 */
public class Status {

    public enum State {
        running,idle
    }

    private State state;
    private String message;

    private List<String> log;

    public Status(State state, String message) {
        this.state = state;
        this.message = message;
        this.log = new ArrayList<>();
        log.add(message);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        log.add(message);
    }

    public List<String> getLog() {
        return log;
    }
}
