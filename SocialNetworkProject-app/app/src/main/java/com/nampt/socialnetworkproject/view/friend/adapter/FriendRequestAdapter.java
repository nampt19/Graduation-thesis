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

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHoler> {
    private Context mContext;
    private List<Friend> mFriendList;

    public FriendRequestAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setFriendList(List<Friend> friendList) {
        this.mFriendList = friendList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendRequestViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_tap_2, parent, false);
        return new FriendRequestViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendRequestViewHoler holder, int position) {
        final Friend friend = mFriendList.get(position);
        if (friend == null) return;
        if (friend.getAvatar() != null) {
            Glide.with(mContext).load(Host.HOST + friend.getAvatar()).into(holder.imgFriend);
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
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSetAcceptFriend(friend.getId(), 1, holder.getAdapterPosition());
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSetAcceptFriend(friend.getId(), 0, holder.getAdapterPosition());
            }
        });

    }

    private void handleSetAcceptFriend(int friendId, final int isAccept, final int position) {
        FriendService.service.setAcceptFriend(DataLocalManager.getPrefUser().getAccessToken(), friendId, isAccept).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().getCode() == 1000) {
                    mFriendList.remove(position);
                    FriendRequestAdapter.this.notifyItemRemoved(position);
                    if (isAccept != 0) {
                        Toast.makeText(mContext, "Thêm bạn thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Xóa yêu cầu thành công", Toast.LENGTH_SHORT).show();
                    }
                }else if (response.body().getCode() == 9996) {
                    Toast.makeText(mContext, "Người dùng đã bị chặn", Toast.LENGTH_SHORT).show();
                }  else if (response.body().getCode() == 9994) {
                    Toast.makeText(mContext, "2 người đã là bạn", Toast.LENGTH_SHORT).show();
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

    public class FriendRequestViewHoler extends RecyclerView.ViewHolder {
        private CircleImageView imgFriend;
        private TextView txtNameFriend;
        private Button btnAccept, btnDelete;

        public FriendRequestViewHoler(@NonNull View itemView) {
            super(itemView);
            imgFriend = itemView.findViewById(R.id.img_friend_request);
            txtNameFriend = itemView.findViewById(R.id.txt_name_friend_request);
            btnAccept = itemView.findViewById(R.id.btn_accept_request);
            btnDelete = itemView.findViewById(R.id.btn_delete_request);
        }
    }
}
