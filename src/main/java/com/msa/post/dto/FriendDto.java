package com.msa.post.dto;

public class FriendDto {
    String username;
    String friendName;

    public String getUsername() {
        return username;
    }

    public String getFriendName() {
        return friendName;
    }

    public FriendDto(String username, String friendName) {
        this.username = username;
        this.friendName = friendName;
    }
}
