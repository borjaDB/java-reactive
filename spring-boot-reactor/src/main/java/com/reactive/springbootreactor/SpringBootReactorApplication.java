package com.reactive.springbootreactor;

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
        System.out.println("### Starting Spring ###");

        // Flux is the publisher.
        // You can try adding empty values and check the result.
        Flux<String> names = Flux.just("John", "Mary", "Paul", "Sofia")
                // Iterates for each name
                .doOnNext(element -> {
                    if(element.isEmpty()){
                        throw new RuntimeException("The value cannot be empty");
                    }{
                        System.out.println(element);

                    }{
                        // Other methods
                    }
                });

        // Creates a subscription and it shows the names. This is an observer and is executed at the same time of doOnNext.
        names.subscribe(element -> log.info(element),
                error -> log.error(error.getMessage()),
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
