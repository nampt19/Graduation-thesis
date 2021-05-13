package com.example.nampt.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("partner_id")
    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    @Override
    public String toString() {
        return "SingleChatRequest{" +
                "content='" + content + '\'' +
                ", partnerId=" + partnerId +
                '}';
    }

    @JsonProperty("create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
