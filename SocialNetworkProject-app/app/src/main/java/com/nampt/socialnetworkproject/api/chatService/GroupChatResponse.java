package com.nampt.socialnetworkproject.api.chatService;


import com.google.gson.annotations.SerializedName;
import com.nampt.socialnetworkproject.api.BaseResponse;

public class GroupChatResponse extends BaseResponse {

    @SerializedName("conversation_id")
    int conversationId;


    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String toString() {
        return "GroupChatResponse{" +
                "conversationId=" + conversationId +
                '}';
    }
}
