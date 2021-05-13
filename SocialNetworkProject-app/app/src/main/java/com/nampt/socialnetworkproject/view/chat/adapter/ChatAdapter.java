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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.ChatRow2;
import com.nampt.socialnetworkproject.util.CalculateTimeUtil;
import com.nampt.socialnetworkproject.view.chat.ChatFragment;
import com.nampt.socialnetworkproject.view.message.MessageActivity;

import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int TYPE_CHAT_SINGLE = 1;
    private static int TYPE_CHAT_GROUP = 2;
    List<ChatRow2> rowList;
    Context mContext;
    ChatFragment mFragment;


    public ChatAdapter(Context mContext, ChatFragment mFragment) {
        this.mContext = mContext;
        this.mFragment = mFragment;
    }

    public void setChatRowList(List<ChatRow2> rowList) {
        this.rowList = rowList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (TYPE_CHAT_SINGLE == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_single, parent, false);
            return new SingleChatViewHolder(view);
        } else if (TYPE_CHAT_GROUP == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_group, parent, false);
            return new GroupChatViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ChatRow2 chatRow = rowList.get(position);
        if (chatRow == null) return;
        String roomName = chatRow.getName();
        int partnerId = 0;
        if (holder.getItemViewType() == TYPE_CHAT_SINGLE) {
            final SingleChatViewHolder singleViewHolder = (SingleChatViewHolder) holder;

            if (chatRow.getPartners().get(0).getId() != DataLocalManager.getPrefUser().getId()) {
                roomName = chatRow.getPartners().get(0).getName();
                partnerId = chatRow.getPartners().get(0).getId();
                if (chatRow.getPartners().get(0).getAvatar() != null)
                    Glide.with(mContext).load(Host.HOST + chatRow.getPartners().get(0).getAvatar()).into(singleViewHolder.imgFriend);
                else
                    Glide.with(mContext).load(R.drawable.unnamed).into(singleViewHolder.imgFriend);

                singleViewHolder.titleChatSingle.setText(chatRow.getPartners().get(0).getName());
            } else {
                roomName = chatRow.getPartners().get(1).getName();
                partnerId = chatRow.getPartners().get(1).getId();
                if (chatRow.getPartners().get(1).getAvatar() != null)
                    Glide.with(mContext).load(Host.HOST + chatRow.getPartners().get(1).getAvatar()).into(singleViewHolder.imgFriend);
                else
                    Glide.with(mContext).load(R.drawable.unnamed).into(singleViewHolder.imgFriend);

                singleViewHolder.titleChatSingle.setText(chatRow.getPartners().get(1).getName());
            }

            singleViewHolder.lastMsg.setText(chatRow.getLastMessage());
            singleViewHolder.lastTimeMsg.setText(CalculateTimeUtil.getInstance().
                    calculatorTimeFromChatViews(chatRow.getCreateTimeLastMessage(),new Date(System.currentTimeMillis()+7000)));

            if (!chatRow.isSeen()) {
                singleViewHolder.titleChatSingle.setTypeface(null, Typeface.BOLD);
                singleViewHolder.lastMsg.setTypeface(null, Typeface.BOLD);
                singleViewHolder.lastTimeMsg.setTypeface(null, Typeface.BOLD);
            } else {
                singleViewHolder.titleChatSingle.setTypeface(null, Typeface.NORMAL);
                singleViewHolder.lastMsg.setTypeface(null, Typeface.NORMAL);
                singleViewHolder.lastTimeMsg.setTypeface(null, Typeface.NORMAL);
            }

            final String finalRoomName = roomName;
            final int finalPartnerId = partnerId;
            singleViewHolder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("roomName", finalRoomName);
                    intent.putExtra("roomId", chatRow.getId());
                    intent.putExtra("isRoom", false);
                    intent.putExtra("partnerId", finalPartnerId);
                    mFragment.startActivityForResult(intent, ChatFragment.LAUNCH_MESSAGE_ACTIVITY);
                }
            });
        } else if (holder.getItemViewType() == TYPE_CHAT_GROUP) {

            GroupChatViewHolder groupViewHolder = (GroupChatViewHolder) holder;

            if (chatRow.getPartners().get(0).getAvatar() != null)
                Glide.with(mContext).load(Host.HOST + chatRow.getPartners().get(0).getAvatar()).into(groupViewHolder.imgUser1);
            else Glide.with(mContext).load(R.drawable.unnamed).into(groupViewHolder.imgUser1);
            if (chatRow.getPartners().get(1).getAvatar() != null)
                Glide.with(mContext).load(Host.HOST + chatRow.getPartners().get(1).getAvatar()).into(groupViewHolder.imgUser2);
            else Glide.with(mContext).load(R.drawable.unnamed).into(groupViewHolder.imgUser2);
            if (chatRow.getPartners().get(2).getAvatar() != null)
                Glide.with(mContext).load(Host.HOST + chatRow.getPartners().get(2).getAvatar()).into(groupViewHolder.imgUser3);
            else Glide.with(mContext).load(R.drawable.unnamed).into(groupViewHolder.imgUser3);

            groupViewHolder.titleChatGroup.setText(chatRow.getName());
            groupViewHolder.lastMsg.setText(chatRow.getLastMessage());
            groupViewHolder.lastTimeMsg.setText(CalculateTimeUtil.getInstance().
                    calculatorTimeFromChatViews(chatRow.getCreateTimeLastMessage(),new Date(System.currentTimeMillis()+5000)));

            if (!chatRow.isSeen()) {
                groupViewHolder.titleChatGroup.setTypeface(null, Typeface.BOLD);
                groupViewHolder.lastMsg.setTypeface(null, Typeface.BOLD);
                groupViewHolder.lastTimeMsg.setTypeface(null, Typeface.BOLD);
            } else {
                groupViewHolder.titleChatGroup.setTypeface(null, Typeface.NORMAL);
                groupViewHolder.lastMsg.setTypeface(null, Typeface.NORMAL);
                groupViewHolder.lastTimeMsg.setTypeface(null, Typeface.NORMAL);
            }


            final String finalRoomName1 = roomName;
            groupViewHolder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("roomId", chatRow.getId());
                    intent.putExtra("roomName", finalRoomName1);
                    intent.putExtra("isRoom", true);
                    mFragment.startActivityForResult(intent, ChatFragment.LAUNCH_MESSAGE_ACTIVITY);
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (rowList.get(position).getPartners().size() > 2)
            return TYPE_CHAT_GROUP;
        else return TYPE_CHAT_SINGLE;
    }

    @Override
    public int getItemCount() {
        if (rowList == null || rowList.isEmpty()) return 0;
        return rowList.size();
    }

    public class SingleChatViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgFriend;
        private TextView titleChatSingle;
        private TextView lastMsg;
        private TextView lastTimeMsg;
        private View layoutItem, layoutContentMsg;

        public SingleChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFriend = itemView.findViewById(R.id.img_user_friend);
            titleChatSingle = itemView.findViewById(R.id.txt_title_chat_single);
            lastMsg = itemView.findViewById(R.id.txt_last_msg_chat_single);
            layoutItem = itemView.findViewById(R.id.layout_item_chat_single);
            lastTimeMsg = itemView.findViewById(R.id.txt_last_time_msg_chat_single);
            layoutContentMsg = itemView.findViewById(R.id.layout_content_chat_single);
        }
    }

    public class GroupChatViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgUser1, imgUser2, imgUser3;
        private TextView titleChatGroup;
        private TextView lastMsg;
        private TextView lastTimeMsg;
        private View layoutItem;

        public GroupChatViewHolder(@NonNull View itemView) {
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
