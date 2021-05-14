package com.nampt.socialnetworkproject.view.friend.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.friendService.FriendService;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.Friend;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendSentAdapter extends RecyclerView.Adapter<FriendSentAdapter.FriendSentViewHolder> {
    private Context mContext;
    private List<Friend> mFriendList;

    public FriendSentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setFriendList(List<Friend> friendList) {
        this.mFriendList = friendList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendSentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_tap_3, parent, false);
        return new FriendSentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendSentViewHolder holder, int position) {
        final Friend friend = mFriendList.get(position);
        if (friend == null) return;
        if (friend.getAvatar() != null) {
            Glide.with(mContext).load(Host.HOST + friend.getAvatar())
                    .placeholder(R.drawable.null_bk).into(holder.imgFriend);
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
        holder.btnRemoveRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeleteSentFriend(friend.getId(), holder.getAdapterPosition());
            }
        });
    }

    private void handleDeleteSentFriend(int friendId, final int position) {
        FriendService.service.deleteInvitationSentFriend(DataLocalManager.getPrefUser().getAccessToken(), friendId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().getCode() == 1000) {
                    mFriendList.remove(position);
                    FriendSentAdapter.this.notifyItemRemoved(position);
                    Toast.makeText(mContext, "Xóa lời mời thành công", Toast.LENGTH_SHORT).show();
                } else if (response.body().getCode() == 9992) {
                    Toast.makeText(mContext, "Yêu cầu đã bị xóa trước đó", Toast.LENGTH_SHORT).show();
                } else if (response.body().getCode() == 9993) {
                    Toast.makeText(mContext, "Sai token !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(mContext, "Lỗi kết nối, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mFriendList == null || mFriendList.isEmpty()) return 0;
        return mFriendList.size();
    }

    public class FriendSentViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgFriend;
        private TextView txtNameFriend;
        private Button btnRemoveRequest;

        public FriendSentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFriend = itemView.findViewById(R.id.img_friend_sent);
            txtNameFriend = itemView.findViewById(R.id.txt_name_friend_sent);
            btnRemoveRequest = itemView.findViewById(R.id.btn_destroy_request);
        }
    }
}
