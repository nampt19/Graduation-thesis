package com.example.nampt.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "friend_request")
public class FriendRequest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "sender_id")
    private int senderId;

    public FriendRequest() {
    }

    public FriendRequest(int userId, int senderId) {
        this.userId = userId;
        this.senderId = senderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
}
