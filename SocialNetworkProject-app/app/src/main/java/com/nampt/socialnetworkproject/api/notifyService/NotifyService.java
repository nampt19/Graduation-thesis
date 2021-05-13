package com.nampt.socialnetworkproject.api.notifyService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.postService.PostService;
import com.nampt.socialnetworkproject.api.postService.response.AddPostResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotifyService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();
    NotifyService service = new Retrofit.Builder()
            .baseUrl(Host.HOST)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(NotifyService.class);

    @POST("/notification/set_notify")
    Call<BaseResponse> setNotify(@Header("Authorization") String token,
                                 @Query("type") int type,
                                 @Query("partnerId") int partnerId,
                                 @Query("roomId") int roomId,
                                 @Query("content") String contentChat);

    @POST("/notification/set_seen/{notifyId}")
    Call<BaseResponse> setSeen(@Header("Authorization") String token,
                               @Path("notifyId") int notifyId);

    @POST("/notification/del_notify/{id}")
    Call<BaseResponse> deleteNotify(@Header("Authorization") String token,
                                    @Path("id") int notifyId);

    @GET("/notification/get_list_notify")
    Call<ListNotifyResponse> getListNotify(@Header("Authorization") String token);

    @GET("/notification/get_total_notify")
    Call<BaseResponse> getTotalNotify(@Header("Authorization") String token);



}
