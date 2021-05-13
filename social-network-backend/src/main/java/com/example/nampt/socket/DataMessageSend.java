package com.example.nampt.socket;

import java.util.Date;

public class DataMessageSend {
    private String content;
    private Date createTime;

    public DataMessageSend(String content, Date createTime) {
        this.content = content;
        this.createTime = createTime;
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
