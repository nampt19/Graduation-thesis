package com.nampt.socialnetworkproject.view.writePost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.model.Video;

import java.util.List;

public class VideoBottomSheetAdapter extends RecyclerView.Adapter<VideoBottomSheetAdapter.VideoViewHolder> {
    private Context mContext;
    private List<Video> videoList;
    private ItemVideoListener iSelectedListener;
    static final int REQUEST_VIDEO_CAPTURE = 1;

    public VideoBottomSheetAdapter(Context mContext, ItemVideoListener itemSelectedListener) {
        this.mContext = mContext;
        this.iSelectedListener = itemSelectedListener;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        final Video video = videoList.get(position);
        if (video == null) return;
        if (position == 0) {
            Glide.with(mContext).load(R.drawable.record_video).into(holder.imgPreview);
            holder.txtTimeVideo.setVisibility(View.GONE);
            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(mContext.getPackageManager()) != null) {
                        ((Activity) mContext).startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                    }
                }
            });
        } else {
            Glide.with(mContext).load(video.getThumb()).placeholder(R.drawable.null_bk).into(holder.imgPreview);
            holder.txtTimeVideo.setText(getTimeFromVideoDuration(video.getTimeInMs()));
            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iSelectedListener.onVideoSelected(video.getPath(), video.getSize());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (videoList == null || videoList.isEmpty()) return 0;
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPreview;
        private TextView txtTimeVideo;
        private View layoutItem;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPreview = itemView.findViewById(R.id.img_preview_video_bottom_sheet);
            txtTimeVideo = itemView.findViewById(R.id.txt_time_video);
            layoutItem = itemView.findViewById(R.id.layout_item_video);
        }
    }

    private String getTimeFromVideoDuration(long timeInMs) {
        int totalSecs = (int) (timeInMs / 1000);

        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format("%01d:%02d:%02d", hours, minutes, seconds);

    }
}
