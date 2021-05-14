package com.nampt.socialnetworkproject.view.search.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.Friend;
import com.nampt.socialnetworkproject.model.PeopleSearch;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;
import com.nampt.socialnetworkproject.view.search.SearchActivity;

import java.util.List;

public class PeopleSearchAdapter extends RecyclerView.Adapter<PeopleSearchAdapter.ViewHolder> {
    private Context mContext;
    private List<PeopleSearch> peopleList;

    public PeopleSearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setPeopleList(List<PeopleSearch> peopleList) {
        this.peopleList = peopleList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_people, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PeopleSearch peopleSearch = peopleList.get(position);
        if (peopleSearch == null) return;

        holder.txtNameUser.setText(peopleSearch.getName());

        if (peopleSearch.getAvatar() != null)
            Glide.with(mContext).load(Host.HOST + peopleSearch.getAvatar()).placeholder(R.drawable.null_bk).into(holder.imgUser);
        else
            Glide.with(mContext).load(R.drawable.unnamed).into(holder.imgUser);

        if (peopleSearch.getType() != 1) {
            holder.icFriend.setVisibility(View.GONE);
        } else holder.icFriend.setVisibility(View.VISIBLE);

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileUserActivity.class);
                intent.putExtra("userId", peopleSearch.getId());
                ((Activity) mContext).startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                ((Activity) mContext).finish();
            }
        });
    }


    @Override
    public int getItemCount() {
        if (peopleList == null || peopleList.isEmpty()) return 0;
        return peopleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgUser, icFriend;
        private TextView txtNameUser;
        private View layoutItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.img_people_search);
            txtNameUser = itemView.findViewById(R.id.txt_name_people_search);
            icFriend = itemView.findViewById(R.id.ic_friend_search);
            layoutItem = itemView.findViewById(R.id.layout_item_people_search);
        }
    }
}
