package com.nampt.socialnetworkproject.view.writePost;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;

import java.util.List;

public class PhotoWritePostAdapter extends RecyclerView.Adapter<PhotoWritePostAdapter.ViewHolder> {
    private Context mConText;
    private List<Uri> mListPhotoUri;

    public PhotoWritePostAdapter(Context mConText) {
        this.mConText = mConText;
    }

    public void setUriList(List<Uri> uriList) {
        this.mListPhotoUri = uriList;
        notifyDataSetChanged();
    }

    public List<Uri> getUriList() {
        return mListPhotoUri;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View postView = inflater.inflate(R.layout.item_photo_write_post, parent, false);
        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = mListPhotoUri.get(position);
        if (uri == null) return;
        Glide.with(mConText).load(uri).into(holder.photoWritePost);
    }

    @Override
    public int getItemCount() {
        if (mListPhotoUri != null) return mListPhotoUri.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView photoWritePost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoWritePost = itemView.findViewById(R.id.item_photo_write_post);
        }
    }


}
