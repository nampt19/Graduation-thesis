package com.example.nampt.domain.response.friend;

import com.example.nampt.domain.response.BaseResponse;

import java.util.List;

public class ListFriendResponse extends BaseResponse {
    private int totalFriend;
    private List<DataSingleFriend> data;


    public int getTotalFriend() {
        return totalFriend;
    }

    public void setTotalFriend(int totalFriend) {
        this.totalFriend = totalFriend;
    }

    public List<DataSingleFriend> getData() {
        return data;
    }

    public void setData(List<DataSingleFriend> data) {
        this.data = data;
    }
}
