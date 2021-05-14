package com.nampt.socialnetworkproject.view.createGroup.adapter;

import android.content.Context;
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

import java.util.List;

public class CreateGroupAdapter extends RecyclerView.Adapter<CreateGroupAdapter.ViewHolder> {
    private Context mContext;
    private List<Friend> userList;

    public CreateGroupAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setUserList(List<Friend> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_online, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend user = userList.get(position);
        if (user == null) return;
        if (user.getAvatar() != null)
            Glide.with(mContext).load(Host.HOST + user.getAvatar()).placeholder(R.drawable.unnamed).into(holder.imgUser);

    }

    @Override
    public int getItemCount() {
        if (userList == null || userList.isEmpty()) return 0;
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgUser;
        private TextView txtNameUser;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.img_user_online);
             txtNameUser = itemView.findViewById(R.id.txt_name_user_online);
            View onlineView = itemView.findViewById(R.id.view_doing_online);
            onlineView.setVisibility(View.GONE);
            txtNameUser.setVisibility(View.GONE);
        }
    }
}
