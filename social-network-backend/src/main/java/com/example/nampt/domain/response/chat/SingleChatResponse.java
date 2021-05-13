package com.example.nampt.domain.response.chat;

import com.example.nampt.domain.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SingleChatResponse extends BaseResponse {

    private int senderId;

    private String content;

    @JsonProperty("sender_id")
    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    @JsonProperty("content")
    public String getNewContent() {
        return content;
    }

    public void setNewContent(String content) {
        this.content = content;
    }
}
