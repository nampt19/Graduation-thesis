package com.nampt.socialnetworkproject.api.chatService;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class CreateGroupChatRequest {

    String name;

    @SerializedName("partner_ids")
    List<Integer> partnerIds;

    @SerializedName("create_time")
    Date createTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getPartnerIds() {
        return partnerIds;
    }

    public void setPartnerIds(List<Integer> partnerIds) {
        this.partnerIds = partnerIds;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
