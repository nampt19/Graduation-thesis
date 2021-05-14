package com.nampt.socialnetworkproject.view.profileUser;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.friendService.FriendService;
import com.nampt.socialnetworkproject.api.notifyService.NotifyServiceImpl;
import com.nampt.socialnetworkproject.api.postService.PostService;
import com.nampt.socialnetworkproject.api.postService.response.ListPostResponse;
import com.nampt.socialnetworkproject.api.postService.response.data.DataSinglePost;
import com.nampt.socialnetworkproject.api.userService.UserService;
import com.nampt.socialnetworkproject.api.userService.response.PeopleResponse;
import com.nampt.socialnetworkproject.api.userService.response.ProfileUserResponse;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.Post;
import com.nampt.socialnetworkproject.model.User;
import com.nampt.socialnetworkproject.util.WindowUtil;

import com.nampt.socialnetworkproject.view.message.MessageActivity;
import com.nampt.socialnetworkproject.view.notification.NotificationActivity;
import com.nampt.socialnetworkproject.view.search.SearchActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import hb.xvideoplayer.MxVideoPlayerWidget;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUserActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView imgBack, imgBanner, imgAvatar, imgThinkPostAvatar;
    TextView txtUsername, txtAddress, txtSchool, txt_search_profile_user;
    Button btnSendRequest, btnEditProfile, btnImage;
    ImageButton btnChat, btnMore;
    View layoutNoData, layoutLoading, layoutNoNetwork,
            layoutAddress, layoutSchool, layoutBodyProfile,
            layoutBodyProfile2, layoutBodyProfile3, layoutAll, layoutSentRequest, layoutEditProfile;
    RecyclerView rcvPost;
    PostProfileAdapter mPostAdapter;
    List<Post> mListPost = new ArrayList<>();
    User profileUser;
    int userId, type = 0;
    private static int TYPE_AVATAR = 1, TYPE_BANNER = 2;
    BottomSheetDialog bottomSheetMore, bottomSheetIsFriend, bottomSheetReplyRequestFriend;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        addControl();
        initRcvPost();
        addEvent();
    }

    private void addControl() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_profile);
        imgBack = findViewById(R.id.ic_back_profile_activity);
        imgBanner = findViewById(R.id.img_banner_profile);
        imgAvatar = findViewById(R.id.img_avatar_profile);
        imgThinkPostAvatar = findViewById(R.id.img_user_avatar_profile);
        btnImage = findViewById(R.id.btn_image_album);
        txt_search_profile_user = findViewById(R.id.txt_search_profile_user);

        txtUsername = findViewById(R.id.txt_name_user_profile);
        txtAddress = findViewById(R.id.txt_address_profile);
        txtSchool = findViewById(R.id.txt_school_profile);
        btnSendRequest = findViewById(R.id.btn_send_request_profile);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChat = findViewById(R.id.btn_chat_profile);
        btnMore = findViewById(R.id.btn_horiz_profile);
        rcvPost = findViewById(R.id.rcv_post_profile);
        layoutAddress = findViewById(R.id.layout_address_profile);
        layoutSchool = findViewById(R.id.layout_school_profile);
        layoutBodyProfile = findViewById(R.id.layout_body_profile);
        layoutBodyProfile2 = findViewById(R.id.layout_body_profile_2);
        layoutBodyProfile3 = findViewById(R.id.layout_container_think);
        layoutAll = findViewById(R.id.layout_container_all_profile_user);

        layoutSentRequest = findViewById(R.id.layout_container_sent_request);
        layoutEditProfile = findViewById(R.id.layout_edit_profile);

        layoutNoData = findViewById(R.id.layout_no_data_profile);
        layoutLoading = findViewById(R.id.layout_progress_loading_profile);
        layoutNoNetwork = findViewById(R.id.layout_no_network_profile);
        layoutNoData.setBackgroundColor(getResources().getColor(R.color.whiteCardColor));
        layoutLoading.setBackgroundColor(getResources().getColor(R.color.whiteCardColor));
        layoutNoNetwork.setBackgroundColor(getResources().getColor(R.color.whiteCardColor));

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueThin));
        swipeRefreshLayout.setOnRefreshListener(this);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userId = getIntent().getIntExtra("userId", 0);
        getTypeFriendBySeeker();
    }

    private void getTypeFriendBySeeker() {
        UserService.service.getType(DataLocalManager.getPrefUser().getAccessToken(), userId)
                .enqueue(new Callback<PeopleResponse>() {
                    @Override
                    public void onResponse(Call<PeopleResponse> call, Response<PeopleResponse> response) {
                        if (response.body().getCode() == 1000) {
                            type = response.body().getData().getType();
                        }
                        setThemeForSendRequest(type);
                    }

                    @Override
                    public void onFailure(Call<PeopleResponse> call, Throwable t) {
                        getTypeFriendBySeeker();
                    }
                });

    }

    private void initRcvPost() {
        mPostAdapter = new PostProfileAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPostAdapter.setPostList(mListPost);
        rcvPost.setLayoutManager(layoutManager);
        rcvPost.setAdapter(mPostAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addEvent() {
        txt_search_profile_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileUserActivity.this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileUserActivity.this, ImageActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        if (DataLocalManager.getPrefUser().getLinkAvatar() != null) {
            Glide.with(this)
                    .load(Host.HOST + DataLocalManager.getPrefUser().getLinkAvatar())
                    .placeholder(R.drawable.unnamed).into(imgThinkPostAvatar);
        }
        if (userId == DataLocalManager.getPrefUser().getId()) {
            layoutSentRequest.setVisibility(View.GONE);
            layoutEditProfile.setVisibility(View.VISIBLE);
            layoutBodyProfile3.setVisibility(View.VISIBLE);
            imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermissionAndSelectMedia(TYPE_AVATAR);
                }
            });
            imgBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermissionAndSelectMedia(TYPE_BANNER);
                }
            });
        } else {
            layoutSentRequest.setVisibility(View.VISIBLE);
            layoutEditProfile.setVisibility(View.GONE);
            layoutBodyProfile3.setVisibility(View.GONE);
        }
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileUserActivity.this, EditProfileActivity.class);
                String strSchool,strAddress;

                if (layoutAddress.getVisibility()==View.GONE){
                    strAddress = "";
                }else {
                    strAddress = txtAddress.getText().toString().trim();
                }
                if (layoutSchool.getVisibility()==View.GONE){
                    strSchool = "";
                }else {
                    strSchool = txtSchool.getText().toString().trim();
                }
                intent.putExtra("school", strSchool);
                intent.putExtra("address", strAddress);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
            }
        });

        setDataProfile();
        setFirstDataPost();
        // setThemeForSendRequest(type);
        handleAllRequests();
    }

    private void handleAllRequests() {
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileUserActivity.this, MessageActivity.class);
                intent.putExtra("partnerId", userId);
                intent.putExtra("roomName", txtUsername.getText().toString());
                intent.putExtra("isRoom", false);
                startActivity(intent);
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View viewDialogMe = ProfileUserActivity.this.getLayoutInflater().inflate(R.layout.layout_bottom_sheet_more_profile, null);
                bottomSheetMore = new BottomSheetDialog(ProfileUserActivity.this);
                bottomSheetMore.setContentView(viewDialogMe);
                bottomSheetMore.show();
                viewDialogMe.findViewById(R.id.block_user_profile)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleBlockUser();
                            }
                        });
            }
        });
        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                switch (type) {
                    case 0:
                        handleSetRequestFriend();
                        break;
                    case 1:
                        View viewDialogMe = ProfileUserActivity.this.getLayoutInflater().inflate(R.layout.layout_bottom_sheet_is_friend, null);
                        bottomSheetIsFriend = new BottomSheetDialog(ProfileUserActivity.this);
                        bottomSheetIsFriend.setContentView(viewDialogMe);
                        bottomSheetIsFriend.show();
                        viewDialogMe.findViewById(R.id.item_delete_friend_user_profile)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        handleDeleteFriend();
                                    }
                                });
                        break;
                    case 2:
                        handleDeleteSentInvitation();
                        break;
                    case 3:

                        View viewDialog = ProfileUserActivity.this.getLayoutInflater().inflate(R.layout.layout_bottom_sheet_reply_request_friend, null);
                        bottomSheetReplyRequestFriend = new BottomSheetDialog(ProfileUserActivity.this);
                        bottomSheetReplyRequestFriend.setContentView(viewDialog);
                        bottomSheetReplyRequestFriend.show();

                        viewDialog.findViewById(R.id.item_accept_request)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        handleSetAcceptFriend(1);
                                    }
                                });
                        viewDialog.findViewById(R.id.item_remove_request)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        handleSetAcceptFriend(0);
                                    }
                                });
                        break;
                }
            }
        });

    }

    private void handleSetAcceptFriend(final int isAccept) {
        FriendService.service.setAcceptFriend(DataLocalManager.getPrefUser().getAccessToken(), userId, isAccept).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().getCode() == 1000) {
                    bottomSheetReplyRequestFriend.hide();

                    if (isAccept != 0) {
                        type = 1;
                        setThemeForSendRequest(type);
                        Toast.makeText(ProfileUserActivity.this, "Thêm bạn thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        type = 0;
                        setThemeForSendRequest(type);
                        Toast.makeText(ProfileUserActivity.this, "Xóa yêu cầu thành công", Toast.LENGTH_SHORT).show();
                    }
                } else if (response.body().getCode() == 9996) {
                    Toast.makeText(ProfileUserActivity.this, "Người dùng đã bị chặn", Toast.LENGTH_SHORT).show();
                } else if (response.body().getCode() == 9994) {
                    Toast.makeText(ProfileUserActivity.this, "2 người đã là bạn rồi", Toast.LENGTH_SHORT).show();
                } else if (response.body().getCode() == 9992) {
                    Toast.makeText(ProfileUserActivity.this, "Yêu cầu đã bị xóa trước đó", Toast.LENGTH_SHORT).show();
                } else if (response.body().getCode() == 9993) {
                    Toast.makeText(ProfileUserActivity.this, "Sai token !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(ProfileUserActivity.this, "Lỗi kết nối, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleDeleteFriend() {
        FriendService.service.deleteFriend(DataLocalManager.getPrefUser().getAccessToken(), userId)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            Toast.makeText(ProfileUserActivity.this, "Xóa bạn thành công", Toast.LENGTH_SHORT).show();
                            bottomSheetIsFriend.hide();
                            type = 0;
                            setThemeForSendRequest(type);
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(ProfileUserActivity.this, "Lỗi kết nối !", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleDeleteSentInvitation() {
        FriendService.service.deleteInvitationSentFriend(DataLocalManager.getPrefUser().getAccessToken(), userId)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            type = 0;
                            setThemeForSendRequest(type);
                            Toast.makeText(ProfileUserActivity.this, "Thu hồi lời mời thành công", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getCode() == 9992) {
                            Toast.makeText(ProfileUserActivity.this, "Yêu cầu đã bị xóa trước đó", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getCode() == 9993) {
                            Toast.makeText(ProfileUserActivity.this, "Sai token !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(ProfileUserActivity.this, "Lỗi kết nối !", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleSetRequestFriend() {
        FriendService.service.setRequestFriend(DataLocalManager.getPrefUser().getAccessToken(), userId)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            type += 2;
                            setThemeForSendRequest(type);
                            Toast.makeText(ProfileUserActivity.this, "Gửi lời mời thành công", Toast.LENGTH_SHORT).show();

                            NotifyServiceImpl.getInstance().setNotify(NotificationActivity.TYPE_NOTIFY_FRIEND_REQUEST,
                                    userId, 0,null);

                        } else if (response.body().getCode() == 9996) {
                            Toast.makeText(ProfileUserActivity.this, "Người dùng đã bị chặn", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getCode() == 9995) {
                            Toast.makeText(ProfileUserActivity.this, "Yêu cầu đã được gửi", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getCode() == 9994) {
                            Toast.makeText(ProfileUserActivity.this, "2 người đã là bạn ", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getCode() == 9993) {
                            Toast.makeText(ProfileUserActivity.this, "Sai token !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(ProfileUserActivity.this, "Lỗi kết nối !", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setDataProfile() {
        layoutBodyProfile.setVisibility(View.GONE);
        layoutBodyProfile2.setVisibility(View.GONE);
        layoutBodyProfile3.setVisibility(View.GONE);
        UserService.service.getProfile(DataLocalManager.getPrefUser().getAccessToken(), userId)
                .enqueue(new Callback<ProfileUserResponse>() {
                    @Override
                    public void onResponse(Call<ProfileUserResponse> call, Response<ProfileUserResponse> response) {
                        if (response.body().getCode() == 1000) {
                            if (response.body().isBlock()) {
                                layoutAll.setVisibility(View.GONE);

                                Toast t = Toast.makeText(ProfileUserActivity.this, "Người dùng đã bị chặn", Toast.LENGTH_LONG);
                                t.setGravity(Gravity.CENTER, 0, 0);
                                t.show();

                                return;
                            }

                            layoutAll.setVisibility(View.VISIBLE);
                            layoutBodyProfile.setVisibility(View.VISIBLE);
                            layoutBodyProfile2.setVisibility(View.VISIBLE);

                            if (userId == DataLocalManager.getPrefUser().getId())
                                layoutBodyProfile3.setVisibility(View.VISIBLE);
                            else {
                                layoutBodyProfile3.setVisibility(View.GONE);
                            }
                            User data = response.body().getData();
                            profileUser = new User(data.getId(),
                                    data.getName(),
                                    data.getLinkAvatar(),
                                    data.getLinkBanner(),
                                    data.getAddress(),
                                    data.getSchool(),
                                    data.getAccessToken(),
                                    data.getOnline(),
                                    data.getPhone());
                            if (profileUser.getLinkAvatar() != null)
                                Glide.with(ProfileUserActivity.this)
                                        .load(Host.HOST + profileUser.getLinkAvatar())
                                        .placeholder(R.drawable.null_bk)
                                        .into(imgAvatar);
                            if (profileUser.getLinkBanner() != null)
                                Glide.with(ProfileUserActivity.this)
                                        .load(Host.HOST + profileUser.getLinkBanner())
                                        .placeholder(R.drawable.null_bk)
                                        .into(imgBanner);
                            txtUsername.setText(profileUser.getName());

                            if (profileUser.getSchool() != null) {
                                txtSchool.setText(profileUser.getSchool());
                                layoutSchool.setVisibility(View.VISIBLE);
                            } else layoutSchool.setVisibility(View.GONE);

                            if (profileUser.getAddress() != null) {
                                txtAddress.setText(profileUser.getAddress());
                                layoutAddress.setVisibility(View.VISIBLE);
                            } else layoutAddress.setVisibility(View.GONE);

                            if (userId == DataLocalManager.getPrefUser().getId()) {
                                User user = DataLocalManager.getPrefUser();
                                user.setLinkAvatar(profileUser.getLinkAvatar());
                                user.setLinkBanner(profileUser.getLinkBanner());
                                DataLocalManager.setPrefUser(user);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileUserResponse> call, Throwable t) {
                        Log.e("error", t.getMessage());
                    }
                });
    }

    private void setFirstDataPost() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoNetwork.setVisibility(View.GONE);
        layoutNoData.setVisibility(View.GONE);
        PostService.service
                .getListPostByPosterId(DataLocalManager.getPrefUser().getAccessToken(), userId)
                .enqueue(new Callback<ListPostResponse>() {
                    @Override
                    public void onResponse(Call<ListPostResponse> call, Response<ListPostResponse> response) {
                        layoutLoading.setVisibility(View.GONE);
                        if (response.body().getCode() == 1000) {
                            List<DataSinglePost> singlePostList = response.body().getData();

                            mListPost = getListPost(singlePostList);
                            if (mListPost.isEmpty()) {
                                layoutNoData.setVisibility(View.VISIBLE);
                                return;
                            }
                            mPostAdapter.setPostList(mListPost);

                        } else if (response.body().getCode() == 9993) {
                            // quay về login, xóa sharePrefUser , sai cmnl token rồi
                        }
                    }

                    @Override
                    public void onFailure(Call<ListPostResponse> call, Throwable t) {
                        layoutLoading.setVisibility(View.GONE);
                        layoutNoNetwork.setVisibility(View.VISIBLE);
                        layoutNoNetwork.findViewById(R.id.btn_reload_network_utils).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutNoNetwork.setVisibility(View.GONE);
                                setFirstDataPost();
                                setDataProfile();
                            }
                        });
                    }
                });
    }


    private void handleBlockUser() {
        FriendService.service.block(DataLocalManager.getPrefUser().getAccessToken(), userId)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            Toast.makeText(ProfileUserActivity.this, "Chặn thành công", Toast.LENGTH_SHORT).show();
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("idUserBlock", userId);
                            returnIntent.putExtra("isUserBlockFromMessageActivity",true);
                            setResult(Activity.RESULT_CANCELED, returnIntent);
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(ProfileUserActivity.this, "Lỗi kết nối !", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setThemeForSendRequest(int type) {
        switch (type) {
            case 0:
                btnSendRequest.setText("Thêm bạn bè");
                btnSendRequest.setTextColor(getResources().getColor(R.color.colorBlack));
                btnSendRequest.setBackground(getResources().getDrawable(R.drawable.ripple_effect_click_button_common));
                btnSendRequest.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_person_add_24, 0, 0, 0);
                btnSendRequest.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                break;
            case 1:
                btnSendRequest.setText("Bạn bè");
                btnSendRequest.setTextColor(getResources().getColor(R.color.whiteCardColor));
                btnSendRequest.setBackground(getResources().getDrawable(R.drawable.ripple_effect_button_blue));
                btnSendRequest.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_is_friend_24dp, 0, 0, 0);
                btnSendRequest.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.whiteCardColor)));
                break;
            case 2:
                btnSendRequest.setText("Đã gửi yêu cầu");
                btnSendRequest.setTextColor(getResources().getColor(R.color.colorBlack));
                btnSendRequest.setBackground(getResources().getDrawable(R.drawable.ripple_effect_button_blue_super_thin));
                btnSendRequest.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_person_arrow_right_24, 0, 0, 0);
                btnSendRequest.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                break;
            case 3:
                btnSendRequest.setText("Trả lời");
                btnSendRequest.setTextColor(getResources().getColor(R.color.colorBlueThin));
                btnSendRequest.setBackground(getResources().getDrawable(R.drawable.ripple_effect_button_blue_super_thin));
                btnSendRequest.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_reply_24, 0, 0, 0);
                btnSendRequest.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlueThin)));
                break;
        }
    }

    private void requestPermissionAndSelectMedia(final int requestType) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                selectPhotoFromTedBottomPicker(requestType);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(ProfileUserActivity.this, "Quyền bị từ chối\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Nếu bạn không cấp quyền, bạn sẽ không thể sử dụng dịch vụ\n\nLàm ơn vào mục [Cài đặt] > [Quyền]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void selectPhotoFromTedBottomPicker(final int requestType) {
        TedBottomPicker.with(this)
                .setPeekHeight(WindowUtil.getInstance(this).getWindowHeight())
                .setTitle("Cập nhật ảnh")
                .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        // here is selected image uri
                        if (uri != null) {
                            handleUploadImage(requestType, uri);
                        } else {
                            Toast.makeText(ProfileUserActivity.this, "Kích thứơc ảnh quá lớn !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleUploadImage(int requestType, Uri uri) {
        File file = new File(uri.getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        if (requestType == TYPE_AVATAR) {
            UserService.service
                    .setAvatar(DataLocalManager.getPrefUser().getAccessToken(), part)
                    .enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            if (response.body().getCode() == 1000) {
                                Glide.with(ProfileUserActivity.this)
                                        .load(Host.HOST + response.body().getMessage())
                                        .placeholder(R.drawable.null_bk)
                                        .into(imgAvatar);
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                            Toast.makeText(ProfileUserActivity.this, "Lỗi kết nối, vui lòng thử lại !", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            UserService.service
                    .setBanner(DataLocalManager.getPrefUser().getAccessToken(), part)
                    .enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            if (response.body().getCode() == 1000) {
                                Glide.with(ProfileUserActivity.this)
                                        .load(Host.HOST + response.body().getMessage())
                                        .placeholder(R.drawable.null_bk)
                                        .into(imgBanner);
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                            Toast.makeText(ProfileUserActivity.this, "Lỗi kết nối, vui lòng thử lại !", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private List<Post> getListPost(List<DataSinglePost> data) {
        List<Post> postList = new ArrayList<>();
        for (DataSinglePost post : data) {
            if (!post.isBlock() && !post.isHidden()) {
                Post post2 = new Post();
                post2.setId(post.getPost().getId());
                post2.setContent(post.getPost().getContent());
                post2.setLinkImage1(post.getPost().getLinkImage1());
                post2.setLinkImage2(post.getPost().getLinkImage2());
                post2.setLinkImage3(post.getPost().getLinkImage3());
                post2.setLinkImage4(post.getPost().getLinkImage4());
                post2.setLinkVideo(post.getPost().getLinkVideo());
                post2.setCreateTime(post.getPost().getCreateTime());
                post2.setTotalComment(post.getTotalComment());
                post2.setTotalLike(post.getTotalLike());
                post2.setLiked(post.isLiked());
                post2.setHidden(post.isHidden());
                post2.setBlock(post.isBlock());
                post2.setAuthorId(post.getAuthor().getId());
                post2.setAuthorName(post.getAuthor().getName());
                post2.setLinkAvatar(post.getAuthor().getLinkAvatar());
                postList.add(post2);
            }
        }
        return postList;
    }

    @Override
    public void onPause() {
        MxVideoPlayerWidget.releaseAllVideos();
        super.onPause();
    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        mListPost.clear();

        if (DataLocalManager.getPrefUser().getLinkAvatar() != null) {
            Glide.with(this)
                    .load(Host.HOST + DataLocalManager.getPrefUser().getLinkAvatar())
                    .placeholder(R.drawable.unnamed).into(imgThinkPostAvatar);
        }

        setDataProfile();
        getTypeFriendBySeeker();
        setFirstDataPost();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bottomSheetIsFriend != null)
            bottomSheetIsFriend.cancel();
        if (bottomSheetMore != null)
            bottomSheetMore.cancel();
        if (bottomSheetReplyRequestFriend != null)
            bottomSheetReplyRequestFriend.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}