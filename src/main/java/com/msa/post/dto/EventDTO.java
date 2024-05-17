package com.msa.post.dto;

public class EventDTO {
    private String id;
    private String start;
    private String title;
    private String url;

    // Constructors, getters, and setters

    public EventDTO(String id, String start, String title, String url) {
        this.id = id;
        this.start = start;
        this.title = title;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getStart() {
        return start;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
