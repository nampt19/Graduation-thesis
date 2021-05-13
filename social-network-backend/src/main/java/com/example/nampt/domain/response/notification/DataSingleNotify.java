package com.example.nampt.domain.response.notification;

import com.example.nampt.domain.response.friend.DataSingleFriend;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class DataSingleNotify {
    private int id;
    private DataSingleFriend sender;
    private int type;

    private int dataId;

    private boolean isSeen;
    private Date createTime;


    public DataSingleFriend getSender() {
        return sender;
    }

    public void setSender(DataSingleFriend sender) {
        this.sender = sender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @JsonProperty("is_seen")
    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    @JsonProperty("data_id")
    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    @JsonProperty("create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
