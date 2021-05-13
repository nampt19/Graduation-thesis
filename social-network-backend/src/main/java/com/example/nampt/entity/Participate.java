package com.example.nampt.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "participate")
public class Participate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "conversation_id")
    private int conversationId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "is_seen", nullable = false, columnDefinition = "BOOLEAN")
    private boolean isSeen;

    public Participate() {
    }

    public Participate(int conversationId, int userId, boolean isSeen) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.isSeen = isSeen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    @Override
    public String toString() {
        return "Participate{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", userId=" + userId +
                ", isSeen=" + isSeen +
                '}';
    }
}
