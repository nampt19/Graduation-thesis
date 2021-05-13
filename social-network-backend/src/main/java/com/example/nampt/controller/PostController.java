package com.example.nampt.controller;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.post.ListPostResponse;
import com.example.nampt.entity.Post;
import com.example.nampt.service.IPostService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    IPostService postService;
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @CrossOrigin("*")
    @PostMapping("/add_post")
    public ResponseEntity<BaseResponse> addPost(@RequestHeader("Authorization") String token,
                                                @RequestParam MultipartFile[] file,
                                                @RequestParam String content,
                                                @RequestParam("create_time") String createTime) {
        return ResponseEntity.ok(postService.addPost(token,
                file, content, gson.fromJson(createTime, Date.class)));
    }

    @CrossOrigin("*")
    @PostMapping("/delete_post/{idPost}")
    public ResponseEntity<BaseResponse> deletePost(@RequestHeader("Authorization") String token, @PathVariable int idPost) {
        return ResponseEntity.ok(postService.deletePost(token, idPost));
    }

    @CrossOrigin("*")
    @PostMapping("/edit_post")
    public ResponseEntity<BaseResponse> editPost(@RequestHeader("Authorization") String token, @RequestBody Post post) {
        return ResponseEntity.ok(postService.editPost(token, post.getId(), post.getContent()));
    }

    @CrossOrigin("*")
    @GetMapping("/get_post/{idPost}")
    public ResponseEntity<BaseResponse> getPost(@RequestHeader("Authorization") String token, @PathVariable int idPost) {
        return ResponseEntity.ok(postService.getPost(token, idPost));
    }

    @CrossOrigin("*")
    @GetMapping("/get_list_posts")
    public ResponseEntity<ListPostResponse> getListPost(@RequestHeader("Authorization") String token,
                                                        @RequestParam(name = "index") int index,
                                                        @RequestParam(name = "count") int count,
                                                        @RequestParam(name = "last_id") int lastPostId) {
        return ResponseEntity.ok(postService.getListPost(token, index, count, lastPostId));
    }

    @CrossOrigin("*")
    @GetMapping("/get_posts/{id}")
    public ResponseEntity<ListPostResponse> getListPostByPosterId(@RequestHeader("Authorization") String token,
                                                                  @PathVariable("id") int idPost) {
        return ResponseEntity.ok(postService.getListPostByUserId(token, idPost));
    }

    @CrossOrigin("*")
    @PostMapping("/hidden_post/{id}")
    public ResponseEntity<BaseResponse> hiddenPost(@RequestHeader("Authorization") String token, @PathVariable("id") int idPost) {
        return ResponseEntity.ok(postService.hiddenPost(token, idPost));
    }

    @CrossOrigin("*")
    @PostMapping("/like/{idPost}")
    public ResponseEntity<BaseResponse> likePost(@RequestHeader("Authorization") String token, @PathVariable("idPost") int idPost) {
        return ResponseEntity.ok(postService.like(token, idPost));
    }


}
