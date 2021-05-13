package com.nampt.socialnetworkproject.view.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.view.home.HomeActivity;
import com.nampt.socialnetworkproject.api.userService.UserService;
import com.nampt.socialnetworkproject.api.userService.request.LoginRequest;
import com.nampt.socialnetworkproject.api.userService.response.LoginResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.User;
import com.nampt.socialnetworkproject.util.WindowUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    TextInputLayout txtPhone, txtPassword;
    TextView txtOpenLayoutRegister;
    Button btnLogin;
    LoginActivity loginActivity;
    Context mContext;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof ILoginCallBacks)) {
            throw new IllegalStateException(" Activity must implement MainCallbacks");
        }
        loginActivity = (LoginActivity) getActivity();
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CardView layout_login = (CardView) inflater.inflate(R.layout.layout_login, null);

        addControl(layout_login);
        addEvent();

        return layout_login;
    }

    private void addControl(CardView layout_login) {
        txtOpenLayoutRegister = layout_login.findViewById(R.id.txtOpenLayoutRegister);
        btnLogin = layout_login.findViewById(R.id.btnLogin);
        txtPhone = layout_login.findViewById(R.id.txt_input_phone_login);
        txtPassword = layout_login.findViewById(R.id.txt_input_password_login);
        progressDialog = new ProgressDialog(loginActivity, R.style.myLoadingDialogStyle);
    }

    private void addEvent() {
        txtOpenLayoutRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.onMsgFromFragToMain("LOGIN_FRAGMENT");
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowUtil.getInstance(mContext).hideSoftKeyboard();
                if (!Validate.validatePhoneNumber(txtPhone) | !Validate.validatePasswordLoginPane(txtPassword)) {
                    return;
                }
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String phoneNumber = txtPhone.getEditText().getText().toString().trim();
        String password = txtPassword.getEditText().getText().toString().trim();
        progressDialog.setMessage("Đang tải, vui lòng đợi...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        UserService.service.login(new LoginRequest(phoneNumber, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        progressDialog.hide();
                        if (response.body().getCode() == 1000) {
                            // save info user into sharePreference
                            User data = response.body().getData();
                            User user = new User(data.getId(),
                                    data.getName(),
                                    data.getLinkAvatar(),
                                    data.getLinkBanner(),
                                    data.getAddress(),
                                    data.getSchool(),
                                    data.getAccessToken(),
                                    data.getOnline(),
                                    data.getPhone());
                            DataLocalManager.setPrefUser(user);
                            Log.e("user-pref-saved", DataLocalManager.getPrefUser().toString());

                            progressDialog.cancel();
                            Intent intent = new Intent(loginActivity, HomeActivity.class);
                            startActivity(intent);
                            loginActivity.finishAffinity();

                        } else if (response.body().getCode() == 9992) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity)
                                    .setTitle("Thông báo")
                                    .setMessage("Tài khoản không tồn tại !")
                                    .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else if (response.body().getCode() == 9997) {
                            txtPassword.setError("Sai mật khẩu");
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        progressDialog.hide();
                        Toast toast = Toast.makeText(loginActivity, "Lỗi mạng, vui lòng thử lại ", Toast.LENGTH_LONG);
                        toast.show();
                        Log.e("error", t.getMessage());
                    }
                });
    }


    @Override
    public void onDestroy() {
        if (progressDialog != null)
            progressDialog.cancel();
        super.onDestroy();
    }
}
