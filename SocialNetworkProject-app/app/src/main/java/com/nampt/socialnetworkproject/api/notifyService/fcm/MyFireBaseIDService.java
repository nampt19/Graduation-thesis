package com.nampt.socialnetworkproject.api.notifyService.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFireBaseIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token= FirebaseInstanceId.getInstance().getToken();
        luuTokenVaoCSDLRieng(token);
    }

    private void luuTokenVaoCSDLRieng(String token) {
        FcmService.service.saveDeviceToken(DataLocalManager.getPrefUser().getAccessToken(),
                DataLocalManager.getPrefUser().getId(),token)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode()==1000){
                            Log.e("save fcm token","success");
                        }else {
                            Log.e("save fcm token","failue");
                        }
                    }
                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Log.e("save fcm token","failue");
                    }
                });
    }
}
