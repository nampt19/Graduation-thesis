package com.nampt.socialnetworkproject.view.writePost;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.model.Video;
import com.nampt.socialnetworkproject.util.WindowUtil;

import java.util.ArrayList;
import java.util.List;

public class VideoBottomSheetDialogFragment extends BottomSheetDialogFragment {
    RecyclerView rcvVideo;
    VideoBottomSheetAdapter adapter;
    Context mContext;
    ItemVideoListener itemSelectedListener;

    public VideoBottomSheetDialogFragment(ItemVideoListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet_select_video_fragment, null);
        dialog.setContentView(viewDialog);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                FrameLayout bottomSheet = ((BottomSheetDialog) dialog).findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = WindowUtil.getInstance(mContext).getWindowHeight();
                }
                bottomSheet.setLayoutParams(layoutParams);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        addControl(viewDialog);
        addEvent(viewDialog);
        return dialog;
    }

    private void addEvent(View viewDialog) {

    }

    private void addControl(View viewDialog) {
        rcvVideo = viewDialog.findViewById(R.id.rcv_video);
        adapter = new VideoBottomSheetAdapter(viewDialog.getContext(), new ItemVideoListener() {
            @Override
            public void onVideoSelected(String path, long size) {
                itemSelectedListener.onVideoSelected(path, size);
            }

        });
        GridLayoutManager layoutManager = new GridLayoutManager(viewDialog.getContext(), 3);
        adapter.setVideoList(getVideoList(viewDialog.getContext()));
        rcvVideo.setLayoutManager(layoutManager);
        rcvVideo.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private List<Video> getVideoList(Context context) {
        List<Video> list = new ArrayList<>();
        Uri uri;
        Cursor cursor;
        int columnIndexData, columnThumb, columnSize, columnDuration;
        String absolutePathOfImage = null;
        String thumbnail = null;
        long size = 0, duration = 0;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] proj = {MediaStore.MediaColumns.DATA, MediaStore.Video.Thumbnails.DATA,
                         MediaStore.Video.Media.SIZE, MediaStore.Video.Media.RESOLUTION,
                         MediaStore.Video.VideoColumns.DURATION};
        cursor = context.getApplicationContext().getContentResolver().query(uri, proj, null, null, null);

        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        columnThumb = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
        columnSize = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
        columnDuration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(columnIndexData);
            thumbnail = cursor.getString(columnThumb);
            size = cursor.getLong(columnSize);
            duration = cursor.getLong(columnDuration);
            Video video = new Video(absolutePathOfImage, thumbnail, size, duration);
            list.add(video);
        }
        list.add(0, new Video(null, null, 0, 0));
        return list;
    }

}
