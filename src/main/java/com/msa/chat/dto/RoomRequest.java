package com.msa.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoomRequest {
    private String userA;
    private String userB;
    private String roomId; //방 고유키
    private String roomName; //방이름
}
