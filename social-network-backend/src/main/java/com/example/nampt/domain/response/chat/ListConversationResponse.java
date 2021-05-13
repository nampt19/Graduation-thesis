package com.example.nampt.domain.response.chat;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.friend.DataSingleFriend;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class ListConversationResponse extends BaseResponse {
    List<DataSingleConversation> data;

    public List<DataSingleConversation> getData() {
        return data;
    }

    public void setData(List<DataSingleConversation> data) {
        this.data = data;
    }
}
