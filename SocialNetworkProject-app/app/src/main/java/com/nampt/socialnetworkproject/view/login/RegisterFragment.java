package com.nampt.socialnetworkproject.view.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.nampt.socialnetworkproject.api.userService.UserService;
import com.nampt.socialnetworkproject.api.userService.request.RegisterRequest;
import com.nampt.socialnetworkproject.api.userService.response.LoginResponse;
import com.nampt.socialnetworkproject.util.WindowUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    LoginActivity loginActivity;
    Context mContext;
    TextView txtUndoLayoutLogin;
    TextInputLayout txtUsername, txtPhone, txtPassword, txtVerifyPassword;
    Button btnRegister;
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
        CardView layout_register = (CardView) inflater.inflate(R.layout.layout_register, null);
        addControl(layout_register);
        addEvent();
        return layout_register;
    }

    private void addControl(View v) {
        txtUndoLayoutLogin = v.findViewById(R.id.txtOpenLayoutLogin);
        txtUsername = v.findViewById(R.id.txt_username_register);
        txtPhone = v.findViewById(R.id.txt_input_phone_register);
        txtPassword = v.findViewById(R.id.txt_input_password_register);
        txtVerifyPassword = v.findViewById(R.id.txt_verify_password_register);
        btnRegister = v.findViewById(R.id.btnRegister);
        progressDialog = new ProgressDialog(loginActivity);
    }

    private void addEvent() {
        txtUndoLayoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.onMsgFromFragToMain("REGISTER_FRAGMENT");
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowUtil.getInstance(mContext).hideSoftKeyboard();
                if (!Validate.validateUsername(txtUsername)
                        | !Validate.validatePhoneNumber(txtPhone)
                        | !Validate.validatePasswordRegisterPane(txtPassword, txtVerifyPassword)) {
                    return;
                }
                handleSignUp();

            }
        });
    }

    private void handleSignUp() {
        final String username = txtUsername.getEditText().getText().toString().trim();
        String phoneNumber = txtPhone.getEditText().getText().toString().trim();
        String password = txtPassword.getEditText().getText().toString().trim();
        progressDialog.setMessage("Đang tải, vui lòng đợi...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        UserService.service.signup(new RegisterRequest(username, phoneNumber, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressDialog.hide();
                if (response.body().getCode() == 1000) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity)
                            .setTitle("Thông báo")
                            .setMessage("đăng ký thành công")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("Đăng nhập ngay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    loginActivity.onMsgFromFragToMain("REGISTER_FRAGMENT");
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    txtPassword.getEditText().setText(null);
                    txtPhone.getEditText().setText(null);
                    txtUsername.getEditText().setText(null);
                    txtVerifyPassword.getEditText().setText(null);

                } else if (response.body().getCode() == 9996) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity)
                            .setTitle("Thông báo")
                            .setMessage("Tài khoản đã tồn tại !")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if (response.body().getCode() == 9997) {
                    if (response.body().getMessage().equals("username invalid"))
                        txtUsername.setError("Sai định dạng");
                    if (response.body().getMessage().equals("phone invalid"))
                        txtPhone.setError("Sai định dạng");
                    if (response.body().getMessage().equals("username invalid"))
                        txtPassword.setError("Sai định dạng");
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
