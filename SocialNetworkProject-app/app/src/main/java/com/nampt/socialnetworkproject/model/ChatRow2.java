package com.nampt.socialnetworkproject.model;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ChatRow2 {
    int id;

    List<Friend> partners;

    String name;

    @SerializedName("last_message")
    String lastMessage;

    @SerializedName("create_time_last_message")
    Date createTimeLastMessage;

    @SerializedName("is_seen")
    boolean isSeen;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Friend> getPartners() {
        return partners;
    }

    public void setPartners(List<Friend> partners) {
        this.partners = partners;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getCreateTimeLastMessage() {
        return createTimeLastMessage;
    }

    public void setCreateTimeLastMessage(Date createTimeLastMessage) {
        this.createTimeLastMessage = createTimeLastMessage;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    /*Comparator for sorting the list by roll no*/
    public static Comparator<ChatRow2> DateComparatorDESC = new Comparator<ChatRow2>() {

        @Override
        public int compare(ChatRow2 o1, ChatRow2 o2) {
            if (o2.getCreateTimeLastMessage()==null || o1.getCreateTimeLastMessage()==null){
                return 0;
            }
            return o2.getCreateTimeLastMessage().compareTo(o1.getCreateTimeLastMessage());
        }
    };

    @Override
    public String toString() {
        return "ChatRow2{" +
                "id=" + id +
                ", partners=" + partners +
                ", name='" + name + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", createTimeLastMessage=" + createTimeLastMessage +
                ", isSeen=" + isSeen +
                '}';
    }
}
