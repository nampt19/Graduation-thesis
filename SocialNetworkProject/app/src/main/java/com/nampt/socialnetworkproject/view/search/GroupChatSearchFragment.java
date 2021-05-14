package com.nampt.socialnetworkproject.view.search;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.chatService.ChatService;
import com.nampt.socialnetworkproject.api.chatService.ListConversationResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.ChatRow2;
import com.nampt.socialnetworkproject.model.Friend;
import com.nampt.socialnetworkproject.view.chat.adapter.ChatGroupAdapter;
import com.nampt.socialnetworkproject.view.search.adapter.PeopleSearchAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;


public class GroupChatSearchFragment extends Fragment implements FragmentCallbacks {
    private Context mContext;
    RecyclerView rcvChatSearch;
    View layoutNoData, layoutLoading, layoutNoNetwork;
    ChatGroupAdapter chatAdapter;
    private List<ChatRow2> mChatRowList = new ArrayList<>();
    private String searchField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_group_chat, container, false);
        addControl(view);
        initRcv();
        return view;
    }

    private void addControl(View view) {
        rcvChatSearch = view.findViewById(R.id.rcv_search_group_chat);

        layoutNoData = view.findViewById(R.id.layout_no_data_group_chat_search);
        layoutLoading = view.findViewById(R.id.layout_progress_loading_group_chat_search);
        layoutNoNetwork = view.findViewById(R.id.layout_no_network_group_chat_search);

        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);

    }

    private void initRcv() {
        chatAdapter = new ChatGroupAdapter(mContext,this);
        LinearLayoutManager linearLayout = new LinearLayoutManager(mContext);
        chatAdapter.setChatRowList(mChatRowList);
        rcvChatSearch.setAdapter(chatAdapter);
        rcvChatSearch.setLayoutManager(linearLayout);
    }



    private void setFirstDataConversationList() {
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoNetwork.setVisibility(View.GONE);
        ChatService.service.searchConversationsByName(DataLocalManager.getPrefUser().getAccessToken(), searchField)
                .enqueue(new Callback<ListConversationResponse>() {
                    @Override
                    public void onResponse(Call<ListConversationResponse> call, Response<ListConversationResponse> response) {
                        layoutLoading.setVisibility(View.GONE);
                        if (response.body().getCode() == 1000) {
                            List<ChatRow2> chatRows = response.body().getData();
                            if (chatRows.isEmpty()) {
                                layoutNoData.setVisibility(View.VISIBLE);
                                mChatRowList.clear();
                                chatAdapter.notifyDataSetChanged();
                                return;
                            }

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

                            }
                        });
                    }
                });
    }

    @Override
    public void onMsgFromMainToFragment(String strValue) {
        searchField = strValue;
        setFirstDataConversationList();

    }

}