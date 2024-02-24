package com.reactive.springbootreactor.models;

import lombok.Data;

@Data
public class UserComments {

    private User user;
    private Comment post;

    public UserComments(User user, Comment post) {
        this.user = user;
        this.post = post;
    }

}
