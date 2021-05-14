package com.nampt.socialnetworkproject.view.writePost;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.model.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;


public class WritePostActivity extends AppCompatActivity {
    TextView btnPost;
    EditText edtWritePost;
    RecyclerView rcvImagePost;
    PhotoWritePostAdapter photoAdapter;
    ImageView imgPhoto, imgVideo;
    MxVideoPlayerWidget videoWritePost;
    View containerVideoWritePost;
    Toolbar toolbar;
    String pathVideo;
    VideoBottomSheetDialogFragment videoBottomSheet;
    private static final int TYPE_REQUEST_VIDEO = 2, TYPE_REQUEST_PHOTO = 1;
    boolean isOpenImagePicker, isOpenVideoPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        addControl();
        initToolBar();
        addEvent();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void addControl() {
        toolbar = findViewById(R.id.toolbar_write_post_activity);
        btnPost = findViewById(R.id.btn_post);
        edtWritePost = findViewById(R.id.edt_write_post);
        rcvImagePost = findViewById(R.id.rcv_list_img_write_post);
        imgPhoto = findViewById(R.id.ic_image_write_post);
        imgVideo = findViewById(R.id.ic_video_write_post);
        videoWritePost = findViewById(R.id.video_write_post);
        containerVideoWritePost = findViewById(R.id.container_video_write_post);
        containerVideoWritePost.setVisibility(View.GONE);
        rcvImagePost.setVisibility(View.GONE);

        //set adapter + recycle view
        photoAdapter = new PhotoWritePostAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rcvImagePost.setLayoutManager(layoutManager);
        rcvImagePost.setAdapter(photoAdapter);

        isOpenImagePicker=getIntent().getBooleanExtra("isOpenImagePicker",false);
        isOpenVideoPicker=getIntent().getBooleanExtra("isOpenVideoPicker",false);

        if (isOpenImagePicker){
            requestPermissionAndSelectMedia(TYPE_REQUEST_PHOTO);
        }
        if (isOpenVideoPicker){
            requestPermissionAndSelectMedia(TYPE_REQUEST_VIDEO);
        }
        if (!isOpenImagePicker&&!isOpenVideoPicker){
            edtWritePost.requestFocus();
        }
    }


    private void addEvent() {
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionAndSelectMedia(TYPE_REQUEST_PHOTO);
            }
        });
        imgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionAndSelectMedia(TYPE_REQUEST_VIDEO);
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = edtWritePost.getText().toString().trim();
                if (content.length() == 0 &&
                        rcvImagePost.getVisibility() == View.GONE &&
                        containerVideoWritePost.getVisibility() == View.GONE) {
                    Toast toast = Toast.makeText(WritePostActivity.this, "không có dữ liệu đăng !", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("content", content);
                returnIntent.putExtra("pathVideo", pathVideo);
                returnIntent.putParcelableArrayListExtra("listUriImage", (ArrayList<? extends Parcelable>) photoAdapter.getUriList());
                setResult(Activity.RESULT_OK, returnIntent);
                onBackPressed();
            }
        });
    }

    private void requestPermissionAndSelectMedia(final int requestType) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // request type 1 : Photo, type 2 : video
                if (requestType == TYPE_REQUEST_PHOTO) selectPhotoFromTedBottomPicker();
                else selectVideoFromBottomSheet();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(WritePostActivity.this, "Quyền bị từ chối\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Nếu bạn không cấp quyền, bạn sẽ không thể sử dụng dịch vụ\n\nLàm ơn vào mục [Cài đặt] > [Quyền]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void selectVideoFromBottomSheet() {

        videoBottomSheet = new VideoBottomSheetDialogFragment(
                new ItemVideoListener() {
                    @Override
                    public void onVideoSelected(String path, long size) {
                        if (size > 10000000) {
                            Toast.makeText(WritePostActivity.this, "File quá lớn (>10mb) !", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //remove bottom sheet after selected item
                        Fragment f1 = getSupportFragmentManager().findFragmentByTag("VIDEO-TAG");
                        getSupportFragmentManager().beginTransaction().remove(f1).commit();
                        if (rcvImagePost.getVisibility() == View.VISIBLE) {
                            photoAdapter.setUriList(null);
                            rcvImagePost.setVisibility(View.GONE);
                        }
                        containerVideoWritePost.setVisibility(View.VISIBLE);
                        try {
                            pathVideo = path;
                            videoWritePost.startPlay(path, MxVideoPlayer.SCREEN_LAYOUT_NORMAL, " ");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        videoBottomSheet.show(getSupportFragmentManager(), "VIDEO-TAG");
    }

    private void selectPhotoFromTedBottomPicker() {
        TedBottomPicker.with(this)
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Xong")
                .setEmptySelectionText("Chưa chọn ảnh nào")
                .setSelectMaxCount(4)
                .setSelectMaxCountErrorText("Không chọn quá 4 ảnh")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        // here is selected image uri list
                        if (uriList != null && !uriList.isEmpty()) {
                            photoAdapter.setUriList(uriList);
                            if (containerVideoWritePost.getVisibility() == View.VISIBLE) {
                                containerVideoWritePost.setVisibility(View.GONE);
                                pathVideo = null;
                            }
                            rcvImagePost.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        MxVideoPlayerWidget.releaseAllVideos();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (MxVideoPlayer.backPress()) return;
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VideoBottomSheetAdapter.REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {

            if (videoBottomSheet.isAdded())
                videoBottomSheet.dismiss();

            Uri videoUri = data.getData();
            Video video = getVideoEntityFromURI(videoUri);
            if (video.getSize() > 10000000) {
                Toast.makeText(WritePostActivity.this, "File quá lớn (>10mb) !", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rcvImagePost.getVisibility() == View.VISIBLE) {
                photoAdapter.setUriList(null);
                rcvImagePost.setVisibility(View.GONE);
            }
            containerVideoWritePost.setVisibility(View.VISIBLE);
            try {
                pathVideo = video.getPath();
                videoWritePost.startPlay(video.getPath(),
                        MxVideoPlayer.SCREEN_LAYOUT_NORMAL, " ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Video getVideoEntityFromURI(Uri contentURI) {
        long size = 0, duration = 0;
        String absolutePathOfImage = null;
        String thumbnail = null;
        int columnIndexData, columnThumb, columnSize, columnDuration;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        cursor.moveToFirst();

        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        columnThumb = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
        columnSize = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
        columnDuration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
        absolutePathOfImage = cursor.getString(columnIndexData);
        thumbnail = cursor.getString(columnThumb);
        size = cursor.getLong(columnSize);
        duration = cursor.getLong(columnDuration);
        cursor.close();

        return new Video(absolutePathOfImage, thumbnail, size, duration);
    }
}