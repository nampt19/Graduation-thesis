package com.example.nampt.controller;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    INotificationService notificationService;

    @CrossOrigin("*")
    @PostMapping("/set_notify")
    public ResponseEntity<BaseResponse> setNotify(@RequestHeader("Authorization") String token,
                                                  @RequestParam("type") int type,
                                                  @RequestParam("partnerId") int partnerId,
                                                  @RequestParam("roomId") int roomId,
                                                  @RequestParam("content") String content) {
        return ResponseEntity.ok(notificationService.setNotify(token, type, partnerId, roomId,content));
    }

    @CrossOrigin("*")
    @PostMapping("/set_seen/{notifyId}")
    public ResponseEntity<BaseResponse> setSeen(@RequestHeader("Authorization") String token,
                                                @PathVariable("notifyId") int notifyId) {
        return ResponseEntity.ok(notificationService.setSeen(token, notifyId));
    }

    @CrossOrigin("*")
    @PostMapping("/del_notify/{notifyId}")
    public ResponseEntity<BaseResponse> delNotification(@RequestHeader("Authorization") String token,
                                                        @PathVariable("notifyId") int notifyId) {
        return ResponseEntity.ok(notificationService.delNotification(token, notifyId));
    }

    @CrossOrigin("*")
    @GetMapping("/get_list_notify")
    public ResponseEntity<BaseResponse> getListNotification(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(notificationService.getListNotification(token));
    }

    @CrossOrigin("*")
    @GetMapping("/get_total_notify")
    public ResponseEntity<BaseResponse> getTotalNotification(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(notificationService.getTotalNotification(token));
    }
}
