package com.nampt.socialnetworkproject.view.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.userService.UserService;
import com.nampt.socialnetworkproject.api.userService.response.ListPeopleResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.PeopleSearch;
import com.nampt.socialnetworkproject.view.search.adapter.PeopleSearchAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleSearchFragment extends Fragment implements FragmentCallbacks {
    private Context mContext;
    RecyclerView rcvPeopleSearch;
    PeopleSearchAdapter mAdapter;
    View layoutNoData, layoutLoading, layoutNoNetwork;
    List<PeopleSearch> mList = new ArrayList<>();
    String searchField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_people, container, false);
        addControl(view);
        initRcv();
        addEvent();

        return view;
    }


    private void addControl(View view) {
        rcvPeopleSearch = view.findViewById(R.id.rcv_search_people);
        layoutNoData = view.findViewById(R.id.layout_no_data_search_people);
        layoutLoading = view.findViewById(R.id.layout_progress_loading_search_people);
        layoutNoNetwork = view.findViewById(R.id.layout_no_network_search_people);
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
        layoutNoNetwork.setVisibility(View.GONE);

    }

    private void initRcv() {
        mAdapter = new PeopleSearchAdapter(mContext);
        LinearLayoutManager linearLayout = new LinearLayoutManager(mContext);
        mAdapter.setPeopleList(mList);
        rcvPeopleSearch.setAdapter(mAdapter);
        rcvPeopleSearch.setLayoutManager(linearLayout);
    }


    private void addEvent() {

    }

    private void handleGetPeopleList() {
        layoutNoData.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoNetwork.setVisibility(View.GONE);
        UserService.service.searchPeoplesByName(DataLocalManager.getPrefUser().getAccessToken(), searchField)
                .enqueue(new Callback<ListPeopleResponse>() {
                    @Override
                    public void onResponse(Call<ListPeopleResponse> call, Response<ListPeopleResponse> response) {
                        layoutLoading.setVisibility(View.GONE);
                        if (response.body().getCode() == 1000) {
                            List<PeopleSearch> peopleList = response.body().getData();
                            Iterator<PeopleSearch> it = peopleList.iterator();
                            while (it.hasNext()) {
                                PeopleSearch pp = it.next();
                                if (pp.isBlock()) it.remove();
                            }
                            if (peopleList.isEmpty()) {
                                layoutNoData.setVisibility(View.VISIBLE);
                                mList.clear();
                                mAdapter.notifyDataSetChanged();
                                return;
                            }

                            mList = peopleList;
                            mAdapter.setPeopleList(mList);
                        }

                    }

                    @Override
                    public void onFailure(Call<ListPeopleResponse> call, Throwable t) {
                        layoutLoading.setVisibility(View.GONE);
                        layoutNoNetwork.setVisibility(View.VISIBLE);
                        layoutNoNetwork.findViewById(R.id.btn_reload_network_utils).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutNoNetwork.setVisibility(View.GONE);
                                handleGetPeopleList();
                            }
                        });
                    }
                });
    }

    @Override
    public void onMsgFromMainToFragment(String strValue) {
        searchField = strValue;
        handleGetPeopleList();
    }
}