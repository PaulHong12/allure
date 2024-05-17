package com.msa.post.dto;

public record commentDto(int postId, String content) {
    public Object getContent() {
        return content;
    }

}
