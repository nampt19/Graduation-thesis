package com.example.nampt.domain.response.login;

import com.example.nampt.domain.response.BaseResponse;

public class LoginResponse extends BaseResponse {
    private DataLoginResponse data;

    public DataLoginResponse getData() {
        return data;
    }

    public void setData(DataLoginResponse data) {
        this.data = data;
    }
}
