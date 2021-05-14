package com.nampt.socialnetworkproject.view.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.model.Friend;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;

import java.util.List;

public class UserOnlineAdapter extends RecyclerView.Adapter<UserOnlineAdapter.UserOnlineViewHolder> {
    private Context mContext;
    private List<Friend> userOnlineList;

    public UserOnlineAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setUserOnlineList(List<Friend> friendList) {
        this.userOnlineList = friendList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserOnlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userOnlineView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_online, parent, false);
        return new UserOnlineViewHolder(userOnlineView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOnlineViewHolder holder, int position) {
        Friend user = userOnlineList.get(position);
        if (user == null) return;
        if (user.getAvatar() != null)
            Glide.with(mContext).load(Host.HOST + user.getAvatar()).placeholder(R.drawable.unnamed).into(holder.imgUser);
        holder.txtNameUser.setText(user.getName());
        holder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileUserActivity.class);
                intent.putExtra("userId", user.getId());
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (userOnlineList == null || userOnlineList.isEmpty()) return 0;
        return userOnlineList.size();
    }

    public class UserOnlineViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgUser;
        private TextView txtNameUser;

        public UserOnlineViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.img_user_online);
            txtNameUser = itemView.findViewById(R.id.txt_name_user_online);
        }
    }
}
