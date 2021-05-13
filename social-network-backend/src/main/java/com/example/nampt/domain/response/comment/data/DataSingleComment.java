package com.example.nampt.domain.response.comment.data;

import com.example.nampt.domain.response.post.data.Author;
import com.example.nampt.entity.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSingleComment {
    private Comment comment;
    private Author author;
    private boolean isBlock;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @JsonProperty("is_block")
    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }
}
