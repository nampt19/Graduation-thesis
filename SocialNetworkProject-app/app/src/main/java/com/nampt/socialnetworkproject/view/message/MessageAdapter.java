package com.nampt.socialnetworkproject.view.message;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.MessageRow;
import com.nampt.socialnetworkproject.util.CalculateTimeUtil;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<MessageRow> messageList;
    private static int TYPE_MY_MSG = 1, TYPE_FRIEND_MSG = 2;


    public MessageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setMessageList(List<MessageRow> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_MY_MSG) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_msg, parent, false);
            return new MyMessageViewHolder(view);
        } else if (viewType == TYPE_FRIEND_MSG) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_msg, parent, false);
            return new FriendMessageViewHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageRow message = messageList.get(position);
        if (message == null) return;
        if (holder.getItemViewType() == TYPE_MY_MSG) {
            final MyMessageViewHolder myHolder = (MyMessageViewHolder) holder;
            myHolder.txtMsg.setText(message.getContent());
            myHolder.txtTime.setText(CalculateTimeUtil.getInstance().
                    calculatorTimeFromChatViews(message.getCreateTime(), new Date(System.currentTimeMillis()+7000)));

            myHolder.txtMsg.setOnClickListener(new View.OnClickListener() {
                boolean isShow = false;

                @Override
                public void onClick(View v) {
                    if (isShow) {
                        myHolder.txtTime.setVisibility(View.GONE);
                        isShow = false;
                    } else {
                        myHolder.txtTime.setVisibility(View.VISIBLE);
                        isShow = true;
                    }
                }
            });
        } else if (holder.getItemViewType() == TYPE_FRIEND_MSG) {
            final FriendMessageViewHolder friendHolder = (FriendMessageViewHolder) holder;

            if (message.getSender().getAvatar() != null)
                Glide.with(mContext).load(Host.HOST + message.getSender().getAvatar())
                        .placeholder(R.drawable.null_bk).into(friendHolder.imgFriend);
            friendHolder.txtMsg.setText(message.getContent());


            friendHolder.txtNameFriend.setText(message.getSender().getName() + "   " +
                    CalculateTimeUtil.getInstance().calculatorTimeFromChatViews(message.getCreateTime(),
                            new Date(System.currentTimeMillis()+7000)));
            friendHolder.imgFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProfileUserActivity.class);
                    intent.putExtra("userId", message.getSender().getId());
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                }
            });
            friendHolder.txtMsg.setOnClickListener(new View.OnClickListener() {
                boolean isShow = false;

                @Override
                public void onClick(View v) {
                    if (isShow) {
                        friendHolder.txtNameFriend.setVisibility(View.GONE);
                        isShow = false;
                    } else {
                        friendHolder.txtNameFriend.setVisibility(View.VISIBLE);
                        isShow = true;
                    }
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSender().getId() != DataLocalManager.getPrefUser().getId())
            return TYPE_FRIEND_MSG;
        else return TYPE_MY_MSG;
    }

    @Override
    public int getItemCount() {
        if (messageList == null || messageList.isEmpty()) return 0;
        return messageList.size();
    }

    public class FriendMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtMsg, txtNameFriend;
        CircleImageView imgFriend;

        public FriendMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txt_friend_msg);
            txtNameFriend = itemView.findViewById(R.id.txt_name_friend_msg);
            imgFriend = itemView.findViewById(R.id.img_friend_msg);
        }
    }

    public class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtMsg, txtTime;

        public MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txt_my_msg);
            txtTime = itemView.findViewById(R.id.txt_time_my_msg);
        }
    }
}
