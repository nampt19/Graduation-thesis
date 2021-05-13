package com.nampt.socialnetworkproject.view.friend.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.friendService.FriendService;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.Friend;
import com.nampt.socialnetworkproject.view.message.MessageActivity;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Friend> mFriendList;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING = 2;
    boolean isLoading;


    public FriendAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setFriendList(List<Friend> friendList) {
        this.mFriendList = friendList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_tap_1, parent, false);
            return new TotalFriendViewHolder(view);
        } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == TYPE_ITEM) {
            Friend friend = mFriendList.get(position);
            if (friend == null || friend.getId() == -1) return;
            TotalFriendViewHolder holder = (TotalFriendViewHolder) viewHolder;

            if (friend.getAvatar() != null) {
                Glide.with(mContext).load(Host.HOST + friend.getAvatar())
                        .placeholder(R.drawable.null_bk)
                        .into(holder.imgFriend);
            } else {
                Glide.with(mContext).load(R.drawable.unnamed).into(holder.imgFriend);
            }

            holder.txtNameFriend.setText(friend.getName());
            holder.imgFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProfileUserActivity.class);
                    intent.putExtra("userId", friend.getId());
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                }
            });

            View viewDialogMe = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_bottom_sheet_more_friend_tap_1, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
            bottomSheetDialog.setContentView(viewDialogMe);
            holder.imgMoreHorizontal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.show();
                }
            });
            viewDialogMe.findViewById(R.id.item_chat_friend_tap_1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("partnerId", friend.getId());
                    intent.putExtra("roomName", friend.getName());
                    intent.putExtra("isRoom", false);
                    mContext.startActivity(intent);
                    bottomSheetDialog.hide();
                }
            });
            viewDialogMe.findViewById(R.id.item_delete_friend_tap_1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleDeleteFriend(friend.getId(), holder.getAdapterPosition());
                    bottomSheetDialog.hide();
                }
            });
            viewDialogMe.findViewById(R.id.item_block_user_friend_tap_1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleBlockUser(friend.getId(), holder.getAdapterPosition());
                    bottomSheetDialog.hide();
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (mFriendList == null || mFriendList.isEmpty()) return 0;
        return mFriendList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mFriendList != null && position == mFriendList.size() - 1 && isLoading)
            return TYPE_LOADING;
        return TYPE_ITEM;
    }

    private void handleBlockUser(int userId, int adapterPosition) {
        FriendService.service.block(DataLocalManager.getPrefUser().getAccessToken(), userId)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            Toast.makeText(mContext, "Chặn thành công", Toast.LENGTH_SHORT).show();
                            mFriendList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                        }
                        if (response.body().getCode() == 9992) {
                            Toast.makeText(mContext, "Người dùng không tồn tại !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(mContext, "Lỗi kết nối !", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void handleDeleteFriend(int userId, int adapterPosition) {
        FriendService.service.deleteFriend(DataLocalManager.getPrefUser().getAccessToken(), userId)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            Toast.makeText(mContext, "Xóa bạn thành công", Toast.LENGTH_SHORT).show();
                            mFriendList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                        }
                        if (response.body().getCode() == 9992) {
                            Toast.makeText(mContext, "Người dùng không tồn tại !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(mContext, "Lỗi kết nối !", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class TotalFriendViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgFriend;
        private TextView txtNameFriend;
        private ImageView imgMoreHorizontal;

        public TotalFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFriend = itemView.findViewById(R.id.img_friend);
            txtNameFriend = itemView.findViewById(R.id.txt_name_friend);
            imgMoreHorizontal = itemView.findViewById(R.id.ic_horizontal_item_tap_1);
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
        isLoading = true;
        if (mFriendList == null) {
            mFriendList = new ArrayList<>();
        }
        mFriendList.add(new Friend(-1));
        notifyItemInserted(mFriendList.size() - 1);
    }

    public void removeFooterLoading() {
        isLoading = false;
        int position = mFriendList.size() - 1;
        Friend friend = mFriendList.get(position);
        if (friend != null && friend.getId() == -1) {
            mFriendList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
