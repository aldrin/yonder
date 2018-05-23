package co.aldrin.nobel.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Entry point
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
@EnableRetry
@SpringBootApplication
@EnableMongoRepositories
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}
