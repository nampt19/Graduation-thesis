package com.nampt.socialnetworkproject.view.home;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.userService.UserService;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        addControl();
        addEvent();
    }

    private void addControl() {
        scannerView = findViewById(R.id.scanner_view);
        ProgressDialog progressDialog = new ProgressDialog(this, R.style.myLoadingDialogStyle);
    }

    private void addEvent() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //handleResult
                scannerView.setResultHandler(ScanActivity.this);
                scannerView.startCamera();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(ScanActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("N???u b???n kh??ng c???p quy???n, b???n s??? kh??ng th??? s??? d???ng d???ch v???\n\nL??m ??n v??o m???c [C??i ?????t] > [Quy???n]")
                .setPermissions(Manifest.permission.CAMERA)
                .check();
    }

    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result result) {

        int userId;
        try {
            userId = Integer.parseInt(result.getText());
            checkUserExist(userId);
        } catch (Exception e) {
            Toast.makeText(this, "D??? li???u kh??ng h???p l??? !", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void checkUserExist(int userId) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        UserService.service.getBlock(DataLocalManager.getPrefUser().getAccessToken(), userId)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (response.body().getCode() == 1000) {
                            if (response.body().getMessage().equals("false")) {
                                Intent intent = new Intent(ScanActivity.this, ProfileUserActivity.class);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                                ScanActivity.this.finish();
                            } else {
                                Toast.makeText(ScanActivity.this, "Kh??ng t??m th???y ng?????i d??ng", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        } else {
                            Toast.makeText(ScanActivity.this, "Kh??ng t??m th???y ng?????i d??ng", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(ScanActivity.this, "Kh??ng t??m th???y ng?????i d??ng", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}