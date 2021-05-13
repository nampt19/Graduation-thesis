package com.example.nampt.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class GroupChatRequest {

    String content;
    int conversationId;
    Date createTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("conversation_id")
    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "GroupChatRequest{" +
                "content='" + content + '\'' +
                ", conversationId=" + conversationId +
                '}';
    }
}
