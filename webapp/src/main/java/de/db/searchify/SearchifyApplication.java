package de.db.searchify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;

@SpringBootApplication
public class SearchifyApplication {

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(SearchifyApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }
}
