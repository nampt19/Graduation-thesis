package com.nampt.socialnetworkproject.api.postService.response.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class DataSinglePost {
    private Post post;

    @SerializedName("total_like")
    private int totalLike;

    @SerializedName("total_comment")
    private int totalComment;

    private Author author;    // nếu tác giả chính là mình thì có thể delete + edit được !

    @SerializedName("is_liked")
    private boolean isLiked; // kiểm tra xem mình đã like chưa !

    @SerializedName("is_block")
    private boolean isBlock; // kiểm tra xem mình có bị tác giả chặn không !

    @SerializedName("is_hidden")
    private boolean isHidden; // kiểm tra xem mình có ẩn bài viết này không !

    public DataSinglePost(Post post, int totalLike, int totalComment, Author author, boolean isLiked, boolean isBlock, boolean isHidden) {
        this.post = post;
        this.totalLike = totalLike;
        this.totalComment = totalComment;
        this.author = author;
        this.isLiked = isLiked;
        this.isBlock = isBlock;
        this.isHidden = isHidden;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }

    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    @Override
    public String toString() {
        return "DataSinglePost{" +
                "post=" + post +
                ", totalLike=" + totalLike +
                ", totalComment=" + totalComment +
                ", author=" + author +
                ", isLiked=" + isLiked +
                ", isBlock=" + isBlock +
                ", isHidden=" + isHidden +
                '}';
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

    public class Post {

        private int id;

        @SerializedName("poster_id")
        private int posterId;

        private String content;

        @SerializedName("link_image_1")
        private String linkImage1;

        @SerializedName("link_image_2")
        private String linkImage2;

        @SerializedName("link_image_3")
        private String linkImage3;

        @SerializedName("link_image_4")
        private String linkImage4;

        @SerializedName("link_video")
        private String linkVideo;

        @SerializedName("create_time")
        private Date createTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

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

        public String getLinkImage1() {
            return linkImage1;
        }

        public void setLinkImage1(String linkImage1) {
            this.linkImage1 = linkImage1;
        }

        public String getLinkImage2() {
            return linkImage2;
        }

        public void setLinkImage2(String linkImage2) {
            this.linkImage2 = linkImage2;
        }

        public String getLinkImage3() {
            return linkImage3;
        }

        public void setLinkImage3(String linkImage3) {
            this.linkImage3 = linkImage3;
        }

        public String getLinkImage4() {
            return linkImage4;
        }

        public void setLinkImage4(String linkImage4) {
            this.linkImage4 = linkImage4;
        }

        public String getLinkVideo() {
            return linkVideo;
        }

        public void setLinkVideo(String linkVideo) {
            this.linkVideo = linkVideo;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        @Override
        public String toString() {
            return "Post{" +
                    "id=" + id +
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
}
