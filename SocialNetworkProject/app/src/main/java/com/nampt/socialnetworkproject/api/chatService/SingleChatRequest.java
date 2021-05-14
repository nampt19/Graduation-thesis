package com.nampt.socialnetworkproject.api.chatService;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class SingleChatRequest {

    String content;

    @SerializedName("partner_id")
    int partnerId;

    @SerializedName("create_time")
    Date createTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SingleChatRequest{" +
                "content='" + content + '\'' +
                ", partnerId=" + partnerId +
                '}';
    }
}
