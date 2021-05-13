package com.nampt.socialnetworkproject.view.detailPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.commentService.CommentService;
import com.nampt.socialnetworkproject.api.commentService.request.SetCommentRequest;
import com.nampt.socialnetworkproject.api.commentService.response.ListCommentResponse;
import com.nampt.socialnetworkproject.api.commentService.response.SingleCommentResponse;
import com.nampt.socialnetworkproject.api.commentService.response.data.DataSingleComment;
import com.nampt.socialnetworkproject.api.postService.PostService;
import com.nampt.socialnetworkproject.api.postService.response.LikeResponse;
import com.nampt.socialnetworkproject.api.postService.response.SinglePostResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.Comment;
import com.nampt.socialnetworkproject.model.Post;
import com.nampt.socialnetworkproject.util.CalculateTimeUtil;
import com.nampt.socialnetworkproject.util.WindowUtil;
import com.nampt.socialnetworkproject.view.home.rcvAdapter.CommentAdapter;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPostActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    Toolbar toolbar;
    RecyclerView rcvComment;
    CommentAdapter commentAdapter;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imgUser;
    private TextView txtNameUser;
    private TextView timePost;
    private TextView txtContentPost;
    private View container_img_post__1_3;
    private View container_img_post__2_4;
    private View container_video_post;
    private ImageView imgPost1;
    private ImageView imgPost2;
    private ImageView imgPost3;
    private ImageView imgPost4;
    private MxVideoPlayerWidget videoPost;

    private TextView totalLike;
    private TextView totalComment;
    private ImageView imgLike;

    EditText edtComment;
    ImageButton btnSend;

    View layoutNoData, layoutLoading, layoutNoNetwork, layoutContainerAll, layoutSetComment;

    Post mPost = new Post();
    List<Comment> mCommentList = new ArrayList<>();
    int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);
        addControl();
        initToolBar();
        addEvent();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Bình luận");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    private void addControl() {
        toolbar = findViewById(R.id.toolbar_detail_post_activity);
        rcvComment = findViewById(R.id.rcv_comment_detail_post_activity);
        nestedScrollView = findViewById(R.id.nested_scroll_view_detail_post);
        swipeRefreshLayout = findViewById(R.id.swipe_fresh_layout_detail_post);

        imgUser = findViewById(R.id.img_user_detail_post_activity);
        txtNameUser = findViewById(R.id.txt_name_user_detail_post_activity);
        timePost = findViewById(R.id.txt_time_detail_post_activity);
        txtContentPost = findViewById(R.id.txt_content_detail_post_activity);
        container_img_post__1_3 = findViewById(R.id.container_img_1_3_detail_post_activity);
        container_img_post__2_4 = findViewById(R.id.container_img_2_4_detail_post_activity);
        container_video_post = findViewById(R.id.container_video_detail_post_activity);

        imgPost1 = findViewById(R.id.img_1_detail_post_activity);
        imgPost2 = findViewById(R.id.img_2_detail_post_activity);
        imgPost3 = findViewById(R.id.img_3_detail_post_activity);
        imgPost4 = findViewById(R.id.img_4_detail_post_activity);
        videoPost = findViewById(R.id.video_detail_post_activity);

        totalLike = findViewById(R.id.txt_total_like_detail_post_activity);
        totalComment = findViewById(R.id.txt_total_comment_detail_post_activity);
        imgLike = findViewById(R.id.img_like_detail_post_activity);
        edtComment = findViewById(R.id.edt_write_comment_detail_post);
        btnSend = findViewById(R.id.btn_send_comment_detail_post);

        layoutNoData = findViewById(R.id.layout_no_data_detail_post);
        layoutLoading = findViewById(R.id.layout_progress_loading_detail_post);
        layoutNoNetwork = findViewById(R.id.layout_no_network_detail_post);
        layoutContainerAll = findViewById(R.id.layout_container_all_detail_post);
        layoutSetComment = findViewById(R.id.layout_set_comment_detail_post);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueThin));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void addEvent() {
        // set RecycleView + adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        commentAdapter = new CommentAdapter(this);
        commentAdapter.setListComment(null);
        rcvComment.setAdapter(commentAdapter);
        rcvComment.setLayoutManager(layoutManager);

        postId = getIntent().getIntExtra("postId", 0);
        getPost();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSetComment(postId);
            }
        });

    }

    private void getPost() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutContainerAll.setVisibility(View.GONE);
        layoutSetComment.setVisibility(View.GONE);
        layoutNoData.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
        PostService.service.getPost(DataLocalManager.getPrefUser().getAccessToken(), postId)
                .enqueue(new Callback<SinglePostResponse>() {
                    @Override
                    public void onResponse(Call<SinglePostResponse> call, Response<SinglePostResponse> response) {
                        layoutLoading.setVisibility(View.GONE);
                        if (response.body().getCode() == 1000) {
                            if (response.body().getData().isBlock()) {
                                layoutNoData.setVisibility(View.VISIBLE);
                                return;
                            }
                            layoutContainerAll.setVisibility(View.VISIBLE);
                            layoutSetComment.setVisibility(View.VISIBLE);

                            mPost.setId(response.body().getData().getPost().getId());
                            mPost.setContent(response.body().getData().getPost().getContent());
                            mPost.setLinkImage1(response.body().getData().getPost().getLinkImage1());
                            mPost.setLinkImage2(response.body().getData().getPost().getLinkImage2());
                            mPost.setLinkImage3(response.body().getData().getPost().getLinkImage3());
                            mPost.setLinkImage4(response.body().getData().getPost().getLinkImage4());
                            mPost.setLinkVideo(response.body().getData().getPost().getLinkVideo());
                            mPost.setCreateTime(response.body().getData().getPost().getCreateTime());
                            mPost.setTotalComment(response.body().getData().getTotalComment());
                            mPost.setTotalLike(response.body().getData().getTotalLike());
                            mPost.setLiked(response.body().getData().isLiked());
                            mPost.setHidden(response.body().getData().isHidden());
                            mPost.setBlock(response.body().getData().isBlock());
                            mPost.setAuthorId(response.body().getData().getAuthor().getId());
                            mPost.setAuthorName(response.body().getData().getAuthor().getName());
                            mPost.setLinkAvatar(response.body().getData().getAuthor().getLinkAvatar());

                            handleUserProfile(mPost);
                            handlePhotoAndVideoPost(mPost);
                            handleLikeAndComment(mPost);

                            getListComment(postId);

                        } else if (response.body().getCode() == 9992) {
                            layoutNoData.setVisibility(View.VISIBLE);
                            layoutContainerAll.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<SinglePostResponse> call, Throwable t) {
                        layoutLoading.setVisibility(View.GONE);
                        layoutNoData.setVisibility(View.GONE);
                        layoutContainerAll.setVisibility(View.GONE);
                        layoutSetComment.setVisibility(View.GONE);
                        layoutNoNetwork.setVisibility(View.VISIBLE);
                        layoutNoNetwork.findViewById(R.id.btn_reload_network_utils).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutNoNetwork.setVisibility(View.GONE);
                                getPost();
                            }
                        });
                    }
                });
    }

    private void getListComment(int postId) {
        CommentService.service.getListComment(DataLocalManager.getPrefUser().getAccessToken(), postId)
                .enqueue(new Callback<ListCommentResponse>() {
                    @Override
                    public void onResponse(Call<ListCommentResponse> call, Response<ListCommentResponse> response) {
                        if (response.body().getCode() == 1000) {

                            for (DataSingleComment commentData : response.body().getData()) {
                                if (!commentData.isBlock()) {
                                    Comment comment = new Comment();
                                    comment.setId(commentData.getComment().getId());
                                    comment.setContent(commentData.getComment().getContent());
                                    comment.setPostId(commentData.getAuthor().getId());
                                    comment.setAuthorName(commentData.getAuthor().getName());
                                    comment.setLinkAvatar(commentData.getAuthor().getLinkAvatar());
                                    comment.setAuthorId(commentData.getAuthor().getId());
                                    comment.setBlock(commentData.isBlock());
                                    comment.setCreateTime(commentData.getComment().getCreateTime());
                                    mCommentList.add(comment);
                                }
                            }
                            commentAdapter.setListComment(mCommentList);
                        } else if (response.body().getCode() == 9992) {
                            Toast.makeText(DetailPostActivity.this, "Bài viết đã bị chủ bài xóa trước đó", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getCode() == 9993) {
                            Toast.makeText(DetailPostActivity.this, "Sai token !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ListCommentResponse> call, Throwable t) {

                    }
                });
    }

    private void handleUserProfile(Post post) {
        if (post.getLinkAvatar() == null) {
            Glide.with(this).load(R.drawable.unnamed).into(imgUser);
        } else {
            Glide.with(this).load(Host.HOST + post.getLinkAvatar()).placeholder(R.drawable.unnamed).into(imgUser);
        }
        txtNameUser.setText(post.getAuthorName());
    }

    private void handlePhotoAndVideoPost(Post post) {
        timePost.setText(CalculateTimeUtil.getInstance().calculatorTimeCommon(post.getCreateTime(),new Date()));
        txtContentPost.setVisibility(View.VISIBLE);
        container_video_post.setVisibility(View.VISIBLE);
        container_img_post__1_3.setVisibility(View.VISIBLE);
        container_img_post__2_4.setVisibility(View.VISIBLE);

        imgPost3.setVisibility(View.VISIBLE);
        imgPost4.setVisibility(View.VISIBLE);

        if (post.getContent() == null) {
            txtContentPost.setVisibility(View.GONE);
        } else {
            txtContentPost.setText(post.getContent());
        }
        if (post.getLinkVideo() == null) {
            container_video_post.setVisibility(View.GONE);
            if (post.getLinkImage1() == null
                    && post.getLinkImage2() == null
                    && post.getLinkImage3() == null
                    && post.getLinkImage4() == null) {
                container_img_post__1_3.setVisibility(View.GONE);
                container_img_post__2_4.setVisibility(View.GONE);
            } else if (post.getLinkImage1() != null
                    && post.getLinkImage2() == null
                    && post.getLinkImage3() == null
                    && post.getLinkImage4() == null) {
                container_img_post__2_4.setVisibility(View.GONE);
                imgPost3.setVisibility(View.GONE);
                Glide.with(this).load(Host.HOST + post.getLinkImage1()).placeholder(R.drawable.null_bk).into(imgPost1);
            } else if (post.getLinkImage1() != null
                    && post.getLinkImage2() != null
                    && post.getLinkImage3() == null
                    && post.getLinkImage4() == null) {
                imgPost3.setVisibility(View.GONE);
                imgPost4.setVisibility(View.GONE);
//                LinearLayout.LayoutParams lay1 = (LinearLayout.LayoutParams) imgPost1.getLayoutParams();
//                LinearLayout.LayoutParams lay2 = (LinearLayout.LayoutParams) imgPost2.getLayoutParams();
//                lay1.weight = 0.0f;
//                lay2.weight = 0.0f;
//                ViewGroup.LayoutParams params = container_img_post__1_3.getLayoutParams();
//                ViewGroup.LayoutParams params2 = container_img_post__2_4.getLayoutParams();
//                params.height = (int) WindowUtil.getInstance(this).convertDpToPixel(210);
//                params2.height = (int) WindowUtil.getInstance(this).convertDpToPixel(210);

                Glide.with(this).load(Host.HOST + post.getLinkImage1()).placeholder(R.drawable.null_bk).into(imgPost1);
                Glide.with(this).load(Host.HOST + post.getLinkImage2()).placeholder(R.drawable.null_bk).into(imgPost2);
            } else if (post.getLinkImage1() != null
                    && post.getLinkImage2() != null
                    && post.getLinkImage3() != null
                    && post.getLinkImage4() == null) {
                imgPost3.setVisibility(View.GONE);
                Glide.with(this).load(Host.HOST + post.getLinkImage1()).placeholder(R.drawable.null_bk).into(imgPost1);
                Glide.with(this).load(Host.HOST + post.getLinkImage2()).placeholder(R.drawable.null_bk).into(imgPost2);
                Glide.with(this).load(Host.HOST + post.getLinkImage3()).placeholder(R.drawable.null_bk).into(imgPost4);

            } else if (post.getLinkImage1() != null
                    && post.getLinkImage2() != null
                    && post.getLinkImage3() != null
                    && post.getLinkImage4() != null) {
                Glide.with(this).load(Host.HOST + post.getLinkImage1()).placeholder(R.drawable.null_bk).into(imgPost1);
                Glide.with(this).load(Host.HOST + post.getLinkImage2()).placeholder(R.drawable.null_bk).into(imgPost2);
                Glide.with(this).load(Host.HOST + post.getLinkImage3()).placeholder(R.drawable.null_bk).into(imgPost3);
                Glide.with(this).load(Host.HOST + post.getLinkImage4()).placeholder(R.drawable.null_bk).into(imgPost4);
            }
        } else {
            container_img_post__1_3.setVisibility(View.GONE);
            container_img_post__2_4.setVisibility(View.GONE);
            try {
                videoPost.startPlay(Host.HOST + post.getLinkVideo(), MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleLikeAndComment(final Post post) {
        totalLike.setText(post.getTotalLike() + "");
        totalComment.setText(post.getTotalComment() + "");
        if (post.isLiked()) {
            imgLike.setColorFilter(ContextCompat.getColor(this, R.color.colorBlueThin));
        } else {
            imgLike.setColorFilter(ContextCompat.getColor(this, R.color.colorGrey));
        }
        imgLike.setOnClickListener(new View.OnClickListener() {
            boolean isLike = post.isLiked();

            @Override
            public void onClick(View v) {
                if (isLike) {
                    imgLike.setColorFilter(ContextCompat.getColor(DetailPostActivity.this, R.color.colorGreyThin));
                    isLike = false;
                } else {
                    imgLike.setColorFilter(ContextCompat.getColor(DetailPostActivity.this, R.color.colorBlueThin));
                    isLike = true;
                }
                PostService.service.likePost(DataLocalManager.getPrefUser().getAccessToken(), post.getId()).enqueue(new Callback<LikeResponse>() {
                    @Override
                    public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                        if (response.body().getCode() == 1000) {
                            totalLike.setText(String.valueOf(response.body().getTotalLike()));
                        } else if (response.body().getCode() == 9992) {
                            Toast.makeText(DetailPostActivity.this, "Bài viết đã bị xóa trước đó !", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getCode() == 9993) {
                            Toast.makeText(DetailPostActivity.this, "Sai token", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LikeResponse> call, Throwable t) {
                        Toast.makeText(DetailPostActivity.this, "Lỗi mạng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void handleSetComment(int idPost) {
        String content = edtComment.getText().toString().trim();

        if (content.length() <= 0 || content.length() > 500) {
            Toast.makeText(DetailPostActivity.this, "Nội dung không hợp lệ", Toast.LENGTH_SHORT).show();
        } else {
            commentAdapter.addFooterLoading();
            CommentService.service.setComment(
                    DataLocalManager.getPrefUser().getAccessToken(),
                    new SetCommentRequest(idPost, content,new Date()))
                    .enqueue(new Callback<SingleCommentResponse>() {
                @Override
                public void onResponse(Call<SingleCommentResponse> call, Response<SingleCommentResponse> response) {
                    if (response.body().getCode() == 1000) {
                        if (mCommentList.size() == 0) {
                            rcvComment.setVisibility(View.VISIBLE);
                        }
                        Comment comment = new Comment();
                        comment.setId(response.body().getData().getComment().getId());
                        comment.setContent(response.body().getData().getComment().getContent());
                        comment.setPostId(response.body().getData().getAuthor().getId());
                        comment.setAuthorName(response.body().getData().getAuthor().getName());
                        comment.setLinkAvatar(response.body().getData().getAuthor().getLinkAvatar());
                        comment.setAuthorId(response.body().getData().getAuthor().getId());
                        comment.setBlock(response.body().getData().isBlock());
                        comment.setCreateTime(response.body().getData().getComment().getCreateTime());

                        commentAdapter.removeFooterLoading();
                        mCommentList.add(comment);
                        commentAdapter.setListComment(mCommentList);
                        edtComment.setText(null);
                        totalComment.setText(mCommentList.size() + "");
                        nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).getHeight());
                    } else if (response.body().getCode() == 1004) {
                        Toast.makeText(DetailPostActivity.this, "Nội dung không hợp lệ", Toast.LENGTH_SHORT).show();
                    } else if (response.body().getCode() == 9992) {
                        Toast.makeText(DetailPostActivity.this, "Bài viết đã bị chủ bài xóa trước đó", Toast.LENGTH_SHORT).show();
                    } else if (response.body().getCode() == 9993) {
                        Toast.makeText(DetailPostActivity.this, "Sai token !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SingleCommentResponse> call, Throwable t) {
                    commentAdapter.removeFooterLoading();
                    Toast.makeText(DetailPostActivity.this, "Lỗi mạng, vui lòng thử lại !", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        MxVideoPlayerWidget.releaseAllVideos();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (MxVideoPlayerWidget.backPress()) return;
        super.onBackPressed();
    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        mCommentList.clear();

        getPost();
    }
}