package com.example.nampt.domain.response.chat;

import com.example.nampt.domain.response.friend.DataSingleFriend;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class DataSingleMessage {
    DataSingleFriend sender;
    List<DataSingleFriend> partners;
    String content;

    @SerializedName("create_time")
    Date createTime;

    public DataSingleFriend getSender() {
        return sender;
    }

    public void setSender(DataSingleFriend sender) {
        this.sender = sender;
    }

    public List<DataSingleFriend> getPartners() {
        return partners;
    }

    public void setPartners(List<DataSingleFriend> partners) {
        this.partners = partners;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
