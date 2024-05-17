package com.msa.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

public class PostDto {

    // Getters
    @Getter
    @Schema(description = "게시물 제목", defaultValue = "디폴트 제목")
    private String title;

    @Getter
    @Schema(description = "내용")
    private String content;

    @Getter
    @Schema(description = "작성자")
    public String username;

    @Schema(description = "id")
    public long postId;

    @Getter
    @Setter
    private String videoId;

    @Getter
    private String keyword; //검색어

    // Constructors
    public PostDto() {
        // Default constructor
    }

    public PostDto(String title, String content, String username, String videoId) {
        this.title = title;
        this.content = content;
        this.username = username;
        this.videoId = videoId;
    }

    // 비디오 아이디 추가 해야하는지 나중에 체크
    public PostDto(String title, String content, String username, long postId) {
        this.title = title;
        this.content = content;
        this.username = username;
        this.postId = postId;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}