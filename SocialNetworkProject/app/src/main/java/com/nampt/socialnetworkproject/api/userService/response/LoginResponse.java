package com.nampt.socialnetworkproject.api.userService.response;


import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.model.User;

public class LoginResponse extends BaseResponse {
    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "code=" + getCode() +
                " message=" + getMessage() +
                " data=" + data +
                '}';
    }

}
