package com.nampt.socialnetworkproject.view.notification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.BaseResponse;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.api.notifyService.NotifyService;
import com.nampt.socialnetworkproject.api.notifyService.NotifyServiceImpl;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.Notification;
import com.nampt.socialnetworkproject.util.CalculateTimeUtil;
import com.nampt.socialnetworkproject.view.detailPost.DetailPostActivity;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;
import com.nampt.socialnetworkproject.view.search.SearchActivity;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifyViewHolder> {
    private Context mContext;
    private List<Notification> notificationList;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public NotificationAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificatioon, parent, false);
        return new NotifyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotifyViewHolder holder, int position) {
        Notification notify = notificationList.get(position);
        if (notify == null) return;
        if (!notify.isSeen()) {
            holder.layoutItem.setBackgroundResource(R.color.colorBlueThinSuper);
        } else holder.layoutItem.setBackgroundResource(R.color.whiteCardColor);

        if (notify.getSender().getAvatar() != null)
            Glide.with(mContext)
                    .load(Host.HOST + notify.getSender().getAvatar())
                    .placeholder(R.drawable.unnamed).into(holder.imgUser);
        else
            Glide.with(mContext)
                    .load(R.drawable.unnamed)
                    .placeholder(R.drawable.unnamed).into(holder.imgUser);

        // build the string
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString strUserName = new SpannableString(notify.getSender().getName());
        // set the style
        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        strUserName.setSpan(new StyleSpan(Typeface.BOLD), 0, strUserName.length(), flag);
        strUserName.setSpan(new ForegroundColorSpan(Color.BLACK), 0, strUserName.length(), flag);
        builder.append(strUserName);
        switch (notify.getType()) {
            case 1:
                builder.append(" đã đăng một bài viết mới");
                holder.txtContentNotify.setText(builder);
                holder.iconType.setImageDrawable(mContext.getDrawable(R.drawable.ic_post_add_24));
                holder.iconType.setBackground(mContext.getDrawable(R.drawable.green_circle_bk));
                break;
            case 2:
                builder.append(" gửi cho bạn lời mời kết bạn");
                holder.txtContentNotify.setText(builder);
                holder.iconType.setImageDrawable(mContext.getDrawable(R.drawable.ic_person_add_24));
                holder.iconType.setBackground(mContext.getDrawable(R.drawable.pink_circle_bk));
                break;
//            case 3:
//                builder.append(" đã nhắn tin cho bạn");
//                holder.txtContentNotify.setText(builder);
//                holder.iconType.setImageDrawable(mContext.getDrawable(R.drawable.ic_chat_add_24));
//                holder.iconType.setBackground(mContext.getDrawable(R.drawable.orange_circle_bk));
//                break;
//            case 4:
//                builder.append(" đã nhắn tin trong nhóm ");
//                holder.txtContentNotify.setText(builder);
//                holder.iconType.setImageDrawable(mContext.getDrawable(R.drawable.ic_group_chat_24));
//                holder.iconType.setColorFilter(Color.argb(255, 255, 255, 255));
//                holder.iconType.setBackground(mContext.getDrawable(R.drawable.orange_circle_bk));
//                break;
        }
        holder.txtTime.setText(CalculateTimeUtil.getInstance().calculatorTimeCommon(notify.getCreateTime(),new Date()));

        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(notify.getId()));
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeleteNotify(holder, notify.getId(), holder.getAdapterPosition());

            }
        });

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.layoutItem.setBackgroundResource(R.color.whiteCardColor);
                notify.setSeen(true);
                NotifyServiceImpl.getInstance().setSeen(notify.getId());

                if (notify.getType() == NotificationActivity.TYPE_NOTIFY_POST) {
                    Intent intent = new Intent(mContext, DetailPostActivity.class);
                    intent.putExtra("postId", notify.getDataId());
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                } else if (notify.getType() == NotificationActivity.TYPE_NOTIFY_FRIEND_REQUEST) {
                    Intent intent = new Intent(mContext, ProfileUserActivity.class);
                    intent.putExtra("userId", notify.getDataId());
                    ((Activity) mContext).startActivityForResult(intent, SearchActivity.LAUNCH_PROFILE_ACTIVITY);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                }
            }
        });

    }


    private void handleDeleteNotify(NotifyViewHolder holder, int id, int adapterPosition) {
        holder.imgDelete.setVisibility(View.GONE);
        holder.imgLoadingDelete.setVisibility(View.VISIBLE);
        NotifyService.service.deleteNotify(DataLocalManager.getPrefUser().getAccessToken(), id)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.body().getCode() == 1000) {
                            holder.imgDelete.setVisibility(View.VISIBLE);
                            holder.imgLoadingDelete.setVisibility(View.GONE);
                            notificationList.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            Toast.makeText(mContext, "Đã xóa bình luận !", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getCode() == 9995) {
                            Toast.makeText(mContext, "Thông báo đã bị xóa trước đó", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getCode() == 9993) {
                            Toast.makeText(mContext, "Sai token !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        holder.imgDelete.setVisibility(View.VISIBLE);
                        holder.imgLoadingDelete.setVisibility(View.GONE);
                        Toast.makeText(mContext, "Lỗi mạng, vui lòng thử lại !", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (notificationList == null || notificationList.isEmpty()) return 0;
        return notificationList.size();
    }

    public class NotifyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgUser;
        private TextView txtContentNotify, txtTime;
        private ImageView iconType;
        private SwipeRevealLayout swipeRevealLayout;
        private View layoutItem;
        private ImageView imgDelete;
        private ProgressBar imgLoadingDelete;


        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.img_user_notify);
            txtContentNotify = itemView.findViewById(R.id.txt_content_notification);
            txtTime = itemView.findViewById(R.id.txt_time_notification);
            iconType = itemView.findViewById(R.id.img_icon_type_notify);
            layoutItem = itemView.findViewById(R.id.layout_item_notify);
            swipeRevealLayout = itemView.findViewById(R.id.swipe_reveal_layout_item_notify);

            imgDelete = itemView.findViewById(R.id.img_delete_notify);
            imgLoadingDelete = itemView.findViewById(R.id.progress_bar_delete_notify);
        }
    }
}
