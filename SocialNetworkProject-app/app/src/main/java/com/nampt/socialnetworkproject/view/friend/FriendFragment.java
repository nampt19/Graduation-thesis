package com.nampt.socialnetworkproject.view.friend;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;

public class FriendFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    TextView btnFriend, btnFriendRequest, btnSentRequest;
    Context mContext;
    SwipeRefreshLayout swipeRefreshLayout;
    int typeTapSelected = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        addControl(view);
        addEvent(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setBackgroundNavigationButton(btnFriend, btnFriendRequest, btnSentRequest);
        createFragment(FriendTap1Fragment.newInstance());
    }

    private void addEvent(View view) {
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeTapSelected=1;
                setBackgroundNavigationButton(btnFriend, btnFriendRequest, btnSentRequest);
                createFragment(FriendTap1Fragment.newInstance());
            }
        });
        btnFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeTapSelected=2;
                setBackgroundNavigationButton(btnFriendRequest, btnFriend, btnSentRequest);
                createFragment(FriendTap2Fragment.newInstance());
            }
        });
        btnSentRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeTapSelected=3;
                setBackgroundNavigationButton(btnSentRequest, btnFriend, btnFriendRequest);
                createFragment(FriendTap3Fragment.newInstance());
            }
        });
    }

    private void addControl(View view) {
        btnFriend = view.findViewById(R.id.btn_friend_all);
        btnFriendRequest = view.findViewById(R.id.btn_friend_request);
        btnSentRequest = view.findViewById(R.id.btn_request_sent);
        swipeRefreshLayout = view.findViewById(R.id.swipe_fresh_layout_friend);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueThin));
    }

    private void createFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.holder_fragment_friend_page, fragment);
        transaction.commit();
    }

    private void setBackgroundNavigationButton(TextView btnSelected, TextView b1, TextView b2) {
        btnSelected.setBackgroundResource(R.drawable.background_btn_selected);
        btnSelected.setTextColor(getResources().getColor(R.color.whiteCardColor));
        b1.setBackgroundResource(R.drawable.background_btn);
        b1.setTextColor(getResources().getColor(android.R.color.black));
        b2.setBackgroundResource(R.drawable.background_btn);
        b2.setTextColor(getResources().getColor(android.R.color.black));
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        switch (typeTapSelected) {
            case 1:
                setBackgroundNavigationButton(btnFriend, btnFriendRequest, btnSentRequest);
                createFragment(FriendTap1Fragment.newInstance());
                break;
            case 2:
                setBackgroundNavigationButton(btnFriendRequest, btnFriend, btnSentRequest);
                createFragment(FriendTap2Fragment.newInstance());
                break;
            case 3:
                setBackgroundNavigationButton(btnSentRequest, btnFriend, btnFriendRequest);
                createFragment(FriendTap3Fragment.newInstance());
                break;
        }

    }
}