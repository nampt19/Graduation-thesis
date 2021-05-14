package com.nampt.socialnetworkproject.api.friendService.response;


import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.model.Friend;

import java.util.List;

public class ListFriendResponse extends BaseResponse {
    private int totalFriend;
    private List<Friend> data;


    public int getTotalFriend() {
        return totalFriend;
    }

    public void setTotalFriend(int totalFriend) {
        this.totalFriend = totalFriend;
    }

    public List<Friend> getData() {
        return data;
    }

    public void setData(List<Friend> data) {
        this.data = data;
    }
}
