package com.example.nampt.service;

import com.example.nampt.domain.response.BaseResponse;

import java.util.List;

public interface IDeviceService {

    BaseResponse saveToken(String token, int userId, String deviceToken);
    String sendMessage(int senderId ,List<Integer> receiverIdList ,int typeNotify,int dataId,String content );
}
