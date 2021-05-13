package com.example.nampt.controller;

import com.example.nampt.domain.request.CommentRequest;
import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.comment.SingleCommentResponse;
import com.example.nampt.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    ICommentService commentService;

    @CrossOrigin("*")
    @PostMapping("/set_comment")
    public ResponseEntity<SingleCommentResponse> addComment(@RequestHeader("Authorization") String token, @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.setComment(token, request.getIdPost(), request.getContent(),request.getCreateTime()));
    }

    @CrossOrigin("*")
    @PostMapping("/delete_comment/{id}")
    public ResponseEntity<BaseResponse> deleteComment(@RequestHeader("Authorization") String token, @PathVariable("id") int idComment) {
        return ResponseEntity.ok(commentService.deleteComment(token, idComment));
    }

    @CrossOrigin("*")
    @GetMapping("/get_list_comment/{idPost}")
    public ResponseEntity<BaseResponse> getCommentList(@RequestHeader("Authorization") String token, @PathVariable("idPost") int id) {
        return ResponseEntity.ok(commentService.getListComment(token, id));
    }
}
