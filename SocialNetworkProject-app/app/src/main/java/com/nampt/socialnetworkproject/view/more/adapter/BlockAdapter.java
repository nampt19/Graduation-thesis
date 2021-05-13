package com.nampt.socialnetworkproject.view.more.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.model.Friend;

import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder> {
    private Context mContext;
    private List<Friend> mFriendList;
    ItemCLickListener cLickListener;

    public interface ItemCLickListener {
        void onItemClicked(int userId,String username);
    }

    public BlockAdapter(Context mContext, ItemCLickListener clickListener) {
        this.cLickListener = clickListener;
        this.mContext = mContext;
    }

    public void setFriendList(List<Friend> mFriendList) {
        this.mFriendList = mFriendList;
    }

    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_block, parent, false);
        return new BlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockViewHolder holder, final int position) {
        final Friend friend = mFriendList.get(position);
        if (friend == null) return;
        holder.txtName.setText(friend.getName());
        if (friend.getAvatar() != null) {
            Glide.with(mContext).load(Host.HOST + friend.getAvatar()).placeholder(R.drawable.null_bk).into(holder.imgAvatar);
        } else {
            Glide.with(mContext).load(R.drawable.unnamed).into(holder.imgAvatar);
        }
        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cLickListener.onItemClicked(friend.getId(),friend.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mFriendList == null || mFriendList.isEmpty()) return 0;
        return mFriendList.size();
    }

    public class BlockViewHolder extends RecyclerView.ViewHolder {
        View layoutParent;
        ImageView imgAvatar;
        TextView txtName;

        public BlockViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutParent = itemView.findViewById(R.id.layout_item_block);
            imgAvatar = itemView.findViewById(R.id.img_friend_block);
            txtName = itemView.findViewById(R.id.txt_name_friend_block);
        }
    }
}
