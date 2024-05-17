package com.msa.chat.controller;

import com.msa.chat.dto.ChatDTO;
import com.msa.chat.dto.ChatRoom;
import com.msa.chat.dto.RoomRequest;
import com.msa.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.msa.chat.dto.ChatDTO.MessageType.TALK;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRepository chatRepository;
    private final SimpMessageSendingOperations template; //특정 Broker로 메세지를 전달
    private final ChatRepository repository;

    // 해당 채팅방 채팅내역 표시
    @PostMapping("/chat/chatlist")
    public ModelAndView goChatRoom(@RequestBody RoomRequest roomRequest, ModelAndView mav){
        //1. 조회
        List<ChatDTO> list = chatRepository.findRoomChatting(roomRequest.getRoomId());

        //return
        mav.addObject("list", list);
        mav.setViewName("jsonView");
        return mav;
    }

    // 채팅방 생성
    // RoomRequest에 roomName과 roomId를
    // a유저네임+b유저네임 (알파벳순으로 누가 먼저올지 정하기) 으로 하기! <- 여기서 구현됨!!
    // 그리고 친구목록 view에서, dm보내기 버튼 누를시, userA와 userB 넘겨주기
    @PostMapping("/chat/createroom")
    public ResponseEntity<?> createRoom(@RequestBody RoomRequest roomRequest) {
        // Existing logic to sort usernames and create a chat room
        String sortedConcatenatedNames = Stream.of(roomRequest.getUserA(), roomRequest.getUserB())
                .sorted()
                .collect(Collectors.joining());
        ChatRoom room = chatRepository.createChatRoom(sortedConcatenatedNames, sortedConcatenatedNames);
        log.info("CREATE Chat Room {}", room);

        // Assuming addUser returns a UUID for the user in the chat room
        String userUUID = repository.addUser(room.getRoomId(), roomRequest.getUserA());

        // Construct entrance message and send
        ChatDTO chat = new ChatDTO();
        chat.setType(TALK);
        chat.setRoomId(room.getRoomId());
        chat.setSender(roomRequest.getUserA());
        //chat.setTime(LocalDate.now().toString());
        chat.setMessage(roomRequest.getUserA() + " 님 입장!!");
        repository.saveMsg(chat);
        template.convertAndSend("/sub/chat/room/" + room.getRoomId(), chat);

        // Return response with room ID and user UUID
        Map<String, String> response = new HashMap<>();
        response.put("roomId", room.getRoomId());
        response.put("userUUID", userUUID);
        //mav.setViewName("chat"); // Adjust view name as per your application's need
        return ResponseEntity.ok(response);
    }

    // 채팅에 참여한 유저 리스트 반환
    @PostMapping("/chat/userlist")
    public ModelAndView userList(@RequestBody RoomRequest roomRequest, ModelAndView mav) {
        //1. 조회
        ArrayList<String> userList = chatRepository.getUserList(roomRequest.getRoomId());

        //return
        mav.addObject("userList", userList);
        mav.setViewName("jsonView");
        return mav;
    }
}
