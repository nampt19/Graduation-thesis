package com.example.nampt.domain.response.post.data;

import com.example.nampt.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSinglePost {
    private Post post;
    private int totalLike;
    private int totalComment;
    private Author author;    // nếu tác giả chính là mình thì có thể delete + edit được !
    private boolean isLiked; // kiểm tra xem mình đã like chưa !
    private boolean isBlock; // kiểm tra xem mình có bị tác giả chặn không !
    private boolean isHidden; // kiểm tra xem mình có ẩn bài viết này không !

    public DataSinglePost(Post post, int total_like, int total_comment, Author author, boolean is_liked, boolean is_block, boolean is_hidden) {
        this.post = post;
        this.totalLike = total_like;
        this.totalComment = total_comment;
        this.author = author;
        this.isLiked = is_liked;
        this.isBlock = is_block;
        this.isHidden = is_hidden;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @JsonProperty("total_like")
    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }

    @JsonProperty("total_comment")
    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @JsonProperty("is_liked")
    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @JsonProperty("is_block")
    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }

    @JsonProperty("is_hidden")
    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
