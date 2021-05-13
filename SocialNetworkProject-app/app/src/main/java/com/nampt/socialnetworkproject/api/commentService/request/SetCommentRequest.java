package com.nampt.socialnetworkproject.api.commentService.request;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class SetCommentRequest {

    @SerializedName("id_post")
    private int idPost;

    private String content;

    @SerializedName("create_time")
    private Date createTime;

    public SetCommentRequest(int idPost, String content, Date createTime) {
        this.idPost = idPost;
        this.content = content;
        this.createTime = createTime;
    }

    public SetCommentRequest() {
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
