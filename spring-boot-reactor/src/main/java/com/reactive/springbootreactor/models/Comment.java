package com.reactive.springbootreactor.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Comment {

    private List<String> commentList;

    public Comment() {
        this.commentList = new ArrayList<>();
    }

    public void addComment(String comment) {
        this.commentList.add(comment);
    }


}
