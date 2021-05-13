package com.nampt.socialnetworkproject.model;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Notification {
    private int id;
    private Friend sender;
    private int type;  /* 1:new post , 2 : chat single , 3 : new friend request 4: chat group */

    @SerializedName("data_id")
    private int dataId;

    @SerializedName("is_seen")
    private boolean isSeen;

    @SerializedName("create_time")
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Friend getSender() {
        return sender;
    }

    public void setSender(Friend sender) {
        this.sender = sender;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", sender=" + sender +
                ", type=" + type +
                ", dataId=" + dataId +
                ", isSeen=" + isSeen +
                ", createTime=" + createTime +
                '}';
    }
}
