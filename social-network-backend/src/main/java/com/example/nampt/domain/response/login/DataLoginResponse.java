package com.example.nampt.domain.response.login;

public class DataLoginResponse {
    private int id;
    private String name;

    private String link_avatar;
    private String link_banner;
    private String address;
    private String school;
    private String access_token;
    private Boolean is_online;
    private String phone;

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

    public String getLink_avatar() {
        return link_avatar;
    }

    public void setLink_avatar(String link_avatar) {
        this.link_avatar = link_avatar;
    }

    public String getLink_banner() {
        return link_banner;
    }

    public void setLink_banner(String link_banner) {
        this.link_banner = link_banner;
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

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Boolean getIs_online() {
        return is_online;
    }

    public void setIs_online(Boolean is_online) {
        this.is_online = is_online;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
