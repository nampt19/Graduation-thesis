package com.nampt.socialnetworkproject.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nampt.socialnetworkproject.MainCallBacks;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.ViewPagerAdapter;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.notifyService.NotifyService;
import com.nampt.socialnetworkproject.api.notifyService.fcm.FcmService;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.view.chat.ChatFragment;
import com.nampt.socialnetworkproject.view.chat.GroupChatFragment;
import com.nampt.socialnetworkproject.view.friend.FriendFragment;
import com.nampt.socialnetworkproject.view.more.MoreFragment;
import com.nampt.socialnetworkproject.view.notification.NotificationActivity;
import com.nampt.socialnetworkproject.view.search.SearchActivity;

import hb.xvideoplayer.MxVideoPlayer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements MainCallBacks {
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    ImageView imgNotification, imgScan;
    LinearLayout layoutSearch;
    TextView txtTotalNotify;
    public static final int CODE_EDIT_POST = 2;
    public static final int CODE_WRITE_POST = 1;
    public static final int LAUNCH_NOTIFY_ACTIVITY = 3;
    ChatFragment chatFragment;
    GroupChatFragment groupChatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveDeviceToken();
        addControl();
        addEvent();
    }

    private void saveDeviceToken() {
        FirebaseMessaging.getInstance().subscribeToTopic("testfcm");
        String token= FirebaseInstanceId.getInstance().getToken();

        FcmService.service.saveDeviceToken(DataLocalManager.getPrefUser().getAccessToken(),
                DataLocalManager.getPrefUser().getId(),token)
        .enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().getCode()==1000){
                    Log.e("save fcm token","success");
                }else {
                    Log.e("save fcm token","failue");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e("save fcm token","failue");
            }
        });
    }

    private void addControl() {
        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.viewPager_controller);
        imgNotification = findViewById(R.id.img_notify_main_toolbar);
        layoutSearch = findViewById(R.id.layout_search_main_toolbar);
        imgScan = findViewById(R.id.img_scan_qr_code);
        txtTotalNotify = findViewById(R.id.txt_total_notify);
        txtTotalNotify.setVisibility(View.GONE);

        chatFragment = new ChatFragment();
        groupChatFragment = new GroupChatFragment();
        setUpBottomNav();
        setUpViewPager();
    }

    private void setUpBottomNav() {
        // chuyển  item được chọn tương ứng vs fragment
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        // get home_fragment
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_chat:
                        // get action_chat_fragment
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_group_chat:
                        // get group_chat_fragment
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.action_friend:
                        // get Friend_fragment
                        viewPager.setCurrentItem(3);
                        break;
                    case R.id.action_more:
                        // get More_fragment
                        viewPager.setCurrentItem(4);
                        break;

                }
                return true;
            }
        });
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdapter.addFrag(new HomeFragment(), "home");
        viewPagerAdapter.addFrag(chatFragment, "chat");
        viewPagerAdapter.addFrag(groupChatFragment, "group");
        viewPagerAdapter.addFrag(new FriendFragment(), "friend");
        viewPagerAdapter.addFrag(new MoreFragment(), "more");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        // chuyển fragment tương ứng vs item được chọn
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.action_chat).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.action_group_chat).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.action_friend).setChecked(true);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.action_more).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addEvent() {
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivityForResult(intent, LAUNCH_NOTIFY_ACTIVITY);

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            }
        });
        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        imgScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });
        getTotalNotify();
    }

    private void getTotalNotify() {
        NotifyService.service.getTotalNotify(DataLocalManager.getPrefUser().getAccessToken())
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            int totalNotify = Integer.parseInt(response.body().getMessage());
                            if (totalNotify == 0) {
                                txtTotalNotify.setVisibility(View.GONE);
                            } else {
                                txtTotalNotify.setVisibility(View.VISIBLE);
                                txtTotalNotify.setText(String.valueOf(totalNotify));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (MxVideoPlayer.backPress()) return;
        super.onBackPressed();
    }

    @Override
    public void onMsgFromFragToMain(String sender, int roomId) {
        if (sender.equals("ChatFragment")) {
            groupChatFragment.onMsgFromMainToFragment(roomId);
        } else if (sender.equals("GroupChatFragment")) {
            chatFragment.onMsgFromMainToFragment(roomId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_NOTIFY_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                int totalNotify = data.getIntExtra("totalNotify", 0);
                Log.e("sadasgsa", totalNotify + "");
                if (totalNotify == 0) {
                    txtTotalNotify.setVisibility(View.GONE);
                } else {
                    txtTotalNotify.setVisibility(View.VISIBLE);
                    txtTotalNotify.setText(totalNotify + "");
                }
            }
        }
    }
}