package com.nampt.socialnetworkproject.view.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nampt.socialnetworkproject.PaginationScrollListener;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.notifyService.NotifyServiceImpl;
import com.nampt.socialnetworkproject.api.postService.PostService;
import com.nampt.socialnetworkproject.api.postService.response.AddPostResponse;
import com.nampt.socialnetworkproject.api.postService.response.ListPostResponse;
import com.nampt.socialnetworkproject.api.postService.response.SinglePostResponse;
import com.nampt.socialnetworkproject.api.postService.response.data.DataSinglePost;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.Post;
import com.nampt.socialnetworkproject.model.User;
import com.nampt.socialnetworkproject.view.home.rcvAdapter.CommentAdapter;
import com.nampt.socialnetworkproject.view.home.rcvAdapter.PostAdapter;
import com.nampt.socialnetworkproject.view.login.LoginActivity;
import com.nampt.socialnetworkproject.view.notification.NotificationActivity;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;
import com.nampt.socialnetworkproject.view.writePost.WritePostActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hb.xvideoplayer.MxVideoPlayerWidget;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rcvPost;
    private PostAdapter mPostAdapter;
    CircleImageView imgAvatar;
    TextView txtPost;
    Context mContext = null;
    View layoutNoData, layoutLoading, layoutNoNetwork, layoutAddPostHome, containerThinkImage, containerThinkVideo;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Post> mListPost = new ArrayList<>();
    int index = 0, count = 7, lastPostId = -1;
    boolean isLoading, isLastPage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Home", "onCreate HomeFragment");
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("Home", "onCreateView");
        View home = inflater.inflate(R.layout.fragment_home, container, false);
        addControl(home);
        initRcvPost();
        addEvent();

        return home;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addControl(View home) {
        imgAvatar = home.findViewById(R.id.img_user_avatar_post);
        rcvPost = home.findViewById(R.id.recycleViewPost);
        txtPost = home.findViewById(R.id.txt_post);
        containerThinkImage = home.findViewById(R.id.container_think_image);
        containerThinkVideo = home.findViewById(R.id.container_think_video);
        layoutNoData = home.findViewById(R.id.layout_no_data_home);
        layoutLoading = home.findViewById(R.id.layout_progress_loading_home);
        layoutNoNetwork = home.findViewById(R.id.layout_no_network_home);
        layoutAddPostHome = home.findViewById(R.id.layout_add_post_home);
        nestedScrollView = home.findViewById(R.id.nested_scroll_view_home);
        swipeRefreshLayout = home.findViewById(R.id.swipe_refresh_layout_home);
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
        layoutAddPostHome.setVisibility(View.GONE);
        txtPost.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setAlpha(1.0f);
                        break;
                }
                return false;
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueThin));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initRcvPost() {
        mPostAdapter = new PostAdapter(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rcvPost.setLayoutManager(layoutManager);
        mPostAdapter.setPostList(mListPost);
        mPostAdapter.setFragment(this);
        rcvPost.setAdapter(mPostAdapter);
        nestedScrollView
                .getViewTreeObserver()
                .addOnScrollChangedListener(new PaginationScrollListener(nestedScrollView, layoutManager) {
                    @Override
                    public void loadMoreItems() {
                        isLoading = true;
                        loadNextPage();
                    }

                    @Override
                    public boolean isLoading() {
                        return isLoading;
                    }

                    @Override
                    public boolean isLastPage() {
                        return isLastPage;
                    }
                });
    }

    private void addEvent() {

        if (DataLocalManager.getPrefUser().getLinkAvatar() != null) {
            Glide.with(this)
                    .load(Host.HOST + DataLocalManager.getPrefUser().getLinkAvatar())
                    .placeholder(R.drawable.unnamed)
                    .into(imgAvatar);
        }
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileUserActivity.class);
                intent.putExtra("userId", DataLocalManager.getPrefUser().getId());
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        txtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WritePostActivity.class);
                startActivityForResult(intent, HomeActivity.CODE_WRITE_POST);

                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            }
        });
        containerThinkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WritePostActivity.class);
                intent.putExtra("isOpenImagePicker", true);
                startActivityForResult(intent, HomeActivity.CODE_WRITE_POST);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        containerThinkVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WritePostActivity.class);
                intent.putExtra("isOpenVideoPicker", true);

                startActivityForResult(intent, HomeActivity.CODE_WRITE_POST);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        setFirstData();
    }

    private void setFirstData() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoNetwork.setVisibility(View.GONE);
        layoutNoData.setVisibility(View.GONE);
        PostService.service
                .getListPost(DataLocalManager.getPrefUser().getAccessToken(), index, count, lastPostId)
                .enqueue(new Callback<ListPostResponse>() {
                    @Override
                    public void onResponse(Call<ListPostResponse> call, Response<ListPostResponse> response) {
                        layoutLoading.setVisibility(View.GONE);
                        if (response.body().getCode() == 1000) {
                            List<DataSinglePost> singlePostList = response.body().getData();

                            mListPost = getListPost(singlePostList);

                            List<Post> postsLocal = new ArrayList<>();
                            postsLocal.addAll(getListPost(singlePostList));

                            DataLocalManager.setPrefListPost(postsLocal);

                            if (mListPost.isEmpty()) {
                                layoutNoData.setVisibility(View.VISIBLE);
                                return;
                            }

                            mPostAdapter.setPostList(mListPost);

                            lastPostId = response.body().getLastId();
                            if (response.body().isLastPage()) {
                                isLastPage = true;
                            } else {
                                index += count;
                            }
                        } else if (response.body().getCode() == 9993) {
                            // quay về login, xóa sharePrefUser , sai cmnl token rồi
                            DataLocalManager.setPrefUser(null);

                            Intent intent = new Intent(mContext, LoginActivity.class);
                            startActivity(intent);
                            ((Activity) mContext).finishAffinity();
                        }
                    }

                    @Override
                    public void onFailure(Call<ListPostResponse> call, Throwable t) {
                        layoutLoading.setVisibility(View.GONE);

                        List<Post> postsCache = DataLocalManager.getPrefPostList();
                        if (postsCache != null && !postsCache.isEmpty()) {
                            mListPost = DataLocalManager.getPrefPostList();
                            mPostAdapter.setPostList(mListPost);
                            isLastPage = true;
                        } else {
                            layoutNoNetwork.setVisibility(View.VISIBLE);
                            layoutNoNetwork.findViewById(R.id.btn_reload_network_utils).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    layoutNoNetwork.setVisibility(View.GONE);
                                    setFirstData();
                                }
                            });
                        }
                    }
                });
    }

    private void loadNextPage() {
        if (mListPost.size() < 5) {
            isLoading = false;
            isLastPage = true;
            return;
        } else {
            isLoading = true;
            isLastPage = false;
        }
        mPostAdapter.addFooterLoading();
        // Toast.makeText(mContext, index + "-" + count, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PostService.service
                        .getListPost(DataLocalManager.getPrefUser().getAccessToken(), index, count, lastPostId)
                        .enqueue(new Callback<ListPostResponse>() {
                            @Override
                            public void onResponse(Call<ListPostResponse> call, Response<ListPostResponse> response) {
                                if (response.body().getCode() == 1000) {
                                    mPostAdapter.removeFooterLoading();
                                    mListPost.addAll(getListPost(response.body().getData()));
                                    mPostAdapter.setPostList(mListPost);
                                    isLoading = false;
                                    lastPostId = response.body().getLastId();
                                    if (response.body().isLastPage()) {
                                        isLastPage = true;
                                    } else {
                                        index += count;
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ListPostResponse> call, Throwable t) {
                                isLoading = false;
                                mPostAdapter.removeFooterLoading();
                                //     Toast.makeText(mContext, "Lỗi kết nối,vui lòng thử lại sau !", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }, 1000);

    }


    private List<Post> getListPost(List<DataSinglePost> data) {
        List<Post> postList = new ArrayList<>();
        for (DataSinglePost post : data) {
            if (!post.isBlock() && !post.isHidden()) {
                Post post2 = new Post();
                post2.setId(post.getPost().getId());
                post2.setContent(post.getPost().getContent());
                post2.setLinkImage1(post.getPost().getLinkImage1());
                post2.setLinkImage2(post.getPost().getLinkImage2());
                post2.setLinkImage3(post.getPost().getLinkImage3());
                post2.setLinkImage4(post.getPost().getLinkImage4());
                post2.setLinkVideo(post.getPost().getLinkVideo());
                post2.setCreateTime(post.getPost().getCreateTime());
                post2.setTotalComment(post.getTotalComment());
                post2.setTotalLike(post.getTotalLike());
                post2.setLiked(post.isLiked());
                post2.setHidden(post.isHidden());
                post2.setBlock(post.isBlock());
                post2.setAuthorId(post.getAuthor().getId());
                post2.setAuthorName(post.getAuthor().getName());
                post2.setLinkAvatar(post.getAuthor().getLinkAvatar());
                postList.add(post2);
            }
        }
        return postList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == HomeActivity.CODE_WRITE_POST) {
                String content = data.getStringExtra("content");
                String pathVideo = data.getStringExtra("pathVideo");
                List<Uri> uriImages = data.getParcelableArrayListExtra("listUriImage");
                handleAddPost(content, uriImages, pathVideo);
            }
            if (requestCode == HomeActivity.CODE_EDIT_POST) {
                String contentEdited = data.getStringExtra("contentEdited");
                if (contentEdited != null && contentEdited.equals("")) contentEdited = null;
                int idPostEdited = data.getIntExtra("idPostEdited", 0);
                int indexPostEdited = -1;
                for (int i = 0; i < mListPost.size(); i++) {
                    if (mListPost.get(i).getId() == idPostEdited) {
                        indexPostEdited = i;
                        break;
                    }
                }
                if (indexPostEdited != -1) {
                    mListPost.get(indexPostEdited).setContent(contentEdited);
                    mPostAdapter.setPostList(mListPost);
                    rcvPost.setAdapter(mPostAdapter);
                }

                List<Post> postsLocal = DataLocalManager.getPrefPostList();
                for (int i = 0; i < postsLocal.size(); i++) {
                    Post postEdited = postsLocal.get(i);
                    if (postEdited.getId() == idPostEdited) {
                        postEdited.setContent(contentEdited);
                        postsLocal.set(i, postEdited);
                        break;
                    }
                }
                DataLocalManager.setPrefListPost(postsLocal);
            }
        }

    }

    private void handleAddPost(String contentPost, List<Uri> mUris, String pathVideo) {
        RequestBody contentPart = RequestBody.create(MultipartBody.FORM, contentPost);
        RequestBody createTimePart = RequestBody.create(MultipartBody.FORM,
                new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create().toJson(new Date()));

        List<MultipartBody.Part> partList = new ArrayList<>();
        RequestBody requestBody;
        if (mUris != null) {
            for (Uri uri : mUris) {
                File file = new File(uri.getPath());
                if (!file.exists()) {
                    requestBody = RequestBody.create(MediaType.parse("image/*"), new byte[0]);
                } else {
                    requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                }
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "image_" + file.getName(), requestBody);
                partList.add(filePart);
            }
        }
        if (pathVideo != null) {
            File file = new File(pathVideo);
            if (file.exists()) {
                requestBody = RequestBody.create(MediaType.parse("video/*"), file);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "video_" + file.getName(), requestBody);
                partList.add(filePart);
            }
        }
        if (mUris == null && pathVideo == null) {
            requestBody = RequestBody.create(MediaType.parse("video/*"), new byte[0]);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "null", requestBody);
            partList.add(filePart);
        }

        MultipartBody.Part[] arrFilePart = new MultipartBody.Part[partList.size()];
        partList.toArray(arrFilePart);

        layoutAddPostHome.setVisibility(View.VISIBLE);
        PostService.service.addPost(DataLocalManager.getPrefUser().getAccessToken(),
                arrFilePart,
                contentPart, createTimePart)
                .enqueue(new Callback<AddPostResponse>() {
                    @Override
                    public void onResponse(Call<AddPostResponse> call, Response<AddPostResponse> response) {
                        layoutAddPostHome.setVisibility(View.GONE);
                        if (response.body().getCode() == 1000) {
                            // lấy post đã thêm
                            if (layoutNoData.getVisibility() == View.VISIBLE) {
                                layoutNoData.setVisibility(View.GONE);
                            }

                            onRefresh();
                            //handleGetPostAdded(response.body().getPost_id());

                            NotifyServiceImpl.getInstance()
                                    .setNotify(NotificationActivity.TYPE_NOTIFY_POST,
                                            response.body().getPost_id(),
                                            0, null);

                        } else if (response.body().getCode() == 9993) {
                            // mất token !
                        } else if (response.body().getCode() == 1004) {
                            Toast.makeText(mContext, "Vượt quá dữ liệu quy định !", Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<AddPostResponse> call, Throwable t) {
                        layoutAddPostHome.setVisibility(View.GONE);
                        Toast.makeText(mContext, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void handleGetPostAdded(int postId) {
        PostService.service.getPost(DataLocalManager.getPrefUser().getAccessToken(), postId).enqueue(new Callback<SinglePostResponse>() {
            @Override
            public void onResponse(Call<SinglePostResponse> call, Response<SinglePostResponse> response) {
                if (response.body().getCode() == 1000) {

                } else if (response.body().getCode() == 1004) {
                    Toast.makeText(mContext, "Nội dung không hợp lệ !", Toast.LENGTH_LONG).show();
                } else if (response.body().getCode() == 9993) {
                    Toast.makeText(mContext, "Hết hạn phiên đăng nhập", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SinglePostResponse> call, Throwable t) {
                Toast.makeText(mContext, "Lỗi kết nối, vui lòng thử lại sau", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        MxVideoPlayerWidget.releaseAllVideos();
    }


    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        index = 0;
        mListPost.clear();
        isLastPage = false;

        if (DataLocalManager.getPrefUser().getLinkAvatar() != null) {
            Glide.with(this).load(Host.HOST + DataLocalManager.getPrefUser().getLinkAvatar()).placeholder(R.drawable.unnamed).into(imgAvatar);
        }
        setFirstData();
    }
}