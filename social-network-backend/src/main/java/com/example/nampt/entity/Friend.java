package com.example.nampt.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "friend")
public class Friend implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_A_id")
    private int userAId;

    @Column(name = "user_B_id")
    private int userBId;

    @Column(name = "is_block", nullable = false, columnDefinition = "BOOLEAN")
    private boolean isBlock;

    public Friend() {
    }

    public Friend(int userAId, int userBId) {
        this.userAId = userAId;
        this.userBId = userBId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserAId() {
        return userAId;
    }

    public void setUserAId(int userAId) {
        this.userAId = userAId;
    }

    public int getUserBId() {
        return userBId;
    }

    public void setUserBId(int userBId) {
        this.userBId = userBId;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }
}
