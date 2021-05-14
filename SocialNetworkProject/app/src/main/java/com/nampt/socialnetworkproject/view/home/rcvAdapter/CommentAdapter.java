package com.nampt.socialnetworkproject.view.home.rcvAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.commentService.CommentService;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.Comment;
import com.nampt.socialnetworkproject.util.CalculateTimeUtil;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;
import com.nampt.socialnetworkproject.view.search.SearchActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Comment> mListComment;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING = 2;
    boolean isLoadingAdd;


    public CommentAdapter(Context mContext) {
        this.mContext = mContext;

    }

    public void setListComment(List<Comment> mListComment) {
        this.mListComment = mListComment;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(itemView);
        } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            final Comment comment = mListComment.get(position);
            if (comment == null || comment.getId() == -1) return;
            final CommentViewHolder viewHolder = (CommentViewHolder) holder;
            if (comment.getLinkAvatar() == null) {
                Glide.with(mContext).load(R.drawable.unnamed).into(viewHolder.urlImgUser);
            } else {
                Glide.with(mContext).load(Host.HOST + comment.getLinkAvatar()).placeholder(R.drawable.unnamed).into(viewHolder.urlImgUser);
            }
            viewHolder.txtUsername.setText(comment.getAuthorName());
            viewHolder.txtContent.setText(comment.getContent());
            viewHolder.txtTime.setText(CalculateTimeUtil.getInstance().calculatorTimeCommon(comment.getCreateTime(),new Date()));

            viewBinderHelper.bind(viewHolder.swipeRevealLayout, String.valueOf(comment.getId()));
            viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleDeleteComment(viewHolder, comment.getId(), comment.getAuthorId(), viewHolder.getAdapterPosition());
                }
            });
            viewHolder.urlImgUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProfileUserActivity.class);
                    intent.putExtra("userId", comment.getAuthorId());
                     mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                }
            });

        }

    }

    @Override
    public int getItemCount() {
        if (mListComment == null || mListComment.isEmpty()) return 0;
        return mListComment.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mListComment != null && position == mListComment.size() - 1 && isLoadingAdd)
            return TYPE_LOADING;
        return TYPE_ITEM;
    }


    private void handleDeleteComment(final CommentViewHolder viewHolder, int idComment, int authorId, final int adapterPosition) {
        if (DataLocalManager.getPrefUser().getId() != authorId) {
            Toast.makeText(mContext, "Không thể xóa bình luận người khác", Toast.LENGTH_LONG).show();
            return;
        }
        viewHolder.imgDelete.setVisibility(View.GONE);
        viewHolder.imgLoadingDelete.setVisibility(View.VISIBLE);
        CommentService.service.deleteComment(DataLocalManager.getPrefUser().getAccessToken(), idComment).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().getCode() == 1000) {
                    viewHolder.imgDelete.setVisibility(View.VISIBLE);
                    viewHolder.imgLoadingDelete.setVisibility(View.GONE);
                    mListComment.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                    Toast.makeText(mContext, "Đã xóa bình luận !", Toast.LENGTH_SHORT).show();
                } else if (response.body().getCode() == 9992) {
                    Toast.makeText(mContext, "Bài viết đã bị chủ bài xóa trước đó", Toast.LENGTH_SHORT).show();
                } else if (response.body().getCode() == 9993) {
                    Toast.makeText(mContext, "Sai token !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                viewHolder.imgDelete.setVisibility(View.VISIBLE);
                viewHolder.imgLoadingDelete.setVisibility(View.GONE);
                Toast.makeText(mContext, "Lỗi mạng, vui lòng thử lại !", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private ImageView urlImgUser;
        private TextView txtUsername, txtContent, txtTime;
        private SwipeRevealLayout swipeRevealLayout;
        private ImageView imgDelete;
        private ProgressBar imgLoadingDelete;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            urlImgUser = itemView.findViewById(R.id.url_img_username_comment);
            txtUsername = itemView.findViewById(R.id.txt_username_comment);
            txtContent = itemView.findViewById(R.id.txt_content_comment);
            txtTime = itemView.findViewById(R.id.txt_time_comment);
            swipeRevealLayout = itemView.findViewById(R.id.swipeRevealLayoutComment);
            imgDelete = itemView.findViewById(R.id.img_delete_comment);
            imgLoadingDelete = itemView.findViewById(R.id.progress_bar_delete_comment);
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
        if (mListComment == null) {
            mListComment = new ArrayList<>();
        }
        mListComment.add(new Comment(-1));
        notifyItemInserted(mListComment.size() - 1);
    }

    public void removeFooterLoading() {
        isLoadingAdd = false;
        int position = mListComment.size() - 1;
        Comment comment = mListComment.get(position);
        if (comment != null && comment.getId() == -1) {
            mListComment.remove(position);
            notifyItemRemoved(position);
        }
    }
}
