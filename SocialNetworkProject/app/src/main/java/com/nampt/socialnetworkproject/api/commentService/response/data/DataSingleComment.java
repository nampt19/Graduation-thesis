package com.nampt.socialnetworkproject.api.commentService.response.data;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class DataSingleComment {
    private Comment comment;
    private Author author;

    @SerializedName("is_block")
    private boolean isBlock;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }

    public class Comment {
        private int id;

        @SerializedName("post_id")
        private int postId;

        @SerializedName("sender_id")
        private int senderId;

        private String content;

        @SerializedName("create_time")
        private Date createTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPostId() {
            return postId;
        }

        public void setPostId(int postId) {
            this.postId = postId;
        }

        public int getSenderId() {
            return senderId;
        }

        public void setSenderId(int senderId) {
            this.senderId = senderId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }
    }

    public class Author {
        private int id;
        private String name;

        @SerializedName("link_avatar")
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

        @Override
        public String toString() {
            return "Author{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", linkAvatar='" + linkAvatar + '\'' +
                    '}';
        }
    }
}
