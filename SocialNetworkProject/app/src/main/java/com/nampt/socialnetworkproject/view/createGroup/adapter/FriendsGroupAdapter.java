package com.nampt.socialnetworkproject.view.createGroup.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.model.Friend;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsGroupAdapter extends RecyclerView.Adapter<FriendsGroupAdapter.ViewHolder> {
    private Context mContext;
    private List<Friend> friendList;
    private IListener itemListener;

    public interface IListener {
        void onItemChecked(Friend friend, boolean isChecked);
    }

    public FriendsGroupAdapter(Context mContext, IListener itemListener) {
        this.mContext = mContext;
        this.itemListener = itemListener;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Friend friend = friendList.get(position);
        if (friend == null) return;

        holder.checkAddGroup.setChecked(false);

        if (friend.getAvatar() != null)
            Glide.with(mContext).load(Host.HOST + friend.getAvatar()).into(holder.imgFriend);
        holder.txtNameFriend.setText(friend.getName());
        holder.checkAddGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.e("checked", "true");
                    itemListener.onItemChecked(friend, true);
                } else {
                    itemListener.onItemChecked(friend, false);
                    Log.e("checked", "false");
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        if (friendList == null || friendList.isEmpty()) return 0;
        return friendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgFriend;
        private TextView txtNameFriend;
        private CheckBox checkAddGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFriend = itemView.findViewById(R.id.img_friend_group);
            txtNameFriend = itemView.findViewById(R.id.txt_name_friend_group);
            checkAddGroup = itemView.findViewById(R.id.checkbox_friend_group);

        }
    }

}
