package com.example.nampt.service;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.notification.ListNotifyResponse;

public interface INotificationService {

    BaseResponse setNotify(String token, int type, int partnerId, int roomId,String content);

    BaseResponse setSeen(String token, int notifyId);

    BaseResponse delNotification(String token, int notifyId);

    ListNotifyResponse getListNotification(String token);

    BaseResponse getTotalNotification(String token);
}
