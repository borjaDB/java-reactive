package com.reactive.springbootreactor;

import com.reactive.springbootreactor.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootReactorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("### Starting Spring ###");

        // Flux is the publisher.
        Flux<User> names = Flux.just("John", "Mary", "Paul", "Sofia")
                // Transforms the names and returns a new instance without modifying the original
                // "Flux<String> names" is immutable
                // Map always returns a value
                .map(name -> new User(name.toUpperCase(),null))
                // Iterates for each name.
                .doOnNext(user -> {
                    if(user == null){
                        throw new RuntimeException("The value cannot be empty");
                    }{
                        // And shows the corresponding value
                        System.out.println("onNext method --> " + user.getName());

                    }{
                        // Other methods
                    }
                })
                .map(user -> {
                    String name = user.getName().toLowerCase();
                    user.setName(name);
                    return user;
                });

        // Creates a subscription and it shows the names. This is an observer and is executed at the same time of the doOnNext.
        names.subscribe(element -> log.info("subscribe method --> " + element.getName()),
                error -> log.error(error.getMessage()),
                // You can replace by a lambda expression
                new Runnable() {
                    @Override
                    public void run() {
                        // Only enter this method if the execution is complete (without failures)
                        log.info("The observer has finished correctly");
                    }
                }
        );
    }
}
