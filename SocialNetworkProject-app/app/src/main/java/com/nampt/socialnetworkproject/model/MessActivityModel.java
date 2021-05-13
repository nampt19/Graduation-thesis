package com.nampt.socialnetworkproject.model;

public class MessActivityModel {
    private int dataId; /*roomId or userId chatting */
    private boolean isInstall;
    private boolean isRoom;

    public MessActivityModel() {
    }

    public MessActivityModel(int dataId, boolean isInstall, boolean isRoom) {
        this.dataId = dataId;
        this.isInstall = isInstall;
        this.isRoom = isRoom;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public boolean isInstall() {
        return isInstall;
    }

    public void setInstall(boolean install) {
        isInstall = install;
    }

    public boolean isRoom() {
        return isRoom;
    }

    public void setRoom(boolean room) {
        isRoom = room;
    }

    @Override
    public String toString() {
        return "MessActivityModel{" +
                "dataId=" + dataId +
                ", isInstall=" + isInstall +
                ", isRoom=" + isRoom +
                '}';
    }
}
