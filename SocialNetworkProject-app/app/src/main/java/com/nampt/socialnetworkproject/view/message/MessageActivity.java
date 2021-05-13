package com.nampt.socialnetworkproject.view.message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.chatService.ChatService;
import com.nampt.socialnetworkproject.api.chatService.ListMessageResponse;
import com.nampt.socialnetworkproject.api.chatService.SingleChatRequest;
import com.nampt.socialnetworkproject.api.notifyService.NotifyServiceImpl;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.DataMsgSocketResponse;
import com.nampt.socialnetworkproject.model.Friend;
import com.nampt.socialnetworkproject.model.MessActivityModel;
import com.nampt.socialnetworkproject.model.MessageRow;
import com.nampt.socialnetworkproject.util.JsonParserUtil;
import com.nampt.socialnetworkproject.view.notification.NotificationActivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;

public class MessageActivity extends AppCompatActivity {
    RecyclerView rcvMessage;
    MessageAdapter messageAdapter;
    EditText edtWireMsg;
    ImageButton btnSendMsg;
    Toolbar toolbar;
    View layoutNoData, layoutLoading, layoutNoNetwork;
    List<MessageRow> mMessageList = new ArrayList<>();
    int roomId, partnerId;
    String roomName;
    boolean isRoom;
    WebSocketClient webSocketClientSingle, webSocketClientRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        addControl();
        initToolbar();
        initRcvMessage();
        initSocketListener();
        addEvent();
    }

    private void initSocketListener() {
        if (isRoom)
            initWebSocketClientRoom();
        if (partnerId != 0)
            initWebSocketClientSingle();
    }

    private void addControl() {
        toolbar = findViewById(R.id.toolbar_message_activity);
        edtWireMsg = findViewById(R.id.edt_write_msg);
        btnSendMsg = findViewById(R.id.btn_send_msg);
        rcvMessage = findViewById(R.id.rcv_msg);

        layoutNoData = findViewById(R.id.layout_no_data_message_activity);
        layoutLoading = findViewById(R.id.layout_progress_loading_message_activity);
        layoutNoNetwork = findViewById(R.id.layout_no_network_message_activity);
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);

        roomId = getIntent().getIntExtra("roomId", 0);
        partnerId = getIntent().getIntExtra("partnerId", 0);
        roomName = getIntent().getStringExtra("roomName");
        isRoom = getIntent().getBooleanExtra("isRoom", false);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(roomName);
        }
    }


    private void initRcvMessage() {
        messageAdapter = new MessageAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messageAdapter.setMessageList(mMessageList);
        rcvMessage.setAdapter(messageAdapter);
        rcvMessage.setLayoutManager(layoutManager);
        rcvMessage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    rcvMessage.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!mMessageList.isEmpty()) {
                                rcvMessage.smoothScrollToPosition(mMessageList.size() - 1);
                            }
                        }
                    }, 100);
                }
            }
        });
    }

    private void initWebSocketClientSingle() {
        URI uri;
        try {
            String host = Host.HOST.replace("http", "ws");
            // Connect to local host
            uri = new URI(host + "websocket/single");

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        webSocketClientSingle = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.e("websocket/single", "Session is starting");
            }

            @Override
            public void onTextReceived(String s) {
                Log.e("WebSocket", "Message received:" + s);
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            DataMsgSocketResponse newMsg = JsonParserUtil.getInstance()
                                    .fromJson(message, DataMsgSocketResponse.class);

                            if (newMsg.getMessage().getSender().getId() != partnerId) {
                                return;
                            }
                            if (layoutNoNetwork.getVisibility() == View.VISIBLE)
                                layoutNoNetwork.setVisibility(View.GONE);
                            if (layoutNoData.getVisibility() == View.VISIBLE)
                                layoutNoData.setVisibility(View.GONE);
                            mMessageList.add(newMsg.getMessage());
                            messageAdapter.notifyDataSetChanged();
                            if (!mMessageList.isEmpty())
                                rcvMessage.scrollToPosition(mMessageList.size() - 1);

                            edtWireMsg.setText(null);
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
        webSocketClientSingle.addHeader("Authorization", DataLocalManager.getPrefUser().getAccessToken());
        webSocketClientSingle.setReadTimeout(360000);
        webSocketClientSingle.setConnectTimeout(5000);
        webSocketClientSingle.enableAutomaticReconnection(1000);
        webSocketClientSingle.connect();
    }

    private void initWebSocketClientRoom() {
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
        webSocketClientRoom = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.e("websocket/group", "Session is starting");
            }

            @Override
            public void onTextReceived(String s) {
                Log.e("WebSocket", "Message received:" + s);
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DataMsgSocketResponse newMsg = JsonParserUtil.getInstance()
                                    .fromJson(message, DataMsgSocketResponse.class);
                            if (layoutNoNetwork.getVisibility() == View.VISIBLE)
                                layoutNoNetwork.setVisibility(View.GONE);
                            if (layoutNoData.getVisibility() == View.VISIBLE)
                                layoutNoData.setVisibility(View.GONE);
                            mMessageList.add(newMsg.getMessage());
                            messageAdapter.notifyDataSetChanged();
                            if (!mMessageList.isEmpty())
                                rcvMessage.scrollToPosition(mMessageList.size() - 1);

                            edtWireMsg.setText(null);
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
        webSocketClientRoom.addHeader("Authorization", DataLocalManager.getPrefUser().getAccessToken());
        webSocketClientRoom.addHeader("Room-Id", String.valueOf(roomId));
        webSocketClientRoom.setReadTimeout(360000);
        webSocketClientRoom.setConnectTimeout(5000);
        webSocketClientRoom.enableAutomaticReconnection(1000);
        webSocketClientRoom.connect();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addEvent() {
        handleGetListMessage();
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtMsg = edtWireMsg.getText().toString().trim();

                DataMessageSend msg = new DataMessageSend(txtMsg, new Date());

                if (txtMsg.isEmpty()) {
                    return;
                }

                if (isRoom) {
                    webSocketClientRoom.send(JsonParserUtil.getInstance().toJson(msg));
                    NotifyServiceImpl.getInstance().setNotify(NotificationActivity.TYPE_NOTIFY_CHAT_GROUP,
                            0, roomId, txtMsg);
                }

                if (partnerId != 0) {
                    SingleChatRequest request = new SingleChatRequest();
                    request.setPartnerId(partnerId);
                    request.setContent(txtMsg);
                    request.setCreateTime(msg.getCreateTime());
                    webSocketClientSingle.send(JsonParserUtil.getInstance().toJson(request));

                    MessageRow newRow = new MessageRow();
                    Friend sender = new Friend(DataLocalManager.getPrefUser().getId(),
                            DataLocalManager.getPrefUser().getName(),
                            DataLocalManager.getPrefUser().getLinkAvatar(),
                            false);

                    newRow.setSender(sender);
                    newRow.setContent(txtMsg);
                    newRow.setCreateTime(msg.getCreateTime());
                    mMessageList.add(newRow);
                    messageAdapter.notifyDataSetChanged();
                    rcvMessage.scrollToPosition(mMessageList.size() - 1);

                    NotifyServiceImpl.getInstance().setNotify(NotificationActivity.TYPE_NOTIFY_CHAT_SINGLE,
                            partnerId, 0, txtMsg);
                }
                edtWireMsg.setText(null);
            }
        });
    }

    private void handleGetListMessage() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoNetwork.setVisibility(View.GONE);
        if (isRoom) {
            ChatService.service
                    .getConversationByRoomId(DataLocalManager.getPrefUser().getAccessToken(), roomId)
                    .enqueue(new Callback<ListMessageResponse>() {
                        @Override
                        public void onResponse(Call<ListMessageResponse> call, Response<ListMessageResponse> response) {
                            layoutLoading.setVisibility(View.GONE);
                            if (response.body().getCode() == 1000) {
                                List<MessageRow> messageList = response.body().getData();
                                if (messageList.size() == 0) {
                                    layoutNoData.setVisibility(View.VISIBLE);
                                    return;
                                }
                                mMessageList = messageList;
                                messageAdapter.setMessageList(mMessageList);
                                rcvMessage.scrollToPosition(messageList.size() - 1);
                            } else if (response.body().getCode() == 9992) {
                                Toast.makeText(MessageActivity.this, "Phòng không tồn tại !", Toast.LENGTH_SHORT).show();
                            } else if (response.body().getCode() == 9993) {
                                Toast.makeText(MessageActivity.this, "Sai token !", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ListMessageResponse> call, Throwable t) {
                            layoutLoading.setVisibility(View.GONE);
                            layoutNoNetwork.setVisibility(View.VISIBLE);
                            layoutNoNetwork.findViewById(R.id.btn_reload_network_utils).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    layoutNoNetwork.setVisibility(View.GONE);
                                    handleGetListMessage();
                                }
                            });
                        }
                    });
        } else {
            // call api get conversation single
            ChatService.service.
                    getConversationByPartnerId(DataLocalManager.getPrefUser().getAccessToken(), partnerId)
                    .enqueue(new Callback<ListMessageResponse>() {
                        @Override
                        public void onResponse(Call<ListMessageResponse> call, Response<ListMessageResponse> response) {
                            layoutLoading.setVisibility(View.GONE);
                            if (response.body().getCode() == 1000) {
                                List<MessageRow> messageList = response.body().getData();
                                if (messageList.size() == 0) {
                                    layoutNoData.setVisibility(View.VISIBLE);
                                    return;
                                }
                                mMessageList = messageList;
                                messageAdapter.setMessageList(mMessageList);
                                rcvMessage.scrollToPosition(messageList.size() - 1);
                            } else if (response.body().getCode() == 9992) {

                                Toast t = Toast.makeText(MessageActivity.this, "Chưa có tin nhắn nào,hãy tích tực nhắn tin !", Toast.LENGTH_LONG);
                                t.setGravity(Gravity.CENTER, 0, 0);
                                t.show();
                            } else if (response.body().getCode() == 9993) {
                                Toast.makeText(MessageActivity.this, "Sai token !", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ListMessageResponse> call, Throwable t) {
                            layoutLoading.setVisibility(View.GONE);
                            layoutNoNetwork.setVisibility(View.VISIBLE);
                            layoutNoNetwork.findViewById(R.id.btn_reload_network_utils).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    layoutNoNetwork.setVisibility(View.GONE);
                                    handleGetListMessage();
                                }
                            });
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        if (isRoom) {
            DataLocalManager.setMessageActivityInstall(new MessActivityModel(roomId, true, true));
        } else {
            DataLocalManager.setMessageActivityInstall(new MessActivityModel(partnerId, true, false));
        }
        Log.e("onStart", "mess onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        DataLocalManager.setMessageActivityInstall(null);
        Log.e("onStop", "mess onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setSeen();
        if (webSocketClientRoom != null)
            webSocketClientRoom.close();

        if (webSocketClientSingle != null)
            webSocketClientSingle.close();

    }

    @Override
    public void finish() {
        Intent replyIntent = new Intent();
        replyIntent.putExtra("room_id_seen", roomId);
        setResult(Activity.RESULT_OK, replyIntent);
        super.finish();

    }

    private void setSeen() {
        if (isRoom) {
            ChatService.service.setSeenByRoomId(DataLocalManager.getPrefUser().getAccessToken(), roomId)
                    .enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            if (response.body().getCode() == 1000) {
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {

                        }
                    });
        } else {
            if (partnerId != 0) {
                ChatService.service.setSeenSingle(DataLocalManager.getPrefUser().getAccessToken(), partnerId)
                        .enqueue(new Callback<BaseResponse>() {
                            @Override
                            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                if (response.body().getCode() == 1000) {
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<BaseResponse> call, Throwable t) {

                            }
                        });
            }
        }
    }
}