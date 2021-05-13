package com.nampt.socialnetworkproject.api.notifyService;

import android.util.Log;

import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.view.notification.NotificationActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotifyServiceImpl {
    public static NotifyServiceImpl instance;

    private NotifyServiceImpl() {
    }

    public static NotifyServiceImpl getInstance() {
        if (instance == null) {
            instance = new NotifyServiceImpl();
        }
        return instance;
    }

    public void setNotify(int type, int partnerId, int roomId, String contentChat) {

        if (contentChat == null) contentChat = "";

        NotifyService.service.setNotify(DataLocalManager.getPrefUser().getAccessToken(), type, partnerId, roomId, contentChat)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        Log.e("push notify", "success");
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Log.e("push notify", "fail");
                    }
                });
    }

    public void setSeen(int notifyId) {
        NotifyService.service.setSeen(DataLocalManager.getPrefUser().getAccessToken(), notifyId)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {

                    }
                });
    }


}
