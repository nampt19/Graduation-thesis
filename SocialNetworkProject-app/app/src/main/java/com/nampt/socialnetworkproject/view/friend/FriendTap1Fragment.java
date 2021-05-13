package com.nampt.socialnetworkproject.view.friend;

import android.content.Context;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nampt.socialnetworkproject.PaginationScrollListener;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.friendService.FriendService;
import com.nampt.socialnetworkproject.api.friendService.response.ListFriendResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.view.friend.adapter.FriendAdapter;
import com.nampt.socialnetworkproject.model.Friend;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendTap1Fragment extends Fragment {
    Context mContext;
    RecyclerView rcvFriend;
    FriendAdapter friendAdapter;
    TextView txtTotalFriend;
    List<Friend> mFriendList = new ArrayList<>();
    View layoutNoData, layoutLoading, layoutNoNetwork;
    NestedScrollView nestedScrollView;
    int index = 0, count = 10;
    boolean isLoading, isLastPage;
    String accessToken = DataLocalManager.getPrefUser().getAccessToken();

    public static FriendTap1Fragment newInstance() {
        FriendTap1Fragment fragment = new FriendTap1Fragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_tab_1, container, false);
        addControl(view);
        initRcvFriend();
        addEvent();
        return view;
    }

    private void addControl(View view) {
        rcvFriend = view.findViewById(R.id.rcv_list_friends);
        txtTotalFriend = view.findViewById(R.id.txt_total_friend);
        layoutNoData = view.findViewById(R.id.layout_no_data_friend);
        layoutLoading = view.findViewById(R.id.layout_progress_loading_friend);
        layoutNoNetwork = view.findViewById(R.id.layout_no_network_friend);
        nestedScrollView = view.findViewById(R.id.nested_scroll_view_friend);
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
    }

    private void initRcvFriend() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        friendAdapter = new FriendAdapter(mContext);
        friendAdapter.setFriendList(mFriendList);
        rcvFriend.setLayoutManager(layoutManager);
        rcvFriend.setAdapter(friendAdapter);
        rcvFriend.setNestedScrollingEnabled(false);
        nestedScrollView
                .getViewTreeObserver()
                .addOnScrollChangedListener(new PaginationScrollListener(nestedScrollView, layoutManager) {
                    @Override
                    public void loadMoreItems() {
                        isLoading = true;
                        loadNextPage();
                    }

                    @Override
                    public boolean isLoading() {
                        return isLoading;
                    }

                    @Override
                    public boolean isLastPage() {
                        return isLastPage;
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void addEvent() {
        setFirstData();
    }

    private void setFirstData() {
        layoutLoading.setVisibility(View.VISIBLE);
        FriendService.service
                .getFriendList(accessToken, index, count)
                .enqueue(new Callback<ListFriendResponse>() {
                    @Override
                    public void onResponse(Call<ListFriendResponse> call, Response<ListFriendResponse> response) {
                        layoutLoading.setVisibility(View.GONE);
                        if (response.body().getCode() == 1000) {
                            mFriendList = response.body().getData();
                            if (mFriendList.size() == 0) {
                                layoutNoData.setVisibility(View.VISIBLE);
                                return;
                            }
                            friendAdapter.setFriendList(mFriendList);
                            txtTotalFriend.setText(response.body().getTotalFriend()+"");

                            if (mFriendList.size() < count) {
                                isLastPage = true;
                            } else {
                                index += count;
                                friendAdapter.addFooterLoading();
                            }

                        } else if (response.body().getCode() == 9993) {
                            Toast.makeText(mContext, "Sai token !", Toast.LENGTH_SHORT).show();
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


    private void loadNextPage() {
        Toast.makeText(mContext, index + "-" + count, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FriendService.service.getFriendList(accessToken, index, count).enqueue(new Callback<ListFriendResponse>() {
                    @Override
                    public void onResponse(Call<ListFriendResponse> call, Response<ListFriendResponse> response) {
                        if (response.body().getCode() == 1000) {
                            List<Friend> newFriendList = response.body().getData();
                            friendAdapter.removeFooterLoading();
                            isLoading = false;
                            mFriendList.addAll(newFriendList);
                            friendAdapter.notifyDataSetChanged();
                            if (newFriendList.size() < count) {
                                isLastPage = true;
                            } else {
                                friendAdapter.addFooterLoading();
                                index += count;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ListFriendResponse> call, Throwable t) {
                        isLoading = false;
                        friendAdapter.removeFooterLoading();
                        Toast.makeText(mContext, "Lỗi kết nối,vui lòng thử lại sau !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 1500);
    }


}