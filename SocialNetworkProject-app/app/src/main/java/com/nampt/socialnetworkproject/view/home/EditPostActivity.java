package com.nampt.socialnetworkproject.view.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.postService.PostService;
import com.nampt.socialnetworkproject.api.postService.request.EditPostRequest;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;

import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPostActivity extends AppCompatActivity {
    TextView btnEditPost;
    EditText edtEditPost;
    View containerVideo, containerMedia;
    MxVideoPlayerWidget videoPost;
    ImageView img1, img2, img3, img4;
    String content;
    Toolbar toolbar;
    boolean hasMedia = false;
    int idPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        addControl();
        initToolBar();
        addEvent();
    }

    private void addControl() {
        toolbar = findViewById(R.id.toolbar_edit_post_activity);
        btnEditPost = findViewById(R.id.btn_save_edit_post);
        edtEditPost = findViewById(R.id.edt_edit_post);
        containerVideo = findViewById(R.id.container_video_edit_post);
        videoPost = findViewById(R.id.video_edit_post);
        containerMedia = findViewById(R.id.container_media_edit_post);
        img1 = findViewById(R.id.img_1_edit_post);
        img2 = findViewById(R.id.img_2_edit_post);
        img3 = findViewById(R.id.img_3_edit_post);
        img4 = findViewById(R.id.img_4_edit_post);
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


    private void addEvent() {
        idPost = getIntent().getIntExtra("idPost", -1);
        content = getIntent().getStringExtra("contentPost");
        String linkImg1 = getIntent().getStringExtra("img1");
        String linkImg2 = getIntent().getStringExtra("img2");
        String linkImg3 = getIntent().getStringExtra("img3");
        String linkImg4 = getIntent().getStringExtra("img4");
        String linkVideo = getIntent().getStringExtra("video");

        edtEditPost.setText(content);
        edtEditPost.requestFocus();
        btnEditPost.setClickable(false);
        if (linkImg1 != null) {
            Glide.with(this).load(Host.HOST + linkImg1).into(img1);
            hasMedia = true;
        }
        if (linkImg2 != null) {
            Glide.with(this).load(Host.HOST + linkImg2).into(img2);
            hasMedia = true;
        }
        if (linkImg3 != null) {
            Glide.with(this).load(Host.HOST + linkImg3).into(img3);
            hasMedia = true;
        }
        if (linkImg4 != null) {
            Glide.with(this).load(Host.HOST + linkImg4).into(img4);
            hasMedia = true;
        }
        if (linkVideo == null) {
            containerVideo.setVisibility(View.GONE);
        } else {
            hasMedia = true;
            containerVideo.setVisibility(View.VISIBLE);
            videoPost.startPlay(Host.HOST + linkVideo, MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
        }

        edtEditPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(content) || s.toString().trim().equals("")) {
                    btnEditPost.setEnabled(false);
                    btnEditPost.setTextColor(getResources().getColor(R.color.colorGreyThin));
                } else {
                    btnEditPost.setEnabled(true);
                    btnEditPost.setTextColor(getResources().getColor(R.color.colorBlueThin));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        containerMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(EditPostActivity.this, "Không thể sửa ảnh và video", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        btnEditPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newContent = edtEditPost.getText().toString().trim();
                String oldContent = content;
                if (validateContent(newContent, oldContent)) {
                    handleEditPost(newContent);
                }
            }
        });
    }

    private boolean validateContent(String newContent, String oldContent) {
        if (oldContent == null) oldContent = "";
        if (oldContent.equals(newContent)) {
            Toast toast = Toast.makeText(EditPostActivity.this, "Bài viết chưa thay đổi !", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        } else {
            if (newContent.length() != 0) {
                return true;
            } else {
                if (hasMedia) {
                    return true;
                } else {
                    Toast toast = Toast.makeText(EditPostActivity.this, "Văn bản không hợp lệ !", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return false;
                }
            }
        }
    }

    private void handleEditPost(String newContent) {
        PostService.service.editPost(DataLocalManager.getPrefUser().getAccessToken(), new EditPostRequest(idPost, newContent)).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().getCode() == 1000) {
                    Toast toast = Toast.makeText(EditPostActivity.this, "Sửa thành công !", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("contentEdited", edtEditPost.getText().toString());
                    returnIntent.putExtra("idPostEdited", idPost);
                    setResult(Activity.RESULT_OK, returnIntent);
                    onBackPressed();
                } else if (response.body().getCode() == 1004 | response.body().getCode() == 9996) {
                    Toast toast = Toast.makeText(EditPostActivity.this, "Văn bản không hợp lệ !", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (response.body().getCode() == 9992) {
                    Toast toast = Toast.makeText(EditPostActivity.this, "Bài viết đã bị xóa trước đó !", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (response.body().getCode() == 9993) {
                    Toast toast = Toast.makeText(EditPostActivity.this, "Sai token !", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast toast = Toast.makeText(EditPostActivity.this, "Lỗi kết nối , vui lòng thử lại sau", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MxVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (MxVideoPlayer.backPress()) return;
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }

}