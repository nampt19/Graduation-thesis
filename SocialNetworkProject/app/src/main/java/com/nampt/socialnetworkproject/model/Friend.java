package com.nampt.socialnetworkproject.model;

import com.google.gson.annotations.SerializedName;

public class Friend {
    private int id;

    private String name;

    private String avatar;

    @SerializedName("is_block")
    private boolean isBlock;

    public Friend() {
    }

    public Friend(int id) {
        this.id = id;
    }


    public Friend(int id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    public Friend(int id, String name, String avatar, boolean isBlock) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.isBlock = isBlock;
    }

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

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imgUrl='" + avatar + '\'' +
                ", isBlock=" + isBlock +
                '}';
    }
}
