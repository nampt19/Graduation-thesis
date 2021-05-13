package com.example.nampt.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class CommentRequest {
    private int idPost;
    private String content;
    private Date createTime;


    @JsonProperty("id_post")
    public int getIdPost() {
        return idPost;
    }

    public void setIdPost(int idPost) {
        this.idPost = idPost;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
