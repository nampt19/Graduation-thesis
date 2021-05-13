package com.example.nampt.domain.response.post.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Author {
    private int id;
    private String name;
    private String linkAvatar;

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

    @JsonProperty("link_avatar")
    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public Author(int id, String name, String linkAvatar) {
        this.id = id;
        this.name = name;
        this.linkAvatar = linkAvatar;
    }
}
