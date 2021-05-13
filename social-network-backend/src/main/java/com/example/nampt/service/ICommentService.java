package com.example.nampt.service;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.comment.ListCommentResponse;
import com.example.nampt.domain.response.comment.SingleCommentResponse;

import java.util.Date;

public interface ICommentService  {
    SingleCommentResponse setComment(String token, int idPost, String comment, Date createTime);

    BaseResponse deleteComment(String token, int idComment);

    ListCommentResponse getListComment(String token,int idPost);
}
