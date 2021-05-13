package com.nampt.socialnetworkproject.view.friend;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.friendService.FriendService;
import com.nampt.socialnetworkproject.api.friendService.response.ListFriendResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.view.friend.adapter.FriendSentAdapter;
import com.nampt.socialnetworkproject.model.Friend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendTap3Fragment extends Fragment {
    Context mContext;
    RecyclerView rcvFriendSent;
    FriendSentAdapter friendSentAdapter;
    TextView txtTotalFriendSent;
    List<Friend> mFriendList = new ArrayList<>();
    View layoutNoData, layoutLoading, layoutNoNetwork;
    ITypeFriendTapListener typeFriendTapListener;

    public static FriendTap3Fragment newInstance() {
        FriendTap3Fragment fragment = new FriendTap3Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_tap_3, container, false);
        addControl(view);
        initRcvFriendSent();
        addEvent();
        return view;
    }

    private void addEvent() {
        handleGetInvitationSentFriends();
    }

    private void handleGetInvitationSentFriends() {
        layoutLoading.setVisibility(View.VISIBLE);
        FriendService.service.getInvitationSentFriendList(DataLocalManager.getPrefUser().getAccessToken()).enqueue(new Callback<ListFriendResponse>() {
            @Override
            public void onResponse(Call<ListFriendResponse> call, Response<ListFriendResponse> response) {
                layoutLoading.setVisibility(View.GONE);
                if (response.body().getCode() == 1000) {
                    List<Friend> friends = response.body().getData();
                    Iterator<Friend> it = friends.iterator();
                    while (it.hasNext()) {
                        Friend friend = it.next();
                        if (friend.isBlock()) {
                            it.remove();
                        }
                    }
                    if (friends.isEmpty()) {
                        layoutNoData.setVisibility(View.VISIBLE);
                        return;
                    }
                    mFriendList = friends;
                    friendSentAdapter.setFriendList(mFriendList);
                    txtTotalFriendSent.setText(String.valueOf(mFriendList.size()));
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
                        handleGetInvitationSentFriends();
                    }
                });
            }
        });
    }

    private void initRcvFriendSent() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        friendSentAdapter = new FriendSentAdapter(mContext);
        friendSentAdapter.setFriendList(mFriendList);
        rcvFriendSent.setLayoutManager(layoutManager);
        rcvFriendSent.setAdapter(friendSentAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void addControl(View view) {
        rcvFriendSent = view.findViewById(R.id.rcv_list_request_sent);
        txtTotalFriendSent = view.findViewById(R.id.txt_total_request_sent);
        layoutNoData = view.findViewById(R.id.layout_no_data_friend_sent);
        layoutLoading = view.findViewById(R.id.layout_progress_loading_friend_sent);
        layoutNoNetwork = view.findViewById(R.id.layout_no_network_friend_sent);
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);
    }
}
