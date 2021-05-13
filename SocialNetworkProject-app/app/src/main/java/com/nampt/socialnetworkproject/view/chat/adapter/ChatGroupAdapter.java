package com.nampt.socialnetworkproject.view.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.model.ChatRow2;
import com.nampt.socialnetworkproject.util.CalculateTimeUtil;
import com.nampt.socialnetworkproject.view.chat.ChatFragment;
import com.nampt.socialnetworkproject.view.chat.GroupChatFragment;
import com.nampt.socialnetworkproject.view.message.MessageActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.gusavila92.websocketclient.WebSocketClient;

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.ViewHolder> {
    private Context mContext;
    private List<ChatRow2> chatRowList;
    private Fragment mFragment;


    public ChatGroupAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public ChatGroupAdapter(Context mContext, Fragment mFragment) {
        this.mContext = mContext;
        this.mFragment = mFragment;
    }



    public void setChatRowList(List<ChatRow2> chatRowList) {

        this.chatRowList = chatRowList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ChatRow2 chatRow = chatRowList.get(position);
        if (chatRow == null) return;


        if (chatRow.getPartners().get(0).getAvatar() != null)
            Glide.with(mContext).load(Host.HOST + chatRow.getPartners().get(0).getAvatar()).into(holder.imgUser1);
        else Glide.with(mContext).load(R.drawable.unnamed).into(holder.imgUser1);
        if (chatRow.getPartners().get(1).getAvatar() != null)
            Glide.with(mContext).load(Host.HOST + chatRow.getPartners().get(1).getAvatar()).into(holder.imgUser2);
        else Glide.with(mContext).load(R.drawable.unnamed).into(holder.imgUser2);
        if (chatRow.getPartners().get(2).getAvatar() != null)
            Glide.with(mContext).load(Host.HOST + chatRow.getPartners().get(2).getAvatar()).into(holder.imgUser3);
        else Glide.with(mContext).load(R.drawable.unnamed).into(holder.imgUser3);

        holder.titleChatGroup.setText(chatRow.getName());
        holder.lastMsg.setText(chatRow.getLastMessage());
        holder.lastTimeMsg.setText(CalculateTimeUtil.getInstance().
                calculatorTimeFromChatViews(chatRow.getCreateTimeLastMessage(),new Date(System.currentTimeMillis()+7000)));


        if (!chatRow.isSeen()) {
            holder.titleChatGroup.setTypeface(null, Typeface.BOLD);
            holder.lastMsg.setTypeface(null, Typeface.BOLD);
            holder.lastTimeMsg.setTypeface(null, Typeface.BOLD);
        } else {
            holder.titleChatGroup.setTypeface(null, Typeface.NORMAL);
            holder.lastMsg.setTypeface(null, Typeface.NORMAL);
            holder.lastTimeMsg.setTypeface(null, Typeface.NORMAL);
        }

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ChatRow2 chatRow2 = chatRow;
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("roomId", chatRow2.getId());
                intent.putExtra("roomName", chatRow2.getName());
                intent.putExtra("isRoom", true);
                mFragment.startActivityForResult(intent, ChatFragment.LAUNCH_MESSAGE_ACTIVITY);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (chatRowList == null || chatRowList.isEmpty()) return 0;
        return chatRowList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgUser1, imgUser2, imgUser3;
        private TextView titleChatGroup;
        private TextView lastMsg, lastTimeMsg;
        private View layoutItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser1 = itemView.findViewById(R.id.img_user_1);
            imgUser2 = itemView.findViewById(R.id.img_user_2);
            imgUser3 = itemView.findViewById(R.id.img_user_3);
            titleChatGroup = itemView.findViewById(R.id.txt_title_chat_group);
            lastMsg = itemView.findViewById(R.id.txt_last_msg_chat_group);
            lastTimeMsg = itemView.findViewById(R.id.txt_last_time_msg_chat_group);
            layoutItem = itemView.findViewById(R.id.layout_item_chat_group);
        }
    }



}
