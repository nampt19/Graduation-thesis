package com.example.nampt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "post")
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "poster_id")
    private int posterId;
    @Column(name = "content")
    private String content;
    @Column(name = "link_image_1")
    private String linkImage1;
    @Column(name = "link_image_2")
    private String linkImage2;
    @Column(name = "link_image_3")
    private String linkImage3;
    @Column(name = "link_image_4")
    private String linkImage4;
    @Column(name = "link_video")
    private String linkVideo;

    @Column(name = "create_time")
    private Date createTime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("poster_id")
    public int getPosterId() {
        return posterId;
    }

    public void setPosterId(int posterId) {
        this.posterId = posterId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("link_image_1")
    public String getLinkImage1() {
        return linkImage1;
    }

    public void setLinkImage1(String linkImage1) {
        this.linkImage1 = linkImage1;
    }

    @JsonProperty("link_image_2")
    public String getLinkImage2() {
        return linkImage2;
    }

    public void setLinkImage2(String linkImage2) {
        this.linkImage2 = linkImage2;
    }

    @JsonProperty("link_image_3")
    public String getLinkImage3() {
        return linkImage3;
    }

    public void setLinkImage3(String linkImage3) {
        this.linkImage3 = linkImage3;
    }

    @JsonProperty("link_image_4")
    public String getLinkImage4() {
        return linkImage4;
    }

    public void setLinkImage4(String linkImage4) {
        this.linkImage4 = linkImage4;
    }

    @JsonProperty("link_video")
    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    @JsonProperty("create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Post{" +
                ", posterId=" + posterId +
                ", content='" + content + '\'' +
                ", linkImage1='" + linkImage1 + '\'' +
                ", linkImage2='" + linkImage2 + '\'' +
                ", linkImage3='" + linkImage3 + '\'' +
                ", linkImage4='" + linkImage4 + '\'' +
                ", linkVideo='" + linkVideo + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
