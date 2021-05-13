package com.example.nampt.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "hiddens")
public class Hidden implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "post_id")
    private int postId;

    @Column(name = "hidden_user_id")
    private int hiddenUserId;

    public Hidden() {
    }

    public Hidden(int postId, int hiddenUserId) {
        this.postId = postId;
        this.hiddenUserId = hiddenUserId;
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

    public int getHiddenUserId() {
        return hiddenUserId;
    }

    public void setHiddenUserId(int hiddenUserId) {
        this.hiddenUserId = hiddenUserId;
    }
}
