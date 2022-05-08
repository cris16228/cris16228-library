package com.github.cris16228.library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.karumi.dexter.BuildConfig;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadController {

    private final Context context;
    private final String url;
    private final String app_name;
    private final boolean save_in_cache;
    String destination;
    private String original_app_name;

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

        AsyncUtils downloader = new AsyncUtils();
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
                    int lenghtOfFile = connection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(link.openStream(),
                            8192);

                    // Output stream
                    OutputStream output = new FileOutputStream(destination);

                    byte[] data = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        /*publishProgress("" + (int) ((total * 100) / lenghtOfFile));*/

                        // writing data to file
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
       /* DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setMimeType("application/vnd.android.package-archive");
        request.setTitle(context.getResources().getString(R.string.updater_title, app_name));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationUri(uri);
        showInstallOption(destination);
        downloadManager.enqueue(request);*/
    }


    private void showInstallOption(String destination) {
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
    }
}