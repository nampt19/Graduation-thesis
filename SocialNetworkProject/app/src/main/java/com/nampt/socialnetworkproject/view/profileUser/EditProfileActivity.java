package com.nampt.socialnetworkproject.view.profileUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.userService.UserService;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txtSchool, txtAddress;
    ImageView btnEditSchool, btnEditAddress;
    Button btnSave;
    String address, school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        addControl();
        initToolbar();
        addEvent();
    }


    private void addControl() {
        toolbar = findViewById(R.id.toolbar_edit_profile_activity);
        txtSchool = findViewById(R.id.txt_school_edit_profile);
        txtAddress = findViewById(R.id.txt_address_edit_profile);
        btnEditSchool = findViewById(R.id.btn_edit_school_profile);
        btnEditAddress = findViewById(R.id.btn_edit_address_profile);
        btnSave = findViewById(R.id.btn_save_edit_profile);

        school = getIntent().getStringExtra("school");
        address = getIntent().getStringExtra("address");
        txtSchool.setText(school);
        txtAddress.setText(address);

        btnEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, PickerActivity.class);
                intent.putExtra("isSchool", false);
                startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        btnEditSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, PickerActivity.class);
                intent.putExtra("isSchool", true);
                startActivityForResult(intent, 101);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chỉnh sửa chi tiết");
        }
    }

    private void addEvent() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtAddress.getText().toString().trim().isEmpty() &&
                        txtSchool.getText().toString().trim().isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Chưa chọn thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (txtAddress.getText().equals(address) && txtSchool.getText().equals(school)) {
                    Toast.makeText(EditProfileActivity.this, "Thông tin không thay đổi", Toast.LENGTH_SHORT).show();
                } else {
                    handleSetProfile();
                }
            }
        });
    }

    private void handleSetProfile() {
        UserService.service.setProfile(DataLocalManager.getPrefUser().getAccessToken(),
                txtSchool.getText().toString().trim(),
                txtAddress.getText().toString().trim())
                .enqueue(new Callback<BaseResponse>() {

                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            Toast.makeText(EditProfileActivity.this, "Thành công !", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                        if (response.body().getCode() == 1004) {
                            Toast.makeText(EditProfileActivity.this, "Thông tin trống ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(EditProfileActivity.this, "Lỗi mạng, vui lòng thử lại !", Toast.LENGTH_SHORT).show();

                    }
                });
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                //edit address
                txtAddress.setText(data.getStringExtra("address"));
            } else if (requestCode == 101) {
                //edit school
                txtSchool.setText(data.getStringExtra("school"));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}