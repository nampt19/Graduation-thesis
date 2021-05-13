package com.nampt.socialnetworkproject.view.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.nampt.socialnetworkproject.FragmentCallBacks;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.chatService.ChatService;
import com.nampt.socialnetworkproject.api.chatService.ListConversationResponse;
import com.nampt.socialnetworkproject.api.notifyService.NotifyServiceImpl;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.ChatRow2;
import com.nampt.socialnetworkproject.model.DataMsgSocketResponse;
import com.nampt.socialnetworkproject.model.Friend;
import com.nampt.socialnetworkproject.util.JsonParserUtil;
import com.nampt.socialnetworkproject.view.chat.adapter.ChatGroupAdapter;
import com.nampt.socialnetworkproject.view.createGroup.CreateNewGroupChatActivity;
import com.nampt.socialnetworkproject.view.home.HomeActivity;
import com.nampt.socialnetworkproject.view.notification.NotificationActivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;

public class GroupChatFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, FragmentCallBacks {
    private RecyclerView rcvGroupChat;
    private ChatGroupAdapter chatGroupAdapter;
    private Context mContext;
    private LinearLayout layoutAddNewGroup;
    SwipeRefreshLayout swipeRefreshLayout;
    View layoutNoData, layoutLoading, layoutNoNetwork;

    private List<ChatRow2> mChatRowList = new ArrayList<>();

    private List<Integer> roomIdList = new ArrayList<>();
    WebSocketClient socketMsgSentListener, socketCreateGroupListener;
    List<WebSocketClient> webSocketClientList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Group1", "onCreate GroupFragment");
        mContext = getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupChat = inflater.inflate(R.layout.fragment_group_chat, container, false);
        addControl(groupChat);
        addEvent();
        return groupChat;
    }


    private void addControl(View view) {
        layoutAddNewGroup = view.findViewById(R.id.layout_add_new_group);
        rcvGroupChat = view.findViewById(R.id.rcv_group_chat);

        layoutNoData = view.findViewById(R.id.layout_no_data_chat_group);
        layoutLoading = view.findViewById(R.id.layout_progress_loading_chat_group);
        layoutNoNetwork = view.findViewById(R.id.layout_no_network_chat_group);
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);

        swipeRefreshLayout = view.findViewById(R.id.swipe_fresh_layout_chat_group);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueThin));
        swipeRefreshLayout.setOnRefreshListener(this);

        chatGroupAdapter = new ChatGroupAdapter(mContext, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        chatGroupAdapter.setChatRowList(mChatRowList);
        rcvGroupChat.setAdapter(chatGroupAdapter);
        rcvGroupChat.setLayoutManager(layoutManager);
    }

    private void addEvent() {
        setFirstDataConversationList();
        initSocketCreateGroupListener();
        layoutAddNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreateNewGroupChatActivity.class);
                GroupChatFragment.this.startActivityForResult(intent, 1);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                List<Integer> partnerIds = data.getIntegerArrayListExtra("partnerIds");
                partnerIds.add(DataLocalManager.getPrefUser().getId());
                String roomName = data.getStringExtra("roomName");
                int roomId = data.getIntExtra("roomId", 0);
                Date createTime = JsonParserUtil.getInstance().fromJson(data.getStringExtra("createTime"), Date.class);

                DataNewGroupRequest request = new DataNewGroupRequest();
                request.setRoomId(roomId);
                request.setRoomName(roomName);
                request.setPartnerIds(partnerIds);
                request.setCreateTime(createTime);

                socketCreateGroupListener.send(JsonParserUtil.getInstance().toJson(request));

                NotifyServiceImpl.getInstance().setNotify(NotificationActivity.TYPE_NOTIFY_CHAT_GROUP,
                        0, roomId, "\uD83D\uDCE3\uD83D\uDCE3 nhóm mới, zô nào !");

            }
            if (requestCode == ChatFragment.LAUNCH_MESSAGE_ACTIVITY) {
                Log.e("seen", "seen");
                int room_id = data.getIntExtra("room_id_seen", 0);
                for (ChatRow2 row : mChatRowList) {
                    if (row.getId() == room_id && !row.isSeen()) {
                        row.setSeen(true);
                    }
                }
                chatGroupAdapter.notifyDataSetChanged();
                ((HomeActivity) mContext).onMsgFromFragToMain("GroupChatFragment", room_id);
            }
        }
    }

    private void setFirstDataConversationList() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoNetwork.setVisibility(View.GONE);
        layoutNoData.setVisibility(View.GONE);
        ChatService.service.getListConversation(DataLocalManager.getPrefUser().getAccessToken())
                .enqueue(new Callback<ListConversationResponse>() {
                    @Override
                    public void onResponse(Call<ListConversationResponse> call, Response<ListConversationResponse> response) {
                        layoutLoading.setVisibility(View.GONE);
                        if (response.body().getCode() == 1000) {
                            List<ChatRow2> chatRows = response.body().getData();

                            if (chatRows.isEmpty()) {
                                layoutNoData.setVisibility(View.VISIBLE);
                                return;
                            }
                            // remove old socket session
                            for (WebSocketClient client : webSocketClientList)
                                client.close();

                            roomIdList.clear();
                            webSocketClientList.clear();

                            Iterator<ChatRow2> it = chatRows.iterator();
                            while (it.hasNext()) {
                                ChatRow2 chatRow = it.next();
                                if (chatRow.getPartners().size() == 2) it.remove();
                                else roomIdList.add(chatRow.getId());
                            }
                            Log.e("roomIdList", roomIdList.toString());
                            Collections.sort(chatRows, ChatRow2.DateComparatorDESC);
                            mChatRowList = chatRows;
                            chatGroupAdapter.setChatRowList(mChatRowList);


                            for (int roomId : roomIdList)
                                initWebSocketClient(roomId);

                        } else if (response.body().getCode() == 9993) {

                        }
                    }

                    @Override
                    public void onFailure(Call<ListConversationResponse> call, Throwable t) {
                        layoutLoading.setVisibility(View.GONE);
                        layoutNoNetwork.setVisibility(View.VISIBLE);
                        layoutNoNetwork.findViewById(R.id.btn_reload_network_utils).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutNoNetwork.setVisibility(View.GONE);
                                setFirstDataConversationList();
                            }
                        });
                    }
                });
    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        mChatRowList.clear();
        setFirstDataConversationList();

    }

    private void initSocketCreateGroupListener() {
        URI uri;
        try {
            String host = Host.HOST.replace("http", "ws");
            // Connect to local host
            uri = new URI(host + "websocket/createGroup");
            Log.e("socketCreateListener", uri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        socketCreateGroupListener = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.e("socketCreateListener2", "Session is starting");
            }

            @Override
            public void onTextReceived(final String message) {
                Log.e("socketCreateListener", "Message received:" + message);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DataMsgSocketResponse newMsg = JsonParserUtil.getInstance()
                                    .fromJson(message, DataMsgSocketResponse.class);
                            ChatRow2 newRow = new ChatRow2();

                            newRow.setId(newMsg.getIdRoom());
                            newRow.setName(newMsg.getRoomName());
                            newRow.setLastMessage(newMsg.getMessage().getContent());
                            newRow.setCreateTimeLastMessage(newMsg.getMessage().getCreateTime());

                            List<Friend> partners = new ArrayList<>();
                            partners.add(new Friend(newMsg.getMessage().getSender().getId(),
                                    newMsg.getMessage().getSender().getName(),
                                    newMsg.getMessage().getSender().getAvatar()));

                            partners.add(new Friend(newMsg.getMessage().getPartners().get(0).getId(),
                                    newMsg.getMessage().getPartners().get(0).getName(),
                                    newMsg.getMessage().getPartners().get(0).getAvatar()));

                            partners.add(new Friend(newMsg.getMessage().getPartners().get(1).getId(),
                                    newMsg.getMessage().getPartners().get(1).getName(),
                                    newMsg.getMessage().getPartners().get(1).getAvatar()));

                            newRow.setPartners(partners);


                            if (newMsg.getMessage().getSender().getId() == DataLocalManager.getPrefUser().getId())
                                newRow.setSeen(true);
                            else newRow.setSeen(false);

                            if (layoutNoData.getVisibility() == View.VISIBLE) {
                                layoutNoData.setVisibility(View.GONE);
                            }
                            if (layoutNoNetwork.getVisibility() == View.VISIBLE) {
                                layoutNoNetwork.setVisibility(View.GONE);
                            }
                            mChatRowList.add(0, newRow);
                            chatGroupAdapter.notifyItemInserted(0);

                            roomIdList.add(newMsg.getIdRoom());
                            initWebSocketClient(newMsg.getIdRoom());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            public void onBinaryReceived(byte[] data) {

            }

            @Override
            public void onPingReceived(byte[] data) {

            }

            @Override
            public void onPongReceived(byte[] data) {

            }

            @Override
            public void onException(Exception e) {
                Log.e("socketCreateListener", e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.e("socketCreateListener", "Closed");
            }
        };
        socketCreateGroupListener.addHeader("Authorization", DataLocalManager.getPrefUser().getAccessToken());
        socketCreateGroupListener.setReadTimeout(3600000);
        socketCreateGroupListener.setConnectTimeout(5000);
        socketCreateGroupListener.enableAutomaticReconnection(100);
        socketCreateGroupListener.connect();
    }


    private void initWebSocketClient(int idRoom) {
        URI uri;
        try {
            String host = Host.HOST.replace("http", "ws");
            // Connect to local host
            uri = new URI(host + "websocket/group");
            Log.e("WebSocket", uri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        socketMsgSentListener = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.e("WebSocket", "Session is starting");
            }

            @Override
            public void onTextReceived(String s) {
                Log.e("WebSocket", "Message received:" + s);
                final String message = s;
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DataMsgSocketResponse newMsg = JsonParserUtil.getInstance()
                                    .fromJson(message, DataMsgSocketResponse.class);
                            ChatRow2 newRow = null;
                            for (int i = 0; i < mChatRowList.size(); i++) {
                                if (mChatRowList.get(i).getId() == newMsg.getIdRoom()) {
                                    newRow = mChatRowList.get(i);
                                    mChatRowList.remove(mChatRowList.get(i));
                                    chatGroupAdapter.notifyItemRemoved(i);
                                    break;
                                }
                            }
                            newRow.setLastMessage(newMsg.getMessage().getContent());
                            newRow.setCreateTimeLastMessage(newMsg.getMessage().getCreateTime());

                            if (newMsg.getMessage().getSender().getId() == DataLocalManager.getPrefUser().getId())
                                newRow.setSeen(true);
                            else newRow.setSeen(false);

                            if (layoutNoData.getVisibility() == View.VISIBLE) {
                                layoutNoData.setVisibility(View.GONE);
                            }
                            if (layoutNoNetwork.getVisibility() == View.VISIBLE) {
                                layoutNoNetwork.setVisibility(View.GONE);
                            }
                            mChatRowList.add(0, newRow);
                            chatGroupAdapter.notifyItemInserted(0);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {

            }

            @Override
            public void onPingReceived(byte[] data) {

            }

            @Override
            public void onPongReceived(byte[] data) {

            }

            @Override
            public void onException(Exception e) {
                Log.e("WebSocket", e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.e("WebSocket", "Closed");
            }
        };
        socketMsgSentListener.addHeader("Authorization", DataLocalManager.getPrefUser().getAccessToken());
        socketMsgSentListener.addHeader("Room-Id", String.valueOf(idRoom));
        socketMsgSentListener.setReadTimeout(3600000);
        socketMsgSentListener.setConnectTimeout(5000);
        socketMsgSentListener.enableAutomaticReconnection(100);
        socketMsgSentListener.connect();
        webSocketClientList.add(socketMsgSentListener);
    }

    @Override
    public void onMsgFromMainToFragment(int intValue) {
        int roomId = intValue;
        for (ChatRow2 row : mChatRowList) {
            if (row.getId() == roomId && !row.isSeen()) {
                row.setSeen(true);
                Log.e("seen", "seen room : " + roomId);
            }
        }
        if (chatGroupAdapter != null)
            chatGroupAdapter.notifyDataSetChanged();

    }


    public class DataNewGroupRequest {
        private int roomId;
        private String roomName;
        private List<Integer> partnerIds;
        private Date createTime;

        public int getRoomId() {
            return roomId;
        }

        public void setRoomId(int roomId) {
            this.roomId = roomId;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public List<Integer> getPartnerIds() {
            return partnerIds;
        }

        public void setPartnerIds(List<Integer> partnerIds) {
            this.partnerIds = partnerIds;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }
    }


}