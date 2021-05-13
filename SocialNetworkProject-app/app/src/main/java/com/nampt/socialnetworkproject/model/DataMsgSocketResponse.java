package com.nampt.socialnetworkproject.model;


public class DataMsgSocketResponse {
    int idRoom;
    String roomName;
    MessageRow message;

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    public MessageRow getMessage() {
        return message;
    }

    public void setMessage(MessageRow message) {
        this.message = message;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
