package com.nampt.socialnetworkproject.view.more;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;

public class MyQrCodeActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imgQrCode, toolbarImg, icBack;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr_code);
        addControl();
        initToolbar();
        addEvent();
    }

    private void addEvent() {

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(DataLocalManager.getPrefUser().getId()+"",
                    BarcodeFormat.QR_CODE, 500, 500);
            imgQrCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addControl() {
        toolbar = findViewById(R.id.toolbar_my_qr_code_activity);
        imgQrCode = findViewById(R.id.img_my_qr_code_activity);
        toolbarTitle = findViewById(R.id.txt_toolbar_title_info_person_act);
        toolbarImg = findViewById(R.id.img_toolbar_info_person_act);
        icBack = findViewById(R.id.ic_back_info_person_activity);
    }


    private void initToolbar() {
        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (DataLocalManager.getPrefUser().getLinkAvatar() != null)
            Glide.with(this).load(Host.HOST + DataLocalManager.getPrefUser().getLinkAvatar())
                    .placeholder(R.drawable.unnamed)
                    .into(toolbarImg);
        toolbarTitle.setText(DataLocalManager.getPrefUser().getName());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

}