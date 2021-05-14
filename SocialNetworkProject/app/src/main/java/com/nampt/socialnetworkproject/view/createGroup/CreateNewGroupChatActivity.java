package com.nampt.socialnetworkproject.view.createGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.GsonBuilder;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.chatService.ChatService;
import com.nampt.socialnetworkproject.api.chatService.CreateGroupChatRequest;
import com.nampt.socialnetworkproject.api.chatService.GroupChatResponse;
import com.nampt.socialnetworkproject.api.friendService.FriendService;
import com.nampt.socialnetworkproject.api.friendService.response.ListFriendResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.view.createGroup.adapter.CreateGroupAdapter;
import com.nampt.socialnetworkproject.view.createGroup.adapter.FriendsGroupAdapter;
import com.nampt.socialnetworkproject.model.Friend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewGroupChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView rcvGroupAdding, rcvFriends;
    TextInputLayout txtCreateGroup;
    CreateGroupAdapter createGroupAdapter;
    FriendsGroupAdapter friendsAdapter;
    Toolbar toolbar;
    View layoutNoData, layoutLoading, layoutNoNetwork;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout layoutContainerAddGroup;
    ImageView btnAddGroup;
    List<Friend> mFriendList = new ArrayList<>();
    List<Friend> friendsSelectedToCreateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat);
        addControl();
        initToolBar();
        initRcv();
        addEvent();

    }

    private void addControl() {
        layoutContainerAddGroup = findViewById(R.id.layout_container_adding_group);
        btnAddGroup = findViewById(R.id.btn_verify_add_new_group);
        toolbar = findViewById(R.id.create_group_chat_toolbar);
        rcvGroupAdding = findViewById(R.id.rcv_group_adding);
        txtCreateGroup = findViewById(R.id.txt_create_group);
        rcvFriends = findViewById(R.id.rcv_friends);
        layoutNoData = findViewById(R.id.layout_no_data_create_group);
        layoutLoading = findViewById(R.id.layout_progress_loading_create_group);
        layoutNoNetwork = findViewById(R.id.layout_no_network_create_group);
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
        swipeRefreshLayout = findViewById(R.id.swipe_fresh_layout_create_group);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueThin));
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tạo nhóm mới");
        }
    }

    private void initRcv() {
        // set rcv + adapter for list user adding
        createGroupAdapter = new CreateGroupAdapter(this);
        createGroupAdapter.setUserList(null);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rcvGroupAdding.setLayoutManager(horizontalLayoutManager);
        rcvGroupAdding.setAdapter(createGroupAdapter);

        // set rcv and adapter for list friend + listener checked to add user into group
        friendsSelectedToCreateGroup = new ArrayList<>();
        layoutContainerAddGroup.setVisibility(View.GONE);
        friendsAdapter = new FriendsGroupAdapter(this, new FriendsGroupAdapter.IListener() {
            @Override
            public void onItemChecked(Friend friend, boolean isChecked) {
                if (isChecked) friendsSelectedToCreateGroup.add(friend);
                else friendsSelectedToCreateGroup.remove(friend);

                if (friendsSelectedToCreateGroup.size() == 0)
                    layoutContainerAddGroup.setVisibility(View.GONE);
                else layoutContainerAddGroup.setVisibility(View.VISIBLE);

                createGroupAdapter.setUserList(friendsSelectedToCreateGroup);
            }
        });
        friendsAdapter.setFriendList(mFriendList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvFriends.setLayoutManager(linearLayoutManager);
        rcvFriends.setAdapter(friendsAdapter);
    }

    private void addEvent() {
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendsSelectedToCreateGroup.size() < 2)
                    Toast.makeText(CreateNewGroupChatActivity.this, "Nhóm tối thiểu 3 người", Toast.LENGTH_SHORT).show();

                else {
                    if (!validateNameGroup(txtCreateGroup)) {
                        return;
                    }
                    handleCreateGroupChat();
                }
            }
        });
        setFirstData();
    }

    private void handleCreateGroupChat() {

        final List<Integer> partnerIds = new ArrayList<>();
        for (Friend friend : friendsSelectedToCreateGroup) {
            partnerIds.add(friend.getId());
        }

        final CreateGroupChatRequest request = new CreateGroupChatRequest();
        request.setName(txtCreateGroup.getEditText().getText().toString().trim());
        request.setPartnerIds(partnerIds);
        request.setCreateTime(new Date());
        ChatService.service.
                createGroupChat(DataLocalManager.getPrefUser().getAccessToken(), request)
                .enqueue(new Callback<GroupChatResponse>() {
                    @Override
                    public void onResponse(Call<GroupChatResponse> call, Response<GroupChatResponse> response) {
                        if (response.body().getCode() == 1000) {
                            Toast.makeText(CreateNewGroupChatActivity.this, "Tạo nhóm thành công !", Toast.LENGTH_SHORT).show();
                            Intent returnIntent = new Intent();
                            returnIntent.putIntegerArrayListExtra("partnerIds", (ArrayList<Integer>) partnerIds);
                            returnIntent.putExtra("roomName", request.getName());
                            returnIntent.putExtra("roomId", response.body().getConversationId());
                            returnIntent.putExtra("createTime",new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(request.getCreateTime()) );
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();

                        } else if (response.body().getCode() == 9992) {
                            Toast.makeText(CreateNewGroupChatActivity.this, "Người dùng không hợp lệ", Toast.LENGTH_SHORT).show();

                        } else if (response.body().getCode() == 9995) {
                            Toast.makeText(CreateNewGroupChatActivity.this, "Tên nhóm bị trùng !", Toast.LENGTH_SHORT).show();

                        } else if (response.body().getCode() == 9993) {
                            Toast.makeText(CreateNewGroupChatActivity.this, "Sai token", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<GroupChatResponse> call, Throwable t) {
                        Toast.makeText(CreateNewGroupChatActivity.this, "Lỗi kết nối, vui lòng thử lại !", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFirstData() {
        layoutLoading.setVisibility(View.VISIBLE);
        FriendService.service
                .getFriendList(DataLocalManager.getPrefUser().getAccessToken(), 0, 1000)
                .enqueue(new Callback<ListFriendResponse>() {
                    @Override
                    public void onResponse(Call<ListFriendResponse> call, Response<ListFriendResponse> response) {
                        layoutLoading.setVisibility(View.GONE);
                        if (response.body().getCode() == 1000) {
                            if (response.body().getData().size() == 0) {
                                layoutNoData.setVisibility(View.VISIBLE);
                                return;
                            }
                            for (Friend friend : response.body().getData()) {
                                if (!friend.isBlock()) {
                                    mFriendList.add(friend);
                                }
                            }
                            friendsAdapter.setFriendList(mFriendList);
                        } else if (response.body().getCode() == 9993) {
                            Toast.makeText(CreateNewGroupChatActivity.this, "Sai token !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ListFriendResponse> call, Throwable t) {
                        layoutLoading.setVisibility(View.GONE);
                        layoutNoNetwork.setVisibility(View.VISIBLE);
                        layoutNoNetwork.findViewById(R.id.btn_reload_network_utils).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutNoNetwork.setVisibility(View.GONE);
                                setFirstData();
                            }
                        });

                    }
                });
    }

    public static boolean validateNameGroup(TextInputLayout txtCreateGroup) {
        String phoneNumber = txtCreateGroup.getEditText().getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            txtCreateGroup.setError("Chưa nhập tên nhóm !");
            return false;
        } else if (phoneNumber.length() > 30) {
            txtCreateGroup.setError("Tên nhóm quá dài !");
            return false;
        } else {
            txtCreateGroup.setError(null);
            return true;
        }
    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        mFriendList.clear();
        friendsSelectedToCreateGroup.clear();
        if (layoutContainerAddGroup.getVisibility() == View.VISIBLE)
            layoutContainerAddGroup.setVisibility(View.GONE);
        setFirstData();
    }

}