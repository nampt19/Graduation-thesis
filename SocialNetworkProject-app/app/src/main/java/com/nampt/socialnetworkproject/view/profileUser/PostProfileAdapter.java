package com.nampt.socialnetworkproject.view.profileUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostProfileAdapter extends RecyclerView.Adapter<PostProfileAdapter.PostProfileViewHolder> implements Serializable {
    private Context mConText;
    private List<Post> mPostList;
    BottomSheetDialog sheetDialogMe;

    public PostProfileAdapter(Context mConText) {
        this.mConText = mConText;
    }

    public void setPostList(List<Post> mPostList) {
        this.mPostList = mPostList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mConText).inflate(R.layout.item_post, parent, false);
        return new PostProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostProfileViewHolder viewHolder, int position) {
        Post post = mPostList.get(position);
        if (post == null) return;
        handleUserProfile(viewHolder, post);
        handlePhotoAndVideoPost(viewHolder, post);
        handleLikeAndComment(viewHolder, post);
        openCommentDialog(viewHolder, post);
        openMoreDialog(viewHolder, post);
    }

    private void openMoreDialog(final PostProfileViewHolder holder, final Post post) {
        if (post.getAuthorId() == DataLocalManager.getPrefUser().getId()) {
            holder.imgMore.setVisibility(View.VISIBLE);
            holder.imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    View viewDialogMe = ((Activity) mConText).getLayoutInflater().inflate(R.layout.layout_bottom_sheet_more_item_post_type_me, null);
                    sheetDialogMe = new BottomSheetDialog(mConText);
                    sheetDialogMe.setContentView(viewDialogMe);
                    sheetDialogMe.show();
                    handleDeletePost(viewDialogMe, post, holder.getAdapterPosition());
                    handleEditPost(viewDialogMe, post, holder.getAdapterPosition());

                }
            });
        } else {
            holder.imgMore.setVisibility(View.GONE);
        }

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
                ((Activity) mConText).startActivityForResult(intent, HomeActivity.CODE_EDIT_POST);
                ((Activity) mConText).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                sheetDialogMe.hide();
            }
        });
    }


    private void handleDeletePost(View viewDialog, final Post post, final int adapterPosition) {
        viewDialog.findViewById(R.id.delete_item_post_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostService.service.deletePost(DataLocalManager.getPrefUser().getAccessToken(), post.getId()).enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            mPostList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            Toast.makeText(mConText, "Đã xóa bài viết", Toast.LENGTH_SHORT).show();
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

                    }
                });
            }
        });

    }

    private void openCommentDialog(final PostProfileViewHolder holder, final Post post) {
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

    private void handleLikeAndComment(final PostProfileViewHolder holder, final Post post) {
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

    private void handlePhotoAndVideoPost(PostProfileViewHolder holder, Post post) {
        holder.timePost.setText(CalculateTimeUtil.getInstance().calculatorTimeCommon(post.getCreateTime(),new Date()));
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

    private void handleUserProfile(PostProfileViewHolder holder, Post post) {
        if (post.getLinkAvatar() == null) {
            Glide.with(mConText).load(R.drawable.unnamed).into(holder.imgUser);
        } else {
            Glide.with(mConText).load(Host.HOST + post.getLinkAvatar()).placeholder(R.drawable.unnamed).into(holder.imgUser);
        }
        holder.txtNameUser.setText(post.getAuthorName());
    }

    @Override
    public int getItemCount() {
        if (mPostList == null || mPostList.isEmpty()) return 0;
        return mPostList.size();
    }

    protected class PostProfileViewHolder extends RecyclerView.ViewHolder {
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

        public PostProfileViewHolder(@NonNull View itemView) {
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
}
