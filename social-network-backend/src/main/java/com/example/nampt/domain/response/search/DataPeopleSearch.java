package com.example.nampt.domain.response.search;

import com.example.nampt.domain.response.friend.DataSingleFriend;

public class DataPeopleSearch extends DataSingleFriend {
    // 0 : none , 1 : isFriend , 2: requested by seeker , 3 : request to seeker
    private int type;

    public DataPeopleSearch() {
    }

    public DataPeopleSearch(int id, String name, String avatar, boolean isBlock, int type) {
        super(id, name, avatar, isBlock);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
