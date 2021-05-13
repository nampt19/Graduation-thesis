package com.nampt.socialnetworkproject.model;

import java.io.Serializable;
import java.util.Date;

public class Post implements Serializable {
    private int id;

    private String content;

    private String linkImage1;

    private String linkImage2;

    private String linkImage3;

    private String linkImage4;

    private String linkVideo;

    private Date createTime;

    private int totalLike;

    private int totalComment;

    private boolean isLiked; // kiểm tra xem mình đã like chưa !

    private boolean isBlock; // kiểm tra xem mình có bị tác giả chặn không !

    private boolean isHidden;

    private int authorId;     // nếu tác giả chính là mình thì có thể delete + edit được !

    private String authorName;

    private String linkAvatar;

    public Post() {
    }

    public Post(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinkImage1() {
        return linkImage1;
    }

    public void setLinkImage1(String linkImage1) {
        this.linkImage1 = linkImage1;
    }

    public String getLinkImage2() {
        return linkImage2;
    }

    public void setLinkImage2(String linkImage2) {
        this.linkImage2 = linkImage2;
    }

    public String getLinkImage3() {
        return linkImage3;
    }

    public void setLinkImage3(String linkImage3) {
        this.linkImage3 = linkImage3;
    }

    public String getLinkImage4() {
        return linkImage4;
    }

    public void setLinkImage4(String linkImage4) {
        this.linkImage4 = linkImage4;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }

    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
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
}
