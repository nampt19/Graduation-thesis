package com.example.nampt.domain.response.user;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.login.DataLoginResponse;

public class ProfileUserResponse extends BaseResponse {
    private DataLoginResponse data;

    private boolean isBlock;

    public DataLoginResponse getData() {
        return data;
    }

    public void setData(DataLoginResponse data) {
        this.data = data;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }
}
