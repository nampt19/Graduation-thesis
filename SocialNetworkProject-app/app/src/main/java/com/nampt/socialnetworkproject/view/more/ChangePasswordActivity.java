package com.nampt.socialnetworkproject.view.more;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.userService.UserService;
import com.nampt.socialnetworkproject.api.userService.response.ListPeopleResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.view.login.Validate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextInputLayout txtLayoutOldPass, txtLayoutNewPass, txtLayoutVerifyPass;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        addControl();
        initToolbar();
        addEvent();
    }

    private void addEvent() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePasswordField(txtLayoutOldPass) &&
                        validatePasswordField(txtLayoutNewPass) &&
                        validatePasswordField(txtLayoutVerifyPass)) {
                    if (!validatePasswordMatch(txtLayoutOldPass, txtLayoutNewPass, txtLayoutVerifyPass)) {
                        return;
                    } else {
                        handleChangePassword();
                    }
                } else return;

            }
        });
    }

    private void handleChangePassword() {
        UserService.service.changePassword(DataLocalManager.getPrefUser().getAccessToken(),
                txtLayoutNewPass.getEditText().getText().toString().trim(),
                txtLayoutOldPass.getEditText().getText().toString().trim())
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else if (response.body().getCode() == 9995) {
                            Toast.makeText(ChangePasswordActivity.this, "Mật khẩu không đổi", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getCode() == 9996) {
                            txtLayoutOldPass.setError("Sai mật khẩu");
                        } else if (response.body().getCode() == 9997) {
                            Toast.makeText(ChangePasswordActivity.this, "Sai định dạng mật khẩu", Toast.LENGTH_SHORT).show();

                        } else if (response.body().getCode() == 9993) {
                            Toast.makeText(ChangePasswordActivity.this, "Sai token !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối !", Toast.LENGTH_SHORT).show();

                    }
                });
        {

        }
    }

    private void addControl() {
        toolbar = findViewById(R.id.toolbar_change_password_activity);
        txtLayoutOldPass = findViewById(R.id.txt_layout_old_password_change);
        txtLayoutNewPass = findViewById(R.id.txt_layout_new_password_change);
        txtLayoutVerifyPass = findViewById(R.id.txt_layout_verify_password_change);
        btnSave = findViewById(R.id.btn_verify_change_password);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Đổi mật khẩu");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private boolean validatePasswordField(TextInputLayout txtPass) {
        String strPass = txtPass.getEditText().getText().toString().trim();
        if (strPass.isEmpty()) {
            txtPass.setError(Validate.INPUT_EMPTY);
            return false;
        } else {
            if (!strPass.matches(Validate.PASSWORD_REGEX)) {
                txtPass.setError("sai định dạng");
                return false;
            } else {
                txtPass.setError(null);
                return true;
            }
        }
    }

    private boolean validatePasswordMatch(TextInputLayout txtOldPass,
                                          TextInputLayout txtNewPass,
                                          TextInputLayout txtVerifyPass) {
        String strOldPass = txtOldPass.getEditText().getText().toString().trim();
        String strNewPass = txtNewPass.getEditText().getText().toString().trim();
        String strVerifyPass = txtVerifyPass.getEditText().getText().toString().trim();
        if (strOldPass.equals(strNewPass)) {
            txtNewPass.setError("Mật khẩu mới chưa thay đổi");
            return false;
        } else {
            if (strNewPass.equals(strVerifyPass)) {
                return true;
            } else {
                txtVerifyPass.setError("mật khẩu xác nhận không khớp");
                return false;
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}