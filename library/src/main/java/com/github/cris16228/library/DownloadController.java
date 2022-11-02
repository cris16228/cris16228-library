package com.github.cris16228.library;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

public class DownloadController {

    private final Context context;
    private final String url;
    private final String app_name;
    private final boolean save_in_cache;
    String destination;
    Dialog dialog;
    LinearProgressIndicator progress;
    TextView download_app_name, download_app_percent, download_app_size;
    ExecutorService executor;
    Handler handler;
    private String original_app_name;
    private String authority;
    NotificationBuilder notificationBuilder;
    public DownloadController(@NonNull Context context, @NonNull String url, String _app_name, boolean save_in_cache) {
        this.context = context;
        this.url = url;
        this.app_name = _app_name;
        this.save_in_cache = save_in_cache;
    }

    public DownloadController(@NonNull Context context, @NonNull String url, String _app_name, boolean save_in_cache, String original_app_name) {
        this.context = context;
        this.url = url;
        this.app_name = _app_name;
        this.save_in_cache = save_in_cache;
        this.original_app_name = original_app_name;
    }

    public DownloadController(@NonNull Context context, @NonNull String url, String _app_name, boolean save_in_cache, String original_app_name,
                              String authority) {
        this.context = context;
        this.url = url;
        this.app_name = _app_name;
        this.save_in_cache = save_in_cache;
        this.original_app_name = original_app_name;
        this.authority = authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public final void enqueueDownload() {
        if (save_in_cache) {
            destination = context.getCacheDir() + "/apks/" + app_name + "/";
        } else if (!TextUtils.isEmpty(original_app_name)) {
            destination = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + original_app_name + "/" + app_name + "/" + ".updates/";
        } else {
            destination = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + app_name + "/" + ".updates/";
        }
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

        AsyncUtils downloader;
        if (executor == null && handler == null)
            downloader = new AsyncUtils();
        else
            downloader = new AsyncUtils(executor, handler);
        downloader.onExecuteListener(new AsyncUtils.onExecuteListener() {
            @Override
            public void preExecute() {
            }

            @Override
            public void doInBackground() {
                int count;
                try {
                    URL link = new URL(url);
                    URLConnection connection = link.openConnection();
                    connection.connect();

                    // this will be useful so that you can show a tipical 0-100%
                    // progress bar
                    int contentLength = connection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(link.openStream(),
                            8192);

                    // Output stream
                    OutputStream output = Files.newOutputStream(Paths.get(destination));

                    byte[] data = new byte[1024];

                    long total = 0;
                    notificationBuilder = new NotificationBuilder(context);
                    notificationBuilder.createDownloadNotification(app_name, "Downloading update...", -1);
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        notificationBuilder.updateDownloadNotification((int) total, contentLength);
                        /*Log.i("Downloader: ", longUtils.getSize(total) + "/" + longUtils.getSize(contentLength));
                         */
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();

                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
            }

            @Override
            public void postDelayed() {
                showInstallOption(destination);
            }
        });
        downloader.execute();
    }


    private void showInstallOption(String destination) {
        if (TextUtils.isEmpty(authority))
            authority = context.getPackageName();
        Uri contentUri = FileProvider.getUriForFile(
                context,
                authority + ".provider",
                new File(destination)
        );
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        install.setData(contentUri);
        if (contentUri != null)
            context.startActivity(install);
    }
}