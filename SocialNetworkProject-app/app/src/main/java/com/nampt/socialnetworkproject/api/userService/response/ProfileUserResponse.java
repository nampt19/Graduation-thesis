package com.nampt.socialnetworkproject.api.userService.response;

import com.google.gson.annotations.SerializedName;

public class ProfileUserResponse extends LoginResponse {

    @SerializedName("block")
    private boolean isBlock;

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }
}
