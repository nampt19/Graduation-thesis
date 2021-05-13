package com.example.nampt.domain.response.friend;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class DataSingleFriend {
    private int id;
    private String name;
    private String avatar;

    public DataSingleFriend() {
    }

    public DataSingleFriend(int id, String name, String avatar, boolean isBlock) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.isBlock = isBlock;
    }

    @SerializedName("is_block")
    private boolean isBlock;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    @JsonProperty("is_block")
    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }
}
