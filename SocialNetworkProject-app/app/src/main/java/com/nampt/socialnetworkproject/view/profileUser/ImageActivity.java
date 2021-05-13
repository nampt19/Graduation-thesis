package com.nampt.socialnetworkproject.view.profileUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.userService.UserService;
import com.nampt.socialnetworkproject.api.userService.response.AlbumResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rcvImage;
    ImageAlbumAdapter mAdapter;
    View layoutNoData, layoutLoading, layoutNoNetwork;
    List<String> urlImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        addControl();
        addEvent();
    }

    private void addEvent() {
        getListUrlImage();
    }

    private void getListUrlImage() {
        int userId = getIntent().getIntExtra("userId", 0);
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoNetwork.setVisibility(View.GONE);
        layoutNoData.setVisibility(View.GONE);
        UserService.service.getAlbumList(DataLocalManager.getPrefUser().getAccessToken(), userId).enqueue(new Callback<AlbumResponse>() {
            @Override
            public void onResponse(Call<AlbumResponse> call, Response<AlbumResponse> response) {
                layoutLoading.setVisibility(View.GONE);
                if (response.body().getCode() == 1000) {
                    urlImageList = response.body().getData();
                    if (urlImageList.isEmpty()) {
                        layoutNoData.setVisibility(View.VISIBLE);
                        return;
                    }
                    Collections.reverse(urlImageList);
                    mAdapter.setUrlImageList(urlImageList);

                } else if (response.body().getCode() == 9996) {
                    Toast t = Toast.makeText(ImageActivity.this, "Người dùng đã bị chặn ", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                } else if (response.body().getCode() == 9995) {
                    Toast t = Toast.makeText(ImageActivity.this, "Người dùng không tồn tại ", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                } else if (response.body().getCode() == 9993) {
                    Toast t = Toast.makeText(ImageActivity.this, "Sai token", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                }
            }

            @Override
            public void onFailure(Call<AlbumResponse> call, Throwable t) {
                layoutLoading.setVisibility(View.GONE);
                layoutNoNetwork.setVisibility(View.VISIBLE);
                layoutNoNetwork.findViewById(R.id.btn_reload_network_utils).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutNoNetwork.setVisibility(View.GONE);
                        getListUrlImage();
                    }
                });
            }
        });
    }

    private void addControl() {
        toolbar = findViewById(R.id.toolbar_image_activity);
        rcvImage = findViewById(R.id.rcv_image_activity);
        layoutNoData = findViewById(R.id.layout_no_data_image);
        layoutLoading = findViewById(R.id.layout_progress_loading_image);
        layoutNoNetwork = findViewById(R.id.layout_no_network_image);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Album ảnh");
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mAdapter = new ImageAlbumAdapter(this);
        mAdapter.setUrlImageList(urlImageList);
        rcvImage.setAdapter(mAdapter);
        rcvImage.setLayoutManager(layoutManager);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}