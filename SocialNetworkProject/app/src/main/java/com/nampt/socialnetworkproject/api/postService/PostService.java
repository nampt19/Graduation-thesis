package com.nampt.socialnetworkproject.api.postService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.postService.response.AddPostResponse;
import com.nampt.socialnetworkproject.api.postService.request.EditPostRequest;
import com.nampt.socialnetworkproject.api.postService.response.LikeResponse;
import com.nampt.socialnetworkproject.api.postService.response.ListPostResponse;
import com.nampt.socialnetworkproject.api.postService.response.SinglePostResponse;

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

public interface PostService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();
    PostService service = new Retrofit.Builder()
            .baseUrl(Host.HOST)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
            .create(PostService.class);

    @Multipart
    @POST("/post/add_post")
    Call<AddPostResponse> addPost(@Header("Authorization") String token,
                                  @Part MultipartBody.Part[] file,
                                  @Part("content") RequestBody content,
                                  @Part("create_time") RequestBody createTime);

    @GET("/post/get_list_posts")
    Call<ListPostResponse> getListPost(@Header("Authorization") String token,
                                       @Query("index") int index,
                                       @Query("count") int count,
                                       @Query("last_id") int lastPostId);

    @GET("/post/get_posts/{id}")
    Call<ListPostResponse> getListPostByPosterId(@Header("Authorization") String token,
                                                 @Path("id") int idPost);

    @GET("/post/get_post/{id}")
    Call<SinglePostResponse> getPost(@Header("Authorization") String token,
                                     @Path("id") int idPost);

    @POST("/post/delete_post/{id}")
    Call<BaseResponse> deletePost(@Header("Authorization") String token,
                                  @Path("id") int idPost);

    @POST("/post/edit_post")
    Call<BaseResponse> editPost(@Header("Authorization") String token,
                                @Body EditPostRequest editPostRequest);

    @POST("/post/hidden_post/{id}")
    Call<BaseResponse> hiddenPost(@Header("Authorization") String token,
                                  @Path("id") int idPost);

    @POST("/post/like/{idPost}")
    Call<LikeResponse> likePost(@Header("Authorization") String token,
                                @Path("idPost") int idPost);

}
