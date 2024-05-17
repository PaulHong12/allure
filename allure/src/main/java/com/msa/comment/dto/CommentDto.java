package com.msa.comment.dto;

import lombok.Getter;

@Getter
public class CommentDto {
    private long id;
    private String content;
    @Getter
    private String nickName;

    // Constructors, getters, and setters

    // Getters and setters for id and content

    public void setId(long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

}