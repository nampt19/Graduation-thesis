package com.nampt.socialnetworkproject.view.more;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.userService.UserService;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.User;
import com.nampt.socialnetworkproject.view.login.Validate;
import com.nampt.socialnetworkproject.view.search.GroupChatSearchFragment;
import com.nampt.socialnetworkproject.view.search.PeopleSearchFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InforPersonActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imgBanner, imgAvatar, btnEdit;
    TextView txtNameTitle, txtNameBody, txtPhone;
    EditText edtNameBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_person);
        addControl();
        initToolbar();
        addEvent();
    }


    private void addControl() {
        toolbar = findViewById(R.id.toolbar_info_person_activity);

        imgAvatar = findViewById(R.id.img_avatar_info_person_activity);
        imgBanner = findViewById(R.id.img_banner_info_person_activity);
        btnEdit = findViewById(R.id.btn_edit_name_info_person_activity);
        txtNameTitle = findViewById(R.id.txt_name_user_title_info_person_activity);
        txtNameBody = findViewById(R.id.txt_name_user_body_info_person_activity);
        edtNameBody = findViewById(R.id.edt_name_user_body_info_person_activity);
        txtPhone = findViewById(R.id.txt_phone_body_info_person_activity);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Thông tin cá nhân");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void addEvent() {
        if (DataLocalManager.getPrefUser().getLinkBanner() != null)
            Glide.with(this)
                    .load(Host.HOST + DataLocalManager.getPrefUser().getLinkBanner())
                    .placeholder(R.drawable.null_bk)
                    .into(imgBanner);
        if (DataLocalManager.getPrefUser().getLinkAvatar() != null)
            Glide.with(this)
                    .load(Host.HOST + DataLocalManager.getPrefUser().getLinkAvatar())
                    .placeholder(R.drawable.null_bk)
                    .into(imgAvatar);
        txtNameTitle.setText(DataLocalManager.getPrefUser().getName());
        txtNameBody.setText(DataLocalManager.getPrefUser().getName());
        txtPhone.setText(DataLocalManager.getPrefUser().getPhone());
        btnEdit.setOnClickListener(v -> {
            txtNameBody.setVisibility(View.GONE);
            edtNameBody.setVisibility(View.VISIBLE);
            edtNameBody.setText(txtNameBody.getText());
            edtNameBody.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) InforPersonActivity.this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        });

        edtNameBody.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String strValue = edtNameBody.getText().toString().trim();
                if (strValue.equals(DataLocalManager.getPrefUser().getName())) {
                    Toast t = Toast.makeText(InforPersonActivity.this, "Tên chưa thay đổi", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                } else if (!strValue.matches(Validate.USERNAME_REGEX)) {
                    Toast t = Toast.makeText(InforPersonActivity.this, "Tên phải từ 3 - 50 kí tự, không kí tự đặc biệt !", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                } else {
                    setName(strValue);
                }
            }
            return false;
        });
    }

    private void setName(String strValue) {
        UserService.service.setName(DataLocalManager.getPrefUser().getAccessToken(), strValue)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            Toast t = Toast.makeText(InforPersonActivity.this, "Đổi tên thành công", Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.CENTER, 0, 0);
                            t.show();
                            edtNameBody.setVisibility(View.GONE);
                            txtNameBody.setVisibility(View.VISIBLE);
                            txtNameBody.setText(strValue);
                            txtNameTitle.setText(strValue);

                            User userLocal = DataLocalManager.getPrefUser();
                            userLocal.setName(strValue);
                            DataLocalManager.setPrefUser(userLocal);

                        } else if (response.body().getCode() == 1004) {
                            Toast t = Toast.makeText(InforPersonActivity.this, "Tên không hợp lệ", Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.CENTER, 0, 0);
                            t.show();
                        } else if (response.body().getCode() == 9993) {
                            Toast t = Toast.makeText(InforPersonActivity.this, "Sai token", Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.CENTER, 0, 0);
                            t.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast tt = Toast.makeText(InforPersonActivity.this, "Lỗi kết nối !", Toast.LENGTH_SHORT);
                        tt.setGravity(Gravity.CENTER, 0, 0);
                        tt.show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("username", DataLocalManager.getPrefUser().getName());
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }
}