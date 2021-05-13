package com.nampt.socialnetworkproject.model;

public class PeopleSearch extends Friend {
    // 0 : none , 1 : isFriend , 2: requested by seeker , 3 : request to seeker
    private int type;

    public PeopleSearch() {
    }

    public PeopleSearch(int id, String name, String avatar, boolean isBlock, int type) {
        super(id, name, avatar, isBlock);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PeopleSearch{" +
                "type=" + getId() +
                "type=" + getName() +
                "type=" + isBlock() +
                "type=" + type +
                '}';
    }
}
