package com.nampt.socialnetworkproject.api.notifyService.fcm;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.api.Host;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;
import com.nampt.socialnetworkproject.model.MessActivityModel;
import com.nampt.socialnetworkproject.view.detailPost.DetailPostActivity;
import com.nampt.socialnetworkproject.view.home.HomeActivity;
import com.nampt.socialnetworkproject.view.message.MessageActivity;
import com.nampt.socialnetworkproject.view.profileUser.ProfileUserActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "1";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData() != null) {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String urlIcon = remoteMessage.getData().get("icon");
            int dataId = Integer.parseInt(remoteMessage.getData().get("data_id"));
            int typeNotify = Integer.parseInt(remoteMessage.getData().get("type_notify"));


            Log.e("TAG-message-received", " title:" + title + "\nbody:" + body +
                    "\nicon_url:" + urlIcon + "\ndata_id:" + dataId + "\ntype_notify:" + typeNotify +
                    "\nisMessActivityShowing:" + DataLocalManager.getMessageActivityInstall());
            showNotification(title, body, urlIcon, dataId, typeNotify);

        }

    }


    private void showNotification(String body) {
        showNotification("google", body, "null", 0, 0);
    }

    private void showNotification(String title, String body, String urlIcon, int dataId, int typeNotify) {
        MessActivityModel messActivityShowing = DataLocalManager.getMessageActivityInstall();

        switch (typeNotify) {
            case 1:
            case 2:
                initNotification(title, body, urlIcon, dataId, typeNotify);
                break;

            case 3:
                if (messActivityShowing != null) {
                    Log.e("messActivityShowing", messActivityShowing.toString());
                    if (messActivityShowing.isInstall() &&
                            !messActivityShowing.isRoom() &&
                            messActivityShowing.getDataId() == dataId) {
                        return;
                    }
                }
                initNotification(title, body, urlIcon, dataId, typeNotify);

                break;
            case 4:
                if (messActivityShowing != null) {
                    if (messActivityShowing.isInstall() &&
                            messActivityShowing.isRoom() &&
                            messActivityShowing.getDataId() == dataId) {
                        return;
                    }
                }
                initNotification(title, body, urlIcon, dataId, typeNotify);
                break;
        }

    }

    private void initNotification(String title, String body, String urlIcon, int dataId, int typeNotify) {
        Intent intent = new Intent(this, HomeActivity.class);
        switch (typeNotify) {
            case 1:
                intent = new Intent(this, DetailPostActivity.class);
                intent.putExtra("postId", dataId);
                break;
            case 2:
                intent = new Intent(this, ProfileUserActivity.class);
                intent.putExtra("userId", dataId);
                break;
            case 3:
                intent = new Intent(this, MessageActivity.class);
                intent.putExtra("roomName", title);
                intent.putExtra("isRoom", false);
                intent.putExtra("partnerId", dataId);
                break;
            case 4:
                intent = new Intent(this, MessageActivity.class);
                intent.putExtra("roomId", dataId);
                intent.putExtra("roomName", title.replace("Nhóm: ", ""));
                intent.putExtra("isRoom", true);
                break;
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_32)
                .setLargeIcon(getBitmapFromUrl(urlIcon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());

        //startForeground(1, builder.build());
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            if (!imageUrl.equals("null")) {
                URL url = new URL(Host.HOST + imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } else {
                return ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.unnamed, null)).getBitmap();
            }
        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }


}
