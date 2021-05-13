package com.example.nampt.controller;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.service.IDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    IDeviceService deviceService;
    @CrossOrigin("*")
    @PostMapping("/save_token")
    public ResponseEntity<BaseResponse> saveTokenFcm(@RequestHeader("Authorization") String token,
                                                     @RequestParam("userId") int userId,
                                                     @RequestParam("token") String deviceToken) {

        return ResponseEntity.ok(deviceService.saveToken(token, userId,deviceToken));
    }
}
