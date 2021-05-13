package com.nampt.socialnetworkproject.view.profileUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;

import java.util.List;

public class ImageAlbumAdapter extends RecyclerView.Adapter<ImageAlbumAdapter.AlbumViewHolder> {
    List<String> urlImageList;
    Context mContext;

    public ImageAlbumAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setUrlImageList(List<String> urlImageList) {
        this.urlImageList = urlImageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);

        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        String urlImage = urlImageList.get(position);
        if (urlImage == null) return;

        Glide.with(mContext).load(Host.HOST + urlImage).placeholder(R.drawable.null_bk).into(holder.imgAlbum);

    }

    @Override
    public int getItemCount() {
        if (urlImageList == null || urlImageList.isEmpty()) return 0;
        else
            return urlImageList.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAlbum;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAlbum = itemView.findViewById(R.id.img_album);
        }
    }
}
