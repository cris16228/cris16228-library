package com.github.cris16228.library;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

public class NotificationBuilder {

    public final Context context;
    final String CHANNEL_ID = "DOWNLOAD_NOTIFICATIONS";
    public int NOTIFICATION_ID = 16228;
    NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            "Download Notifications",
            NotificationManager.IMPORTANCE_HIGH);
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    public NotificationBuilder(Context context) {
        this.context = context;
    }

    public void createDownloadNotification(String title, String content, int icon) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(content);
        if (icon != -1)
            builder.setSmallIcon(icon);
        else
            builder.setSmallIcon(R.drawable.ic_download);
    }

    public void updateDownloadNotification(int progress, int max) {
        if (notificationManager == null || builder == null) return;
        if (progress == 0 && max == 0) return;
        builder.setProgress(max, progress, false);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        if (progress >= max) {
            builder.setContentText("Download Complete");
            builder.setProgress(0, 0, false);
        }
    }
}
