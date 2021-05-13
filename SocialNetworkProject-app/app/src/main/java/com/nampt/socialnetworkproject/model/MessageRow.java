package com.nampt.socialnetworkproject.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class MessageRow {
    private Friend sender;
    private List<Friend> partners;
    private String content;

    @SerializedName("create_time")
    private Date createTime;

    public Friend getSender() {
        return sender;
    }

    public void setSender(Friend sender) {
        this.sender = sender;
    }

    public List<Friend> getPartners() {
        return partners;
    }

    public void setPartners(List<Friend> partners) {
        this.partners = partners;
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
}
