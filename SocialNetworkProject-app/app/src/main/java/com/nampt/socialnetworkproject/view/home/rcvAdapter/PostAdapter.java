package com.nampt.socialnetworkproject.view.home.rcvAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.Edits;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.postService.PostService;
import com.nampt.socialnetworkproject.api.postService.response.LikeResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.Post;
import com.nampt.socialnetworkproject.util.CalculateTimeUtil;
import com.nampt.socialnetworkproject.util.WindowUtil;
import com.nampt.socialnetworkproject.view.detailPost.DetailPostActivity;
import com.nampt.socialnetworkproject.view.home.EditPostActivity;
import com.nampt.socialnetworkproject.view.home.HomeActivity;
import com.nampt.socialnetworkproject.view.more.BlockListActivity;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Serializable {
    private Context mConText;
    private List<Post> mPostList;
    BottomSheetDialog sheetDialogFriend;
    BottomSheetDialog sheetDialogMe;
    private Fragment mFragment;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING = 2;
    boolean isLoadingAdd;

    public PostAdapter(Context mConText) {
        this.mConText = mConText;
    }

    public void setFragment(Fragment mFragment) {
        this.mFragment = mFragment;
    }

    public void setPostList(List<Post> mPostList) {
        this.mPostList = mPostList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View postView = inflater.inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(postView);
        } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            Post post = mPostList.get(position);
            if (post == null || post.getId() == -1) return;
            PostViewHolder viewHolder = (PostViewHolder) holder;

            handleUserProfile(viewHolder, post);
            handlePhotoAndVideoPost(viewHolder, post);
            handleLikeAndComment(viewHolder, post);
            openCommentDialog(viewHolder, post);
            openMoreDialog(viewHolder, post);
        }
    }

    private void openMoreDialog(final PostViewHolder holder, final Post post) {
        holder.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.getAuthorId() == DataLocalManager.getPrefUser().getId()) {
                    View viewDialogMe = ((Activity) mConText).getLayoutInflater().inflate(R.layout.layout_bottom_sheet_more_item_post_type_me, null);
                    sheetDialogMe = new BottomSheetDialog(mConText);
                    sheetDialogMe.setContentView(viewDialogMe);
                    sheetDialogMe.show();
                    handleDeletePost(viewDialogMe, post, holder.getAdapterPosition());
                    handleEditPost(viewDialogMe, post, holder.getAdapterPosition());
                } else {
                    View viewDialogFriend = ((Activity) mConText).getLayoutInflater().inflate(R.layout.layout_bottom_sheet_more_item_post_type_friend, null);
                    sheetDialogFriend = new BottomSheetDialog(mConText);
                    sheetDialogFriend.setContentView(viewDialogFriend);
                    sheetDialogFriend.show();
                    handleHiddenPost(viewDialogFriend, post, holder.getAdapterPosition());
                }
            }
        });
    }

    private void handleEditPost(View viewDialogMe, final Post post, final int position) {
        viewDialogMe.findViewById(R.id.edit_item_post_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mConText, EditPostActivity.class);
                intent.putExtra("idPost", post.getId());
                intent.putExtra("contentPost", post.getContent());
                intent.putExtra("img1", post.getLinkImage1());
                intent.putExtra("img2", post.getLinkImage2());
                intent.putExtra("img3", post.getLinkImage3());
                intent.putExtra("img4", post.getLinkImage4());
                intent.putExtra("video", post.getLinkVideo());
                mFragment.startActivityForResult(intent, HomeActivity.CODE_EDIT_POST);
                ((Activity) mConText).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                sheetDialogMe.hide();
            }
        });
    }

    private void handleHiddenPost(View viewDialogFriend, final Post post, final int adapterPosition) {
        viewDialogFriend.findViewById(R.id.hidden_item_post_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostService.service.hiddenPost(DataLocalManager.getPrefUser().getAccessToken(), post.getId()).enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            mPostList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            PostAdapter.this.notifyDataSetChanged();
                            Toast.makeText(mConText, "Đã ẩn bài viết", Toast.LENGTH_SHORT).show();
                            deletePostsCache(post.getId());

                        } else if (response.body().getCode() == 9992) {
                            Toast toast = Toast.makeText(mConText, "Bài viết đã bị xóa trước đó !", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else if (response.body().getCode() == 9993) {
                            Toast toast = Toast.makeText(mConText, "Sai token !", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        sheetDialogFriend.dismiss();
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        sheetDialogMe.dismiss();
                        Toast toast = Toast.makeText(mConText, "Lỗi kết nối !", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 20);
                        toast.show();
                    }
                });
            }
        });
    }

    private void handleDeletePost(View viewDialog, final Post post, final int adapterPosition) {
        viewDialog.findViewById(R.id.delete_item_post_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mConText)
                        .setMessage("Bạn chắc chắn muốn xóa bài viết ?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                PostService.service.deletePost(DataLocalManager.getPrefUser().getAccessToken(), post.getId())
                                        .enqueue(new Callback<BaseResponse>() {
                                            @Override
                                            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                                if (response.body().getCode() == 1000) {
                                                    mPostList.remove(adapterPosition);
                                                    notifyItemRemoved(adapterPosition);
                                                    Toast.makeText(mConText, "Đã xóa bài viết", Toast.LENGTH_SHORT).show();
                                                    deletePostsCache(post.getId());

                                                } else if (response.body().getCode() == 9992) {
                                                    Toast toast = Toast.makeText(mConText, "Bài viết đã bị xóa trước đó !", Toast.LENGTH_SHORT);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                } else if (response.body().getCode() == 9993) {
                                                    Toast toast = Toast.makeText(mConText, "Sai token !", Toast.LENGTH_SHORT);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                }
                                                sheetDialogMe.dismiss();
                                            }

                                            @Override
                                            public void onFailure(Call<BaseResponse> call, Throwable t) {
                                                sheetDialogMe.dismiss();
                                                Toast toast = Toast.makeText(mConText, "Lỗi kết nối !", Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.BOTTOM, 0, 20);
                                                toast.show();
                                            }
                                        });

                            }
                        }).setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FFFF0400"));

            }
        });

    }

    private void deletePostsCache(int idPost) {
        List<Post> postsLocal = DataLocalManager.getPrefPostList();
        for (Post p : postsLocal) {
            if (p.getId() == idPost) {
                postsLocal.remove(p);
                break;
            }
        }
        DataLocalManager.setPrefListPost(postsLocal);
    }

    private void openCommentDialog(final PostViewHolder holder, final Post post) {
        holder.imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mConText, DetailPostActivity.class);
                intent.putExtra("postId", post.getId());
                mConText.startActivity(intent);
                ((Activity) mConText).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            }
        });
    }

    private void handleLikeAndComment(final PostViewHolder holder, final Post post) {
        holder.totalLike.setText(post.getTotalLike() + "");
        holder.totalComment.setText(post.getTotalComment() + "");
        if (post.isLiked()) {
            holder.imgLike.setColorFilter(ContextCompat.getColor(mConText, R.color.colorBlueThin));
        } else {
            holder.imgLike.setColorFilter(ContextCompat.getColor(mConText, R.color.colorGrey));
        }
        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            boolean isLike = post.isLiked();

            @Override
            public void onClick(View v) {
                if (isLike) {
                    holder.imgLike.setColorFilter(ContextCompat.getColor(mConText, R.color.colorGreyThin));
                    isLike = false;
                } else {
                    holder.imgLike.setColorFilter(ContextCompat.getColor(mConText, R.color.colorBlueThin));
                    isLike = true;
                }
                PostService.service.likePost(DataLocalManager.getPrefUser().getAccessToken(), post.getId()).enqueue(new Callback<LikeResponse>() {
                    @Override
                    public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                        if (response.body().getCode() == 1000) {
                            holder.totalLike.setText(String.valueOf(response.body().getTotalLike()));
                        } else if (response.body().getCode() == 9992) {
                            Toast.makeText(mConText, "Bài viết đã bị xóa trước đó !", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getCode() == 9993) {
                            Toast.makeText(mConText, "Sai token", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LikeResponse> call, Throwable t) {
                        Toast.makeText(mConText, "Lỗi mạng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void handlePhotoAndVideoPost(PostViewHolder holder, Post post) {
        holder.timePost.setText(CalculateTimeUtil.getInstance().calculatorTimeCommon(post.getCreateTime(), new Date()));
        holder.txtContentPost.setVisibility(View.VISIBLE);
        holder.container_video_post.setVisibility(View.VISIBLE);
        holder.container_img_post__1_3.setVisibility(View.VISIBLE);
        holder.container_img_post__2_4.setVisibility(View.VISIBLE);
        holder.imgPost3.setVisibility(View.VISIBLE);
        holder.imgPost4.setVisibility(View.VISIBLE);

        if (post.getContent() == null) {
            holder.txtContentPost.setVisibility(View.GONE);
        } else {
            holder.txtContentPost.setText(post.getContent());
        }
        if (post.getLinkVideo() == null) {
            holder.container_video_post.setVisibility(View.GONE);
            if (post.getLinkImage1() == null
                    && post.getLinkImage2() == null
                    && post.getLinkImage3() == null
                    && post.getLinkImage4() == null) {
                holder.container_img_post__1_3.setVisibility(View.GONE);
                holder.container_img_post__2_4.setVisibility(View.GONE);
            } else if (post.getLinkImage1() != null
                    && post.getLinkImage2() == null
                    && post.getLinkImage3() == null
                    && post.getLinkImage4() == null) {
                holder.container_img_post__2_4.setVisibility(View.GONE);
                holder.imgPost3.setVisibility(View.GONE);
                Glide.with(mConText).load(Host.HOST + post.getLinkImage1()).placeholder(R.drawable.null_bk).into(holder.imgPost1);
            } else if (post.getLinkImage1() != null
                    && post.getLinkImage2() != null
                    && post.getLinkImage3() == null
                    && post.getLinkImage4() == null) {
                holder.imgPost3.setVisibility(View.GONE);
                holder.imgPost4.setVisibility(View.GONE);
                LinearLayout.LayoutParams lay1 = (LinearLayout.LayoutParams) holder.imgPost1.getLayoutParams();
                LinearLayout.LayoutParams lay2 = (LinearLayout.LayoutParams) holder.imgPost2.getLayoutParams();
                lay1.weight = 0.0f;
                lay2.weight = 0.0f;
                ViewGroup.LayoutParams params = holder.container_img_post__1_3.getLayoutParams();
                ViewGroup.LayoutParams params2 = holder.container_img_post__2_4.getLayoutParams();
                params.height = (int) WindowUtil.getInstance(mConText).convertDpToPixel(210);
                params2.height = (int) WindowUtil.getInstance(mConText).convertDpToPixel(210);

                Glide.with(mConText).load(Host.HOST + post.getLinkImage1()).placeholder(R.drawable.null_bk).into(holder.imgPost1);
                Glide.with(mConText).load(Host.HOST + post.getLinkImage2()).placeholder(R.drawable.null_bk).into(holder.imgPost2);
            } else if (post.getLinkImage1() != null
                    && post.getLinkImage2() != null
                    && post.getLinkImage3() != null
                    && post.getLinkImage4() == null) {
                holder.imgPost3.setVisibility(View.GONE);
                Glide.with(mConText).load(Host.HOST + post.getLinkImage1()).placeholder(R.drawable.null_bk).into(holder.imgPost1);
                Glide.with(mConText).load(Host.HOST + post.getLinkImage2()).placeholder(R.drawable.null_bk).into(holder.imgPost2);
                Glide.with(mConText).load(Host.HOST + post.getLinkImage3()).placeholder(R.drawable.null_bk).into(holder.imgPost4);

            } else if (post.getLinkImage1() != null
                    && post.getLinkImage2() != null
                    && post.getLinkImage3() != null
                    && post.getLinkImage4() != null) {
                Glide.with(mConText).load(Host.HOST + post.getLinkImage1()).placeholder(R.drawable.null_bk).into(holder.imgPost1);
                Glide.with(mConText).load(Host.HOST + post.getLinkImage2()).placeholder(R.drawable.null_bk).into(holder.imgPost2);
                Glide.with(mConText).load(Host.HOST + post.getLinkImage3()).placeholder(R.drawable.null_bk).into(holder.imgPost3);
                Glide.with(mConText).load(Host.HOST + post.getLinkImage4()).placeholder(R.drawable.null_bk).into(holder.imgPost4);
            }
        } else {
            holder.container_img_post__1_3.setVisibility(View.GONE);
            holder.container_img_post__2_4.setVisibility(View.GONE);
            try {
                holder.videoPost.startPlay(Host.HOST + post.getLinkVideo(), MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleUserProfile(PostViewHolder holder, Post post) {
        if (post.getLinkAvatar() == null) {
            Glide.with(mConText).load(R.drawable.unnamed).into(holder.imgUser);
        } else {
            Glide.with(mConText).load(Host.HOST + post.getLinkAvatar()).placeholder(R.drawable.unnamed).into(holder.imgUser);
        }
        holder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mConText, ProfileUserActivity.class);
                intent.putExtra("userId", post.getAuthorId());
                mConText.startActivity(intent);
                ((Activity) mConText).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            }
        });
        holder.txtNameUser.setText(post.getAuthorName());
    }

    @Override
    public int getItemCount() {
        if (mPostList == null || mPostList.isEmpty()) return 0;
        return mPostList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mPostList != null && position == mPostList.size() - 1 && isLoadingAdd)
            return TYPE_LOADING;
        return TYPE_ITEM;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
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
        private ImageView imgLike, imgComment, imgMore;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.img_user_post);
            txtNameUser = itemView.findViewById(R.id.txt_name_user_post);
            timePost = itemView.findViewById(R.id.txt_time_post);
            txtContentPost = itemView.findViewById(R.id.txt_content_post);
            container_img_post__1_3 = itemView.findViewById(R.id.container_img_post__1_3);
            container_img_post__2_4 = itemView.findViewById(R.id.container_img_post_2_4);
            container_video_post = itemView.findViewById(R.id.container_video_post);
            imgPost1 = itemView.findViewById(R.id.img_post_1);
            imgPost2 = itemView.findViewById(R.id.img_post_2);
            imgPost3 = itemView.findViewById(R.id.img_post_3);
            imgPost4 = itemView.findViewById(R.id.img_post_4);
            videoPost = itemView.findViewById(R.id.video_post);

            totalLike = itemView.findViewById(R.id.txt_total_like_post);
            totalComment = itemView.findViewById(R.id.txt_total_comment_post);
            imgLike = itemView.findViewById(R.id.img_like_post);

            imgComment = itemView.findViewById(R.id.img_comment_post);
            imgMore = itemView.findViewById(R.id.img_more_item_post);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar_loading_item);
        }
    }

    public void addFooterLoading() {
        isLoadingAdd = true;
        if (mPostList == null) {
            mPostList = new ArrayList<>();
        }
        mPostList.add(new Post(-1));
        notifyItemInserted(mPostList.size() - 1);
    }

    public void removeFooterLoading() {
        isLoadingAdd = false;
        int position = mPostList.size() - 1;
        Post post = mPostList.get(position);
        if (post != null && post.getId() == -1) {
            mPostList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
