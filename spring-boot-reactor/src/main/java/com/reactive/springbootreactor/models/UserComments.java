package com.reactive.springbootreactor.models;
public class UserComments {

    private User user;
    private Post post;

    public UserComments(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    @Override
    public String toString() {
        return "UserComments{" +
                "user=" + user +
                ", post=" + post +
                '}';
    }
}
