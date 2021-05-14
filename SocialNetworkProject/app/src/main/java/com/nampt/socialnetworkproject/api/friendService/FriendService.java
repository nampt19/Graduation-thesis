package com.nampt.socialnetworkproject.api.friendService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.friendService.response.ListFriendResponse;
import com.nampt.socialnetworkproject.api.postService.PostService;
import com.nampt.socialnetworkproject.api.postService.response.ListPostResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FriendService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();
    FriendService service = new Retrofit.Builder()
            .baseUrl(Host.HOST)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(FriendService.class);

    @GET("/friend/get_list_friends")
    Call<ListFriendResponse> getFriendList(@Header("Authorization") String token,
                                           @Query("index") int index,
                                           @Query("count") int count);

    @GET("/friend/get_requested_friends")
    Call<ListFriendResponse> getRequestedFriendList(@Header("Authorization") String token);

    @GET("/friend/get_list_invitation_sent_friends")
    Call<ListFriendResponse> getInvitationSentFriendList(@Header("Authorization") String token);

    @GET("/friend/get_list_friends_online")
    Call<ListFriendResponse> getFriendOnlineList(@Header("Authorization") String token);

    @POST("/friend/set_accept_friend")
    Call<BaseResponse> setAcceptFriend(@Header("Authorization") String token,
                                       @Query("sender_id") int senderId,
                                       @Query("is_accept") int isAccept);

    @POST("/friend/delete_invitation_sent_friend/{id}")
    Call<BaseResponse> deleteInvitationSentFriend(@Header("Authorization") String token,
                                                  @Path("id") int userId);

    @POST("/friend/set_request_friend/{id}")
    Call<BaseResponse> setRequestFriend(@Header("Authorization") String token,
                                    @Path("id") int userId);

    @POST("/friend/delete_friend/{id}")
    Call<BaseResponse> deleteFriend(@Header("Authorization") String token,
                                                  @Path("id") int userId);

    @POST("/friend/block_user/{id}")
    Call<BaseResponse> block(@Header("Authorization") String token,
                                                  @Path("id") int userId);

    @POST("/friend/unblock_user/{id}")
    Call<BaseResponse> unblock(@Header("Authorization") String token,
                                                  @Path("id") int userId);

    @GET("/friend/get_block_list")
    Call<ListFriendResponse> getBlockList(@Header("Authorization") String token);

}
