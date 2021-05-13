package com.nampt.socialnetworkproject.api.chatService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.commentService.CommentService;
import com.nampt.socialnetworkproject.api.commentService.response.ListCommentResponse;
import com.nampt.socialnetworkproject.api.userService.response.ListPeopleResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .build();
    ChatService service = new Retrofit.Builder()
            .baseUrl(Host.HOST)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(ChatService.class);

    @GET("/chat/get_list_conversation")
    Call<ListConversationResponse> getListConversation(@Header("Authorization") String token);


    @POST("/chat/create_group")
    Call<GroupChatResponse> createGroupChat(@Header("Authorization") String token,
                                            @Body CreateGroupChatRequest request);

    @GET("/chat/get_conversation/{id}")
    Call<ListMessageResponse> getConversationByRoomId(@Header("Authorization") String token,
                                                      @Path("id") int roomId);

    @GET("/chat/get_conversation_single/{id}")
    Call<ListMessageResponse> getConversationByPartnerId(@Header("Authorization") String token,
                                                         @Path("id") int partnerId);

    @POST("/chat/search/{name}")
    Call<ListConversationResponse> searchConversationsByName(@Header("Authorization") String token,
                                                             @Path("name") String name);

    @POST("/chat/set_seen_by_room_id/{roomId}")
    Call<BaseResponse> setSeenByRoomId(@Header("Authorization") String token,
                                       @Path("roomId") int roomId);

    @POST("/chat/set_seen_single/{partnerId}")
    Call<BaseResponse> setSeenSingle(@Header("Authorization") String token,
                                     @Path("partnerId") int partnerId);
}
