package com.nampt.socialnetworkproject.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String name;

    @SerializedName("link_avatar")
    private String linkAvatar;

    @SerializedName("link_banner")
    private String linkBanner;
    private String address;
    private String school;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("is_online")
    private Boolean isOnline;

    private String phone;

    public User() {
    }

    public User(int id, String name, String linkAvatar) {
        this.id = id;
        this.name = name;
        this.linkAvatar = linkAvatar;
    }

    public User(int id, String name, String linkAvatar, String linkBanner, String address, String school, String accessToken, Boolean isOnline,String phone) {
        this.id = id;
        this.name = name;
        this.linkAvatar = linkAvatar;
        this.linkBanner = linkBanner;
        this.address = address;
        this.school = school;
        this.accessToken = accessToken;
        this.isOnline = isOnline;
        this.phone=phone;
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

    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public String getLinkBanner() {
        return linkBanner;
    }

    public void setLinkBanner(String linkBanner) {
        this.linkBanner = linkBanner;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", linkAvatar='" + linkAvatar + '\'' +
                ", linkBanner='" + linkBanner + '\'' +
                ", address='" + address + '\'' +
                ", school='" + school + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }
}
