package com.msa.Youtube.controller;

import com.msa.Youtube.dto.YoutubeDto;
import com.msa.Youtube.service.YoutubeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Controller
public class YoutubeController {
    private YoutubeService youtubeService;
    String id = null;

    public YoutubeController(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;
    }

    @RequestMapping
    public String SearchYoutube(@RequestParam(value="word") String search) {
        try {
            YoutubeDto item;
            item = youtubeService.YoutubeSearch(search);

            id = item.getVideoId();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

}
