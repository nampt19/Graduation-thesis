package com.nampt.socialnetworkproject.view.more;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.model.Post;
import com.nampt.socialnetworkproject.view.login.LoginActivity;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.userService.UserService;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MoreFragment extends Fragment {
    private Context mContext;
    private RelativeLayout containerProfile;
    private ProgressDialog progressDialog;
    private View layoutInfoPerson, layoutChangePassword, layoutQrCode, layoutBlockList, layoutLogout;
    private ImageView imgAvatar;
    private TextView txtNameUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        Log.e("more1", "onCreate moreFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        addControl(view);
        addEvent();

        return view;
    }

    private void addControl(View view) {
        containerProfile = view.findViewById(R.id.layout_container_profile_user);
        imgAvatar = view.findViewById(R.id.img_user_more);
        txtNameUser = view.findViewById(R.id.txt_name_user_more);
        layoutLogout = view.findViewById(R.id.layout_logout);
        layoutInfoPerson = view.findViewById(R.id.item_info_person_more);
        layoutChangePassword = view.findViewById(R.id.item_change_password_person_more);
        layoutQrCode = view.findViewById(R.id.item_qr_person_more);
        layoutBlockList = view.findViewById(R.id.item_block_person_more);

        progressDialog = new ProgressDialog(mContext, R.style.myLoadingDialogStyle);
    }

    private void addEvent() {
        if (DataLocalManager.getPrefUser().getLinkAvatar() != null)
            Glide.with(this)
                    .load(Host.HOST + DataLocalManager.getPrefUser().getLinkAvatar())
                    .placeholder(R.drawable.null_bk)
                    .into(imgAvatar);

        txtNameUser.setText(DataLocalManager.getPrefUser().getName());

        containerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileUserActivity.class);
                intent.putExtra("userId", DataLocalManager.getPrefUser().getId());
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        layoutInfoPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InforPersonActivity.class);
                MoreFragment.this.startActivityForResult(intent, 1234);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        layoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChangePasswordActivity.class);
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            }
        });
        layoutQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyQrCodeActivity.class);
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

            }
        });
        layoutBlockList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BlockListActivity.class);
                startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });

    }

    private void handleLogout() {
        progressDialog.setMessage("Đang tải, vui lòng chờ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String token = DataLocalManager.getPrefUser().getAccessToken();
        UserService.service.logout(token).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progressDialog.hide();
                User userLogout = DataLocalManager.getPrefUser();
                userLogout.setAccessToken(null);
                userLogout.setOnline(false);
                DataLocalManager.setPrefUser(userLogout);

                List<Post> postsLocal = DataLocalManager.getPrefPostList();
                postsLocal.clear();
                DataLocalManager.setPrefListPost(postsLocal);

                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                ((Activity) mContext).finishAffinity();
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progressDialog.hide();
                Toast.makeText(mContext, "Lỗi mạng, vui lòng thử lại", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (progressDialog != null)
            progressDialog.cancel();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            String name = data.getStringExtra("username");
            txtNameUser.setText(name);
        }
    }
}