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
import com.nampt.socialnetworkproject.view.friend.adapter.FriendRequestAdapter;
import com.nampt.socialnetworkproject.model.Friend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendTap2Fragment extends Fragment {
    Context mContext;
    RecyclerView rcvFriendRequest;
    FriendRequestAdapter friendRequestAdapter;
    TextView txtTotalFriendRequest;
    List<Friend> mFriendList = new ArrayList<>();
    View layoutNoData, layoutLoading, layoutNoNetwork;
    private static String TYPE_TAP_2 = "FRIEND_REQUEST";

    public static FriendTap2Fragment newInstance() {
        FriendTap2Fragment fragment = new FriendTap2Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_tap_2, container, false);
        addControl(view);
        initRcvRequestFriend();
        addEvent();
        return view;
    }

    private void addEvent() {
        handleGetRequestedFriends();
    }

    private void handleGetRequestedFriends() {
        layoutLoading.setVisibility(View.VISIBLE);
        FriendService.service.getRequestedFriendList(DataLocalManager.getPrefUser().getAccessToken()).enqueue(new Callback<ListFriendResponse>() {
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
                    friendRequestAdapter.setFriendList(mFriendList);
                    txtTotalFriendRequest.setText(String.valueOf(mFriendList.size()));
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
                        handleGetRequestedFriends();
                    }
                });

            }
        });
    }

    private void initRcvRequestFriend() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        friendRequestAdapter = new FriendRequestAdapter(mContext);
        friendRequestAdapter.setFriendList(mFriendList);
        rcvFriendRequest.setLayoutManager(layoutManager);
        rcvFriendRequest.setAdapter(friendRequestAdapter);
    }


    private void addControl(View view) {
        rcvFriendRequest = view.findViewById(R.id.rcv_list_friends_request);
        txtTotalFriendRequest = view.findViewById(R.id.txt_total_friend_request);

        layoutNoData = view.findViewById(R.id.layout_no_data_friend_request);
        layoutLoading = view.findViewById(R.id.layout_progress_loading_friend_request);
        layoutNoNetwork = view.findViewById(R.id.layout_no_network_friend_request);
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);

    }
}
