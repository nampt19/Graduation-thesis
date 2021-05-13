package com.example.nampt.socket;

import com.example.nampt.domain.response.chat.DataSingleMessage;
import com.example.nampt.domain.response.friend.DataSingleFriend;

import java.util.Date;

public class ResponseData {
    int idRoom;
    String roomName;
    DataSingleMessage message;

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }


    public DataSingleMessage getMessage() {
        return message;
    }

    public void setMessage(DataSingleMessage message) {
        this.message = message;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }


}
