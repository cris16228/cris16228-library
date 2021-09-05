package com.github.cris16228.library;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;

public class DownloadController {

    private final Context context;
    private final String url;
    private final String app_name;

    public DownloadController(@NonNull Context context, @NonNull String url, String _app_name) {
        this.context = context;
        this.url = url;
        this.app_name = _app_name;
    }

    public final void enqueueDownload() {
        String destination = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + app_name + "/" + ".updates/";
        System.out.println(destination);
        File path = new File(destination);
        if (!path.exists()) {
            path.mkdirs();
        }
        destination = destination + "update.apk";
        Uri uri = Uri.parse("file://" + destination);
        File file = new File(destination);
        if (file.exists()) {
            file.delete();
        }
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setMimeType("application/vnd.android.package-archive");
        request.setTitle(context.getResources().getString(R.string.updater_title, app_name));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationUri(uri);
        showInstallOption(destination);
        downloadManager.enqueue(request);
    }

    private void showInstallOption(String destination) {
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(@NonNull Context context, @NonNull Intent intent) {

                Uri contentUri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.LIBRARY_PACKAGE_NAME + ".provider",
                        new File(destination)
                );
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                install.setData(contentUri);
                if (contentUri != null)
                    context.startActivity(install);
                context.unregisterReceiver(this);
            }
        };
        context.registerReceiver(onComplete, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
    }
}