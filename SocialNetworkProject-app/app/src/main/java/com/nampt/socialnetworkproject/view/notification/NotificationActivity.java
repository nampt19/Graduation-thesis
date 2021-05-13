package com.nampt.socialnetworkproject.view.notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.notifyService.ListNotifyResponse;
import com.nampt.socialnetworkproject.api.notifyService.NotifyService;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.Notification;
import com.nampt.socialnetworkproject.view.search.SearchActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rcvNotification;
    private NotificationAdapter notificationAdapter;
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Notification> mNotifyList = new ArrayList<>();
    View layoutNoData, layoutLoading, layoutNoNetwork;
    public static final int TYPE_NOTIFY_POST = 1, TYPE_NOTIFY_FRIEND_REQUEST = 2, TYPE_NOTIFY_CHAT_SINGLE = 3,
            TYPE_NOTIFY_CHAT_GROUP = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        addControl();
        addEvent();
    }

    private void addControl() {
        toolbar = findViewById(R.id.toolbar_notification_activity);
        rcvNotification = findViewById(R.id.rcv_notification);
        swipeRefreshLayout = findViewById(R.id.swipe_fresh_layout_notify);
        layoutNoData = findViewById(R.id.layout_no_data_notify);
        layoutLoading = findViewById(R.id.layout_progress_loading_notify);
        layoutNoNetwork = findViewById(R.id.layout_no_network_notify);
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notificationAdapter = new NotificationAdapter(this);
        notificationAdapter.setNotificationList(mNotifyList);
        rcvNotification.setAdapter(notificationAdapter);
        rcvNotification.setLayoutManager(layoutManager);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueThin));
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    private void addEvent() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thông báo");
        }
        setFistData();

    }

    private void setFistData() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoNetwork.setVisibility(View.GONE);
        layoutNoData.setVisibility(View.GONE);
        NotifyService.service.getListNotify(DataLocalManager.getPrefUser().getAccessToken())
                .enqueue(new Callback<ListNotifyResponse>() {
                    @Override
                    public void onResponse(Call<ListNotifyResponse> call, Response<ListNotifyResponse> response) {
                        layoutLoading.setVisibility(View.GONE);
                        if (response.body().getCode() == 1000) {
                            mNotifyList = response.body().getData();
                            if (mNotifyList.isEmpty()) {
                                layoutNoData.setVisibility(View.VISIBLE);
                                return;
                            }
                            Iterator<Notification> it = mNotifyList.iterator();
                            while (it.hasNext()) {
                                Notification n = it.next();
                                //remove notify chat (single + group)
                                if (n.getType() == 3 || n.getType() == 4 || n.getSender().isBlock()) {
                                    it.remove();
                                }
                            }
                            Collections.reverse(mNotifyList);
                            notificationAdapter.setNotificationList(mNotifyList);
                        }
                    }

                    @Override
                    public void onFailure(Call<ListNotifyResponse> call, Throwable t) {
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void finish() {

        Iterator<Notification> it = mNotifyList.iterator();
        while (it.hasNext()) {
            Notification n = it.next();
            if (n.isSeen())
                it.remove();
        }

        Intent replyIntent = new Intent();
        replyIntent.putExtra("totalNotify",mNotifyList.size());
        setResult(Activity.RESULT_OK,replyIntent);
        super.finish();
    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        mNotifyList.clear();
        setFistData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SearchActivity.LAUNCH_PROFILE_ACTIVITY) {
            if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
                    int idUserBlock = data.getIntExtra("idUserBlock", 0);
                    for (Notification n : mNotifyList) {
                        if (n.getSender().getId() == idUserBlock) {
                            mNotifyList.remove(n);
                            break;
                        }
                    }
                    notificationAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}