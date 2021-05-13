package com.nampt.socialnetworkproject.api.postService.request;

public class EditPostRequest {
    private int id;
    private String content;

    public EditPostRequest() {
    }

    public EditPostRequest(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
