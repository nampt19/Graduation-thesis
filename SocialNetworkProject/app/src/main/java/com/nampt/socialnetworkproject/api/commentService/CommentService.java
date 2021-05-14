package com.nampt.socialnetworkproject.api.commentService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.commentService.request.SetCommentRequest;
import com.nampt.socialnetworkproject.api.commentService.response.ListCommentResponse;
import com.nampt.socialnetworkproject.api.commentService.response.SingleCommentResponse;
import com.nampt.socialnetworkproject.api.Host;

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

public interface CommentService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .build();
    CommentService service = new Retrofit.Builder()
            .baseUrl(Host.HOST)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(CommentService.class);

    @POST("/comment/delete_comment/{id}")
    Call<BaseResponse> deleteComment(@Header("Authorization") String token,
                                     @Path("id") int idComment);

    @POST("/comment/set_comment")
    Call<SingleCommentResponse> setComment(@Header("Authorization") String token,
                                           @Body SetCommentRequest request);

    @GET("/comment/get_list_comment/{idPost}")
    Call<ListCommentResponse> getListComment(@Header("Authorization") String token,
                                             @Path("idPost") int idPost);

}
