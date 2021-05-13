package com.example.nampt.controller;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.friend.ListFriendResponse;
import com.example.nampt.service.IFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friend")
public class FriendController {
    @Autowired
    IFriendService friendService;

    @CrossOrigin("*")
    @GetMapping("/get_list_friends")
    public ResponseEntity<ListFriendResponse> getListFriends(@RequestHeader("Authorization") String token,
                                                             @RequestParam(name = "index") int index,
                                                             @RequestParam(name = "count") int count) {
        return ResponseEntity.ok(friendService.getFriendsList(token, index, count));
    }

    @CrossOrigin("*")
    @GetMapping("/get_list_friends_online")
    public ResponseEntity<ListFriendResponse> getListFriendsOnline(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(friendService.getFriendsListOnline(token));
    }

    @CrossOrigin("*")
    @GetMapping("/get_requested_friends")
    public ResponseEntity<ListFriendResponse> getListRequestFriends(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(friendService.getRequestFriendsList(token));
    }

    @CrossOrigin("*")
    @GetMapping("/get_list_invitation_sent_friends")
    public ResponseEntity<ListFriendResponse> getListInvitationSentFriends(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(friendService.getInvitationSentFriendList(token));
    }

    @CrossOrigin("*")
    @PostMapping("/set_request_friend/{id}")
    public ResponseEntity<BaseResponse> setRequestFriend(@RequestHeader("Authorization") String token, @PathVariable("id") int userId) {
        return ResponseEntity.ok(friendService.setRequestFriend(token, userId));
    }

    @CrossOrigin("*")
    @PostMapping("/set_accept_friend")
    public ResponseEntity<BaseResponse> setAcceptFriend(@RequestHeader("Authorization") String token, @RequestParam("sender_id") int senderId, @RequestParam("is_accept") int isAccept) {
        return ResponseEntity.ok(friendService.setAcceptFriend(token, senderId, isAccept));
    }

    @CrossOrigin("*")
    @PostMapping("/delete_invitation_sent_friend/{id}")
    public ResponseEntity<BaseResponse> delInvitationSentFriend(@RequestHeader("Authorization") String token, @PathVariable("id") int userId) {
        return ResponseEntity.ok(friendService.deleteInvitationSentFriend(token, userId));
    }
    @CrossOrigin("*")
    @PostMapping("/delete_friend/{id}")
    public ResponseEntity<BaseResponse> delFriend(@RequestHeader("Authorization") String token, @PathVariable("id") int userId) {
        return ResponseEntity.ok(friendService.deleteFriend(token, userId));
    }

    @CrossOrigin("*")
    @PostMapping("/block_user/{id}")
    public ResponseEntity<BaseResponse> blockUser(@RequestHeader("Authorization") String token, @PathVariable("id") int userId) {
        return ResponseEntity.ok(friendService.blockUser(token, userId));
    }

    @CrossOrigin("*")
    @PostMapping("/unblock_user/{id}")
    public ResponseEntity<BaseResponse> unBlockUser(@RequestHeader("Authorization") String token, @PathVariable("id") int userId) {
        return ResponseEntity.ok(friendService.unBlockUser(token,userId));
    }

    @CrossOrigin("*")
    @GetMapping("/get_block_list")
    public ResponseEntity<BaseResponse> getBlockList(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(friendService.getBlockList(token));
    }

}
