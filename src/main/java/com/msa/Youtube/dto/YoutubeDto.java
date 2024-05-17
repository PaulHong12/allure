package com.msa.Youtube.dto;

import lombok.Getter;

@Getter
public class YoutubeDto {
    private String title;
    private String videoId;

    public YoutubeDto(String title, String videoId) {
        this.title = title;
        this.videoId = videoId;
    }

}
