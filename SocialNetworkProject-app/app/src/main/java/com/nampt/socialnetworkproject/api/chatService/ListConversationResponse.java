package com.nampt.socialnetworkproject.api.chatService;

import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.model.ChatRow2;

import java.util.List;

public class ListConversationResponse extends BaseResponse {
    List<ChatRow2> data;

    public List<ChatRow2> getData() {
        return data;
    }

    public void setData(List<ChatRow2> data) {
        this.data = data;
    }
}
