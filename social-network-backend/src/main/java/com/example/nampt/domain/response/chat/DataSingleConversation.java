package com.example.nampt.domain.response.chat;

import com.example.nampt.domain.response.friend.DataSingleFriend;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class DataSingleConversation {
    int id;
    List<DataSingleFriend> partners;
    String name;
    String lastMessage;
    Date createTimeLastMessage;
    boolean isSeen;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DataSingleFriend> getPartners() {
        return partners;
    }

    public void setPartners(List<DataSingleFriend> partners) {
        this.partners = partners;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("last_message")
    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @JsonProperty("create_time_last_message")
    public Date getCreateTimeLastMessage() {
        return createTimeLastMessage;
    }

    public void setCreateTimeLastMessage(Date createTimeLastMessage) {
        this.createTimeLastMessage = createTimeLastMessage;
    }



    @JsonProperty("is_seen")
    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
