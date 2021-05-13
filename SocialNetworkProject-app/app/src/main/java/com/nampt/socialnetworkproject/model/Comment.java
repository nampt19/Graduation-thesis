package com.nampt.socialnetworkproject.model;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
    private int id;
    private int postId;
    private String content;
    private Date createTime;
    private int authorId;
    private String authorName;
    private String linkAvatar;
    private boolean isBlock;


    public Comment() {
    }

    public Comment(int id) {
        this.id = id;
    }

    public Comment(String linkAvatar, String authorName, String content, Date createTime) {
        this.content = content;
        this.createTime = createTime;
        this.authorName = authorName;
        this.linkAvatar = linkAvatar;
    }

    public Comment(int id, int postId, String content, Date createTime, int authorId, String authorName, String linkAvatar, boolean isBlock) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.createTime = createTime;
        this.authorId = authorId;
        this.authorName = authorName;
        this.linkAvatar = linkAvatar;
        this.isBlock = isBlock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
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

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }
}
