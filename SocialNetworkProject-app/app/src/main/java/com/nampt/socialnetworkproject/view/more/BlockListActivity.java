package com.nampt.socialnetworkproject.view.more;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.friendService.FriendService;
import com.nampt.socialnetworkproject.api.friendService.response.ListFriendResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.Friend;
import com.nampt.socialnetworkproject.view.more.adapter.BlockAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockListActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rcvBlock;
    BlockAdapter mAdapter;
    List<Friend> mFriendBLockList = new ArrayList<>();
    View layoutNoData, layoutLoading, layoutNoNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_list);
        addControl();
        initToolbar();
        initRcv();
        addEvent();

    }

    private void addEvent() {
        setFistData();
    }

    private void setFistData() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoNetwork.setVisibility(View.GONE);
        FriendService.service.getBlockList(DataLocalManager.getPrefUser().getAccessToken())
                .enqueue(new Callback<ListFriendResponse>() {
                    @Override
                    public void onResponse(Call<ListFriendResponse> call, Response<ListFriendResponse> response) {
                        layoutLoading.setVisibility(View.GONE);
                        if (response.body().getCode() == 1000) {
                            List<Friend> friends = response.body().getData();
                            if (friends.isEmpty()) {
                                layoutNoData.setVisibility(View.VISIBLE);
                                return;
                            }
                            mFriendBLockList = friends;
                            mAdapter.setFriendList(mFriendBLockList);
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
                                setFistData();
                            }
                        });
                    }
                });
    }

    private void initRcv() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mAdapter = new BlockAdapter(this, new BlockAdapter.ItemCLickListener() {
            @Override
            public void onItemClicked(final int userId,String name) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BlockListActivity.this)
                        .setTitle("Bỏ chặn "+ name +" ?")
                        .setMessage("Nếu bạn bỏ chặn "+name+", " +
                                ""+name+" có thể xem dòng thời gian và trò chuyện với bạn.")
                        .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                handleUnBlockUser(userId);
                            }
                        }).setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        mAdapter.setFriendList(mFriendBLockList);

        rcvBlock.setLayoutManager(layoutManager);
        rcvBlock.setAdapter(mAdapter);

    }

    private void handleUnBlockUser(final int userId) {
        FriendService.service.unblock(DataLocalManager.getPrefUser().getAccessToken(), userId)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            for (int i = 0; i < mFriendBLockList.size(); i++) {
                                if (mFriendBLockList.get(i).getId() == userId) {
                                    mFriendBLockList.remove(i);
                                    mAdapter.notifyItemRemoved(i);
                                }
                            }

                            Toast.makeText(BlockListActivity.this, "Bỏ chặn Thành công !", Toast.LENGTH_LONG).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(BlockListActivity.this, "Lỗi kết nối !", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void addControl() {
        toolbar = findViewById(R.id.toolbar_block_list_activity);
        rcvBlock = findViewById(R.id.rcvBlockList);
        layoutNoData = findViewById(R.id.layout_no_data_block_list_activity);
        layoutLoading = findViewById(R.id.layout_progress_loading_block_list_activity);
        layoutNoNetwork = findViewById(R.id.layout_no_network_block_list_activity);
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Danh sách chặn");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}