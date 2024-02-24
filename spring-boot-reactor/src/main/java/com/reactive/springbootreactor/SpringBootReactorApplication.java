package com.reactive.springbootreactor;

import com.reactive.springbootreactor.models.Comment;
import com.reactive.springbootreactor.models.User;
import com.reactive.springbootreactor.models.UserComments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
        this.delayElement();

    }

    public void delayElement() {
        // This sample creates a Flux and applies a delay
        Flux.range(1, 12)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(i -> log.info(i.toString()))
                .blockLast();
    }

    public void intervalSample() {
        // This sample creates two flux and merge both creating sequence
        Flux<Integer> range = Flux.range(1, 12);
        Flux<Long> delay = Flux.interval(Duration.ofSeconds(1));

        range.zipWith(delay, (rangeElement, delayElement) -> rangeElement)
                .doOnNext(i -> log.info(i.toString()))
                .blockLast(); // block shows the sequence in the console
    }

    public void zipWithRangesSample() {
        // This sample has two flux and create a new one with the merge of both
        Flux.just(1, 2, 3, 4)
                .map(i -> (i * 2))
                .zipWith(Flux.range(0, 4), (firstFlux, secondFlux) -> String.format("First flux: %d, Second flux: %d", firstFlux, secondFlux))
                .subscribe(e -> log.info(e));
    }

    public void mergeTwoFluxZipWith() {
        // It converts two mono objects in a new flux merging both
        // fromCallable creates a new user calling createUser
        Mono<User> userMono = Mono.fromCallable(() -> {
            return new User("John", "Doe");
        });
        Mono<Comment> commentMono = Mono.fromCallable(() -> {

            Comment comment = new Comment();
            comment.addComment("This is an example comment");
            comment.addComment("It shows the merge of two fluxes");
            comment.addComment("Using fromCallable to create a user and the comment");
            return comment;
        });

        // Now we crate the withZip to merge both and covert to userComment
        Mono<UserComments> userCommentsMono = userMono.zipWith(commentMono, UserComments::new);
        userCommentsMono.subscribe(e -> log.info(e.toString()));

        // The same as above but using tuples
        Mono<UserComments> userCommentsMonoTuple = userMono.zipWith(commentMono)
                .map(tupleElement -> {
                    User u = tupleElement.getT1();
                    Comment c = tupleElement.getT2();
                    return new UserComments(u, c);
                });
        userCommentsMonoTuple.subscribe(e -> log.info("Tuple --> " + e.toString()));

    }

    public void mergeTwoFluxFlatMap() {
        // It converts two mono objects in a new flux merging both
        // fromCallable creates a new user calling createUser
        Mono<User> userMono = Mono.fromCallable(() -> {
            return new User("John", "Doe");
        });
        Mono<Comment> commentMono = Mono.fromCallable(() -> {

            Comment comment = new Comment();
            comment.addComment("This is an example comment");
            comment.addComment("It shows the merge of two fluxes");
            comment.addComment("Using fromCallable to create a user and the comment");
            return comment;
        });

        // Now we crate the flatmap to merge both and covert to userComment
        userMono.flatMap(userElement -> commentMono.map(commentElement -> new UserComments(userElement, commentElement)))
                .subscribe(userComments -> log.info(userComments.toString()));

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
                    list.forEach(item -> log.info("User: " + item.toString()));
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
