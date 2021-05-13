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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nampt.socialnetworkproject.FragmentCallBacks;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.chatService.ChatService;
import com.nampt.socialnetworkproject.api.chatService.ListConversationResponse;
import com.nampt.socialnetworkproject.api.friendService.FriendService;
import com.nampt.socialnetworkproject.api.friendService.response.ListFriendResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.ChatRow2;
import com.nampt.socialnetworkproject.model.DataMsgSocketResponse;
import com.nampt.socialnetworkproject.model.Friend;
import com.nampt.socialnetworkproject.util.JsonParserUtil;
import com.nampt.socialnetworkproject.view.chat.adapter.ChatAdapter;
import com.nampt.socialnetworkproject.view.chat.adapter.UserOnlineAdapter;
import com.nampt.socialnetworkproject.view.home.HomeActivity;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;


public class ChatFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, FragmentCallBacks {
    private RecyclerView rcvUserOnline;
    private RecyclerView rcvChat;

    private UserOnlineAdapter userOnlineAdapter;
    private ChatAdapter chatAdapter;
    private View layoutUserOnline;
    View layoutNoData, layoutLoading, layoutNoNetwork;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context mContext;
    List<Friend> mFriendOnlineList = new ArrayList<>();
    List<ChatRow2> mChatRowList = new ArrayList<>();

    ///  Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private List<Integer> roomIdList = new ArrayList<>();
    WebSocketClient socketMsgSentListener, socketCreateGroupListener, socketSingleChatListener;
    List<WebSocketClient> webSocketClientList = new ArrayList<>();
    public static final int LAUNCH_MESSAGE_ACTIVITY = 100;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("CHAT", "create");
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View chat = inflater.inflate(R.layout.fragment_chat, container, false);
        addControl(chat);
        addEvent();
        return chat;
    }

    private void addControl(View view) {
        rcvUserOnline = view.findViewById(R.id.rcv_user_online);
        rcvChat = view.findViewById(R.id.rcv_chat);

        layoutUserOnline = view.findViewById(R.id.layout_user_online);
        layoutNoData = view.findViewById(R.id.layout_no_data_chat_single);
        layoutLoading = view.findViewById(R.id.layout_progress_loading_chat_single);
        layoutNoNetwork = view.findViewById(R.id.layout_no_network_chat_single);
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);

        swipeRefreshLayout = view.findViewById(R.id.swipe_fresh_layout_chat_single);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueThin));
        swipeRefreshLayout.setOnRefreshListener(this);

        userOnlineAdapter = new UserOnlineAdapter(mContext);
        LinearLayoutManager linearHorizonLayoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        userOnlineAdapter.setUserOnlineList(mFriendOnlineList);
        rcvUserOnline.setLayoutManager(linearHorizonLayoutManager);
        rcvUserOnline.setAdapter(userOnlineAdapter);

        chatAdapter = new ChatAdapter(mContext, this);
        LinearLayoutManager linearVerticalManager = new LinearLayoutManager(mContext);
        chatAdapter.setChatRowList(mChatRowList);
        rcvChat.setLayoutManager(linearVerticalManager);
        rcvChat.setAdapter(chatAdapter);
    }


    private void addEvent() {
        setFirstDataUserOnline();
        setFirstDataConversationList();
        initSocketCreateGroupListener();
        initSocketSingleChatListener();
    }

    private void setFirstDataUserOnline() {
        FriendService.service
                .getFriendOnlineList(DataLocalManager.getPrefUser().getAccessToken())
                .enqueue(new Callback<ListFriendResponse>() {
                    @Override
                    public void onResponse(Call<ListFriendResponse> call, Response<ListFriendResponse> response) {
                        if (response.body().getCode() == 1000) {
                            mFriendOnlineList = response.body().getData();
                            Iterator<Friend> it = mFriendOnlineList.iterator();
                            while (it.hasNext()) {
                                Friend friend = it.next();
                                if (friend.isBlock()) {
                                    it.remove();
                                }
                            }
                            userOnlineAdapter.setUserOnlineList(mFriendOnlineList);
                            if (mFriendOnlineList.isEmpty())
                                layoutUserOnline.setVisibility(View.GONE);
                            else layoutUserOnline.setVisibility(View.VISIBLE);

                        } else {
                            // token sai !
                        }
                    }

                    @Override
                    public void onFailure(Call<ListFriendResponse> call, Throwable t) {
                        layoutUserOnline.setVisibility(View.GONE);
                    }
                });
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
                            webSocketClientList.clear();
                            roomIdList.clear();

                            Iterator<ChatRow2> it = chatRows.iterator();
                            while (it.hasNext()) {
                                ChatRow2 conversation = it.next();
                                if (conversation.getPartners().size() == 2) {
                                    for (Friend partner : conversation.getPartners()) {
                                        if (DataLocalManager.getPrefUser().getId() != partner.getId() && partner.isBlock())
                                            it.remove();
                                    }
                                }
                            }
                            Collections.sort(chatRows, ChatRow2.DateComparatorDESC);

                            mChatRowList = chatRows;
                            chatAdapter.setChatRowList(mChatRowList);

                            // initialize new session
                            for (ChatRow2 row : mChatRowList)
                                roomIdList.add(row.getId());
                            Log.e("roomIdList", roomIdList.toString());
                            for (int roomId : roomIdList)
                                initWebSocketClient(roomId);

                        } else if (response.body().getCode() == 9993) {
                            // quay về login, xóa sharePrefUser , sai cmnl token rồi
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
                                setFirstDataUserOnline();
                            }
                        });
                    }
                });
    }


    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        mFriendOnlineList.clear();
        mChatRowList.clear();
        layoutUserOnline.setVisibility(View.VISIBLE);
        setFirstDataUserOnline();
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
                Log.e("socketCreateListener1", "Session is starting");
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
                            chatAdapter.notifyItemInserted(0);

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
        socketCreateGroupListener.enableAutomaticReconnection(500);
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
                                    chatAdapter.notifyItemRemoved(i);
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
                            chatAdapter.notifyItemInserted(0);

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
        socketMsgSentListener.enableAutomaticReconnection(500);
        socketMsgSentListener.connect();
        webSocketClientList.add(socketMsgSentListener);
    }

    private void initSocketSingleChatListener() {
        URI uri;
        try {
            String host = Host.HOST.replace("http", "ws");
            // Connect to local host
            uri = new URI(host + "websocket/single");
            Log.e("socketCreateListener", uri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        socketSingleChatListener = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.e("socketCreateListener1", "Session is starting");
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
                            ChatRow2 newRow;
                            boolean isRoomExist = false;
                            int indexRow = -1;
                            for (int i = 0; i < mChatRowList.size(); i++) {
                                if (mChatRowList.get(i).getId() == newMsg.getIdRoom()) {
                                    indexRow = i;
                                    isRoomExist = true;
                                    break;
                                }
                            }
                            if (isRoomExist) {
                                newRow = mChatRowList.get(indexRow);
                                mChatRowList.remove(mChatRowList.get(indexRow));
                                chatAdapter.notifyItemRemoved(indexRow);

                                newRow.setLastMessage(newMsg.getMessage().getContent());
                                newRow.setCreateTimeLastMessage(newMsg.getMessage().getCreateTime());

                                if (newMsg.getMessage().getSender().getId()
                                        == DataLocalManager.getPrefUser().getId())
                                    newRow.setSeen(true);
                                else newRow.setSeen(false);

                                if (layoutNoData.getVisibility() == View.VISIBLE) {
                                    layoutNoData.setVisibility(View.GONE);
                                }
                                if (layoutNoNetwork.getVisibility() == View.VISIBLE) {
                                    layoutNoNetwork.setVisibility(View.GONE);
                                }
                                mChatRowList.add(0, newRow);
                                chatAdapter.notifyItemInserted(0);

                            } else {
                                newRow = new ChatRow2();
                                newRow.setId(newMsg.getIdRoom());
                                newRow.setName(null);
                                newRow.setLastMessage(newMsg.getMessage().getContent());
                                newRow.setCreateTimeLastMessage(newMsg.getMessage().getCreateTime());

                                List<Friend> partners = new ArrayList<>();
                                partners.add(new Friend(newMsg.getMessage().getSender().getId(),
                                        newMsg.getMessage().getSender().getName(),
                                        newMsg.getMessage().getSender().getAvatar()));

                                partners.add(new Friend(newMsg.getMessage().getPartners().get(0).getId(),
                                        newMsg.getMessage().getPartners().get(0).getName(),
                                        newMsg.getMessage().getPartners().get(0).getAvatar()));

                                newRow.setPartners(partners);

                                if (newMsg.getMessage().getSender().getId()
                                        == DataLocalManager.getPrefUser().getId())
                                    newRow.setSeen(true);
                                else newRow.setSeen(false);

                                if (layoutNoData.getVisibility() == View.VISIBLE) {
                                    layoutNoData.setVisibility(View.GONE);
                                }
                                if (layoutNoNetwork.getVisibility() == View.VISIBLE) {
                                    layoutNoNetwork.setVisibility(View.GONE);
                                }
                                mChatRowList.add(0, newRow);
                                chatAdapter.notifyItemInserted(0);

                                roomIdList.add(newMsg.getIdRoom());
                                initWebSocketClient(newMsg.getIdRoom());
                            }

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
        socketSingleChatListener.addHeader("Authorization", DataLocalManager.getPrefUser().getAccessToken());
        socketSingleChatListener.setReadTimeout(3600000);
        socketSingleChatListener.setConnectTimeout(5000);
        socketSingleChatListener.enableAutomaticReconnection(500);
        socketSingleChatListener.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LAUNCH_MESSAGE_ACTIVITY) {
                int roomId = data.getIntExtra("room_id_seen", 0);
                for (ChatRow2 row : mChatRowList) {
                    if (row.getId() == roomId && !row.isSeen()) {
                        row.setSeen(true);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                ((HomeActivity) mContext).onMsgFromFragToMain("ChatFragment", roomId);
            }
        }
    }

    @Override
    public void onMsgFromMainToFragment(int intValue) {
        int roomId = intValue;
        for (ChatRow2 row : mChatRowList) {
            if (row.getId() == roomId && !row.isSeen()) {
                row.setSeen(true);
            }
        }
        chatAdapter.notifyDataSetChanged();
    }
}