package com.reactive.springbootreactor;

import com.reactive.springbootreactor.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootReactorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("### Starting Spring ###");
        this.mergeTwoFlux();

    }

    public User createUser() {
        return new User("Jonh","Doe");
    }

    public void mergeTwoFlux() {
        Mono<User> userMono = Mono.fromCallable(()-> createUser());
    }

    public void convertFluxToMono() {
        // The goal of this method is to create a Mono list from Flux.
        List<User> userList = new ArrayList<>();
        userList.add(new User("John", "Rambo"));
        userList.add(new User("Mary", "Popins"));
        userList.add(new User("Paul", "Montana"));
        userList.add(new User("Paul", "Gasol"));
        userList.add(new User("Sofia", "Vergara"));

        Flux.fromIterable(userList)
                // collectList transforms the iterable Flux to Mono list
                .collectList()
                .subscribe(list -> {
                    list.forEach(item ->log.info("User: " + item.toString()));
                });

    }

    public void convertToString() {
        // The goal of this method is to create a string list from object.
        List<User> userList = new ArrayList<>();
        userList.add(new User("John", "Rambo"));
        userList.add(new User("Mary", "Popins"));
        userList.add(new User("Paul", "Montana"));
        userList.add(new User("Paul", "Gasol"));
        userList.add(new User("Sofia", "Vergara"));

        // Flux is the publisher.
        Flux.fromIterable(userList)
                .map(user -> user.getName().toUpperCase().concat(" ").concat(user.getSurname().toUpperCase()))
                // Flatmap creates a new flux, and then it returns a Mono
                .flatMap(name -> {
                    if (name.contains("PAUL")) {
                        return Mono.just(name);
                    } else {
                        return Mono.empty();
                    }

                })
                .map(name -> {
                    // This example shows as we can change an element of the flux (set name to lower case)
                    return name.toLowerCase();
                })
                // Creates a subscription and it shows the names. This is an observer and is executed at the same time of the doOnNext.
                .subscribe(element -> log.info("subscribe method --> " + element));
    }

    public void flatMapSample() {
        // The goal of this method is to create a new object (user) from a string array of values
        List<String> userList = new ArrayList<String>();
        userList.add("John Rambo");
        userList.add("Mary Popins");
        userList.add("Paul Montana");
        userList.add("Sofia Vergara");

        // Flux is the publisher.
        Flux.fromIterable(userList)
                .map(name -> new User(name.split(" ")[0].toUpperCase(), name.split(" ")[1].toUpperCase()))
                // Flatmap creates a new flux, and then it returns a Mono
                .flatMap(user -> {
                    if (user.getName().equalsIgnoreCase("sofia")) {
                        return Mono.just(user);
                    } else {
                        return Mono.empty();
                    }

                })
                .map(user -> {
                    // This example shows as we can change an element of the flux (set name to lower case)
                    String name = user.getName().toLowerCase();
                    user.setName(name);
                    return user;
                })
                // Creates a subscription and it shows the names. This is an observer and is executed at the same time of the doOnNext.
                .subscribe(element -> log.info("subscribe method --> " + element));
    }

    public void iteratorSample() {
        List<String> userList = new ArrayList<String>();
        userList.add("John Rambo");
        userList.add("Mary Popins");
        userList.add("Paul Montana");
        userList.add("Sofia Vergara");

        // Flux is the publisher.
        Flux<String> names = Flux.fromIterable(userList);
        // Transforms the names and returns a new instance without modifying the original
        // "Flux<String> names" is immutable
        // Map always returns a value
        Flux<User> users = names.map(name -> new User(name.split(" ")[0].toUpperCase(), name.split(" ")[1]))
                .filter(name -> {
                    return name.getName().toLowerCase().equals("sofia");
                })
                // Iterates for each name.
                .doOnNext(user -> {
                    if (user == null) {
                        throw new RuntimeException("The value cannot be empty");
                    }
                    {
                        // And shows the corresponding value
                        System.out.println("onNext method --> " + user.getName().concat(" ").concat(user.getSurname()));

                    }
                    {
                        // Other methods
                    }
                })
                .map(user -> {
                    String name = user.getName().toLowerCase();
                    user.setName(name);
                    return user;
                });

        // Creates a subscription and it shows the names. This is an observer and is executed at the same time of the doOnNext.
        users.subscribe(element -> log.info("subscribe method --> " + element),
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
