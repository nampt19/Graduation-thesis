package com.nampt.socialnetworkproject.api.userService;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.postService.response.AddPostResponse;
import com.nampt.socialnetworkproject.api.postService.response.ListPostResponse;
import com.nampt.socialnetworkproject.api.userService.request.LoginRequest;
import com.nampt.socialnetworkproject.api.userService.request.RegisterRequest;
import com.nampt.socialnetworkproject.api.userService.response.AlbumResponse;
import com.nampt.socialnetworkproject.api.userService.response.ListPeopleResponse;
import com.nampt.socialnetworkproject.api.userService.response.LoginResponse;
import com.nampt.socialnetworkproject.api.userService.response.PeopleResponse;
import com.nampt.socialnetworkproject.api.userService.response.ProfileUserResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();
    UserService service = new Retrofit.Builder()
            .baseUrl(Host.HOST)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(UserService.class);

    @POST("user/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("user/signup")
    Call<LoginResponse> signup(@Body RegisterRequest request);

    @POST("user/logout/{token}")
    Call<BaseResponse> logout(@Path("token") String token);

    @GET("user/profile/{id}")
    Call<ProfileUserResponse> getProfile(@Header("Authorization") String token,
                                         @Path("id") int userId);

    @Multipart
    @POST("/user/set_banner")
    Call<BaseResponse> setBanner(@Header("Authorization") String token,
                                 @Part MultipartBody.Part file);

    @Multipart
    @POST("/user/set_avatar")
    Call<BaseResponse> setAvatar(@Header("Authorization") String token,
                                 @Part MultipartBody.Part file);

    @POST("/user/set_profile")
    Call<BaseResponse> setProfile(@Header("Authorization") String token,
                                  @Query("school") String school,
                                  @Query("address") String address);

    @POST("/user/search/{name}")
    Call<ListPeopleResponse> searchPeoplesByName(@Header("Authorization") String token,
                                                 @Path("name") String name);

    @POST("/user/change_password")
    Call<BaseResponse> changePassword(@Header("Authorization") String token,
                                      @Query("newPass") String newPass,
                                      @Query("oldPass") String oldPass);

    @GET("/user/get_type/{userId}")
    Call<PeopleResponse> getType(@Header("Authorization") String token,
                                 @Path("userId") int userId);

    @GET("/user/get_block/{userId}")
    Call<BaseResponse> getBlock(@Header("Authorization") String token,
                                 @Path("userId") int userId);

    @GET("/user/get_album/{userId}")
    Call<AlbumResponse> getAlbumList(@Header("Authorization") String token,
                                     @Path("userId") int userId);

    @POST("/user/set_name/{name}")
    Call<BaseResponse> setName(@Header("Authorization") String token,
                                  @Path("name") String userId);


}
