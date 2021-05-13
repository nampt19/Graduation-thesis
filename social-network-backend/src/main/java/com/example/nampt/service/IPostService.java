package com.example.nampt.service;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.LikeResponse;
import com.example.nampt.domain.response.post.AddPostResponse;
import com.example.nampt.domain.response.post.ListPostResponse;
import com.example.nampt.domain.response.post.SinglePostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public interface IPostService {
    AddPostResponse addPost(String token, MultipartFile[] mediaFile, String content, Date createTime) ;
    BaseResponse deletePost(String token,int postId);
    BaseResponse editPost(String token, int postId,String content);
    SinglePostResponse getPost(String token, int id);
    ListPostResponse getListPost(String token, int index, int count,int lastPostId);
    BaseResponse hiddenPost(String token, int postId);
    LikeResponse like(String token,int postId);

    ListPostResponse getListPostByUserId(String token, int userId);

}
