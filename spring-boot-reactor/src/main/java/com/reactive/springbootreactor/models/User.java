package com.reactive.springbootreactor.models;

import lombok.Data;

@Data
public class User {
    private String name;
    private String surname;

    public User(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }
}
