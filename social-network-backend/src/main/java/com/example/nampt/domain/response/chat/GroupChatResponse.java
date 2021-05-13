package com.example.nampt.domain.response.chat;

import com.example.nampt.domain.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupChatResponse extends BaseResponse {
    int conversationId;

    @JsonProperty("conversation_id")
    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }
}
