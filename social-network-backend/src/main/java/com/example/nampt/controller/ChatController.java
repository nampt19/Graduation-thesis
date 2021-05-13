package com.example.nampt.controller;

import com.example.nampt.domain.request.CreateGroupChatRequest;
import com.example.nampt.domain.request.GroupChatRequest;
import com.example.nampt.domain.request.SingleChatRequest;
import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.chat.*;
import com.example.nampt.service.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    IChatService chatService;

    @CrossOrigin("*")
    @PostMapping("/set_message")
    public ResponseEntity<SingleChatResponse> addMessageSingle(@RequestHeader("Authorization") String token, @RequestBody SingleChatRequest request) {
        return ResponseEntity.ok(chatService.setMessageSingle(token,
                request.getContent(),
                request.getPartnerId(),
                request.getCreateTime()));
    }

    @CrossOrigin("*")
    @PostMapping("/create_group")
    public ResponseEntity<GroupChatResponse> createGroupChat(@RequestHeader("Authorization") String token, @RequestBody CreateGroupChatRequest request) {
        return ResponseEntity.ok(chatService.createGroupChat(token, request.getPartnerIds(), request.getName().trim(),request.getCreateTime()));
    }

    @CrossOrigin("*")
    @PostMapping("/send_message")
    public ResponseEntity<BaseResponse> sendMessGroupChat(@RequestHeader("Authorization") String token, @RequestBody GroupChatRequest request) {
        return ResponseEntity.ok(chatService.sendMessageGroup(token, request.getConversationId(), request.getContent(),request.getCreateTime()));
    }


    @CrossOrigin("*")
    @GetMapping("/get_list_conversation")
    public ResponseEntity<ListConversationResponse> getListConversation(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(chatService.getListConversation(token));
    }

    @CrossOrigin("*")
    @GetMapping("/get_conversation/{id}")
    public ResponseEntity<ListMessageResponse> getConversation(@RequestHeader("Authorization") String token,@PathVariable("id") int idRoom) {
        return ResponseEntity.ok(chatService.getConversation(token,idRoom));
    }

    @CrossOrigin("*")
    @GetMapping("/get_conversation_single/{id}")
    public ResponseEntity<ListMessageResponse> getConversationSingle(@RequestHeader("Authorization") String token,@PathVariable("id") int partnerId) {
        return ResponseEntity.ok(chatService.getConversationSingle(token,partnerId));
    }


    @CrossOrigin("*")
    @PostMapping("/search/{name}")
    public ResponseEntity<ListConversationResponse> search(@RequestHeader("Authorization") String token,
                                                           @PathVariable("name") String name) {
        return ResponseEntity.ok(chatService.searchByName(token,name));
    }

    @CrossOrigin("*")
    @PostMapping("/set_seen_by_room_id/{roomId}")
    public ResponseEntity<BaseResponse> setSeenByRoomId(@RequestHeader("Authorization") String token,
                                                @PathVariable("roomId") int roomId) {
        return ResponseEntity.ok(chatService.setSeenByRoomId(token,roomId));
    }

    @CrossOrigin("*")
    @PostMapping("/set_seen_single/{partnerId}")
    public ResponseEntity<BaseResponse> setSeenSingle(@RequestHeader("Authorization") String token,
                                                @PathVariable("partnerId") int partnerId) {
        return ResponseEntity.ok(chatService.setSeenSingle(token,partnerId));
    }
}
