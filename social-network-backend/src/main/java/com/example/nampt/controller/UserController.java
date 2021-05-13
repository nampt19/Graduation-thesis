package com.example.nampt.controller;

import com.example.nampt.domain.request.RegisterRequest;
import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.login.LoginResponse;
import com.example.nampt.domain.response.search.DataPeopleSearch;
import com.example.nampt.domain.response.search.ListPeopleResponse;
import com.example.nampt.domain.response.search.PeopleResponse;
import com.example.nampt.domain.response.user.AlbumResponse;
import com.example.nampt.domain.response.user.ProfileUserResponse;
import com.example.nampt.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    public IUserService userService;

    @CrossOrigin("*")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> signup(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.signup(request.getUsername().trim(), request.getPhone(), request.getPassword()));
    }

    @CrossOrigin("*")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.login(request.getPhone(), request.getPassword()));
    }

    @CrossOrigin("*")
    @PostMapping("/logout/{token}")
    public ResponseEntity<BaseResponse> logout(@PathVariable String token) {
        return ResponseEntity.ok(userService.logout(token));
    }

    @CrossOrigin("*")
    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileUserResponse> getProfile(@RequestHeader("Authorization") String token
            , @PathVariable("id") int userId) {
        return ResponseEntity.ok(userService.getProfile(token, userId));
    }

    @CrossOrigin("*")
    @PostMapping("/set_banner")
    public ResponseEntity<BaseResponse> setBanner(@RequestHeader("Authorization") String token,
                                                  @RequestParam MultipartFile file) {
        return ResponseEntity.ok(userService.setBanner(token, file));
    }

    @CrossOrigin("*")
    @PostMapping("/set_avatar")
    public ResponseEntity<BaseResponse> setAvatar(@RequestHeader("Authorization") String token,
                                                  @RequestParam MultipartFile file) {
        return ResponseEntity.ok(userService.setAvatar(token, file));
    }

    @CrossOrigin("*")
    @PostMapping("/set_profile")
    public ResponseEntity<BaseResponse> setProfile(@RequestHeader("Authorization") String token,
                                                   @RequestParam(name = "school") String school,
                                                   @RequestParam(name = "address") String address) {
        return ResponseEntity.ok(userService.setProfile(token, school, address));
    }

    @CrossOrigin("*")
    @PostMapping("/search/{name}")
    public ResponseEntity<ListPeopleResponse> search(@RequestHeader("Authorization") String token,
                                                     @PathVariable("name") String name) {
        return ResponseEntity.ok(userService.searchByName(token, name));
    }

    @CrossOrigin("*")
    @PostMapping("/change_password")
    public ResponseEntity<BaseResponse> changePassword(@RequestHeader("Authorization") String token,
                                                       @RequestParam(name = "newPass") String newPass,
                                                       @RequestParam(name = "oldPass") String oldPass) {
        return ResponseEntity.ok(userService.changePassword(token, newPass, oldPass));
    }

    @CrossOrigin("*")
    @GetMapping("/get_type/{userId}")
    public ResponseEntity<PeopleResponse> getType(@RequestHeader("Authorization") String token,
                                                  @PathVariable("userId") int userId) {
        return ResponseEntity.ok(userService.getType(token, userId));
    }

    @CrossOrigin("*")
    @GetMapping("/get_block/{userId}")
    public ResponseEntity<BaseResponse> getBlock(@RequestHeader("Authorization") String token,
                                                  @PathVariable("userId") int userId) {
        return ResponseEntity.ok(userService.getBlock(token, userId));
    }

    @CrossOrigin("*")
    @GetMapping("/get_album/{userId}")
    public ResponseEntity<AlbumResponse> getAlbum(@RequestHeader("Authorization") String token,
                                                  @PathVariable("userId") int userId) {
        return ResponseEntity.ok(userService.getAlbum(token, userId));
    }

    @CrossOrigin("*")
    @PostMapping("/set_name/{name}")
    public ResponseEntity<BaseResponse> setName(@RequestHeader("Authorization") String token,
                                                  @PathVariable("name") String name) {
        return ResponseEntity.ok(userService.setName(token, name.trim()));
    }

}