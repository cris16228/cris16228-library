package com.github.cris16228.library;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;

public class UpdateChecker extends AsyncTask<Integer, Void, Integer> {

    private final WeakReference<Activity> weakActivity;
    private final Download download;
    String json_link;
    String download_link;
    DownloadController downloadController;
    NetworkUtils networkUtils;
    private int patch = 0;
    private String version = "";

    private int app_patch = 0;
    private String app_version = "";
    private String app_name = "";

    public UpdateChecker(Activity activity, String _json_link, String _download_link, Download download, int _patch, String _version, String _app_name) {
        this.weakActivity = new WeakReference<>(activity);
        this.json_link = _json_link;
        this.download_link = _download_link;
        this.download = download;
        this.app_patch = _patch;
        this.app_version = _version;
        this.app_name = _app_name;
        if (networkUtils == null)
            networkUtils = new NetworkUtils();
    }

    public UpdateChecker(Activity activity, String _json_link, String _download_link, Download download, int _patch, String _version, String _app_name,
                         NetworkUtils _networkUtils) {
        this.weakActivity = new WeakReference<>(activity);
        this.json_link = _json_link;
        this.download_link = _download_link;
        this.download = download;
        this.app_patch = _patch;
        this.app_version = _version;
        this.app_name = _app_name;
        this.networkUtils = _networkUtils;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        if (TextUtils.isEmpty(json_link)) {
            Log.e(UpdateChecker.class.getSimpleName(), weakActivity.get().getResources().getString(R.string.json_link_invalid));
            return null;
        }
        if (TextUtils.isEmpty(download_link)) {
            Log.e(UpdateChecker.class.getSimpleName(), weakActivity.get().getResources().getString(R.string.download_link_invalid));
            return null;
        }
        if (download == Download.JSON) {
            try {
                version = getNewVersion(json_link);
                patch = getNewPatch(json_link);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (download == Download.UPDATE) {
            downloadController = new DownloadController(weakActivity.get(), download_link, app_name);
            downloadController.enqueueDownload();
        }
        return patch;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == null)
            return;
        Activity activity = weakActivity.get();
        if (activity == null
                || activity.isFinishing()
                || activity.isDestroyed()) {
            // activity is no longer valid, don't do anything!
            return;
        }
        if (result > 0 && result > app_patch && download == Download.JSON) {
            MaterialAlertDialogBuilder new_update = new MaterialAlertDialogBuilder(activity);
            new_update.setTitle(activity.getApplicationContext().getResources().getString(R.string.new_update_title_patch, result));
            new_update.setMessage(activity.getApplicationContext().getResources().getString(R.string.new_update_message,
                    activity.getApplicationContext().getResources().getString(R.string.patch)));
            new_update.setPositiveButton(R.string.ok, (dialog, which) -> {
                if (networkUtils.no_internet == null)
                    networkUtils.showNoInternet(activity);
                if (networkUtils.isConnectedTo(activity.getApplicationContext())) {
                    UpdateChecker checker = new UpdateChecker(activity, json_link, download_link, Download.UPDATE, app_patch, app_version, app_name);
                    checker.execute();
                } else
                    networkUtils.no_internet.show();
            });
            new_update.setNegativeButton(R.string.cancel, (dialog, which) -> {
            });
            new_update.show();
        }
        if (!TextUtils.isEmpty(version) && !version.equals(app_version) && download == Download.JSON) {
            MaterialAlertDialogBuilder new_update = new MaterialAlertDialogBuilder(activity);
            new_update.setTitle(activity.getApplicationContext().getResources().getString(R.string.new_update_title_app, version));
            new_update.setMessage(activity.getApplicationContext().getResources().getString(R.string.new_update_message,
                    activity.getApplicationContext().getResources().getString(R.string.app_update)));
            new_update.setPositiveButton(R.string.ok, (dialog, which) -> {
                if (networkUtils.no_internet == null)
                    networkUtils.showNoInternet(activity);
                if (networkUtils.isConnectedTo(activity.getApplicationContext())) {
                    UpdateChecker checker = new UpdateChecker(activity, json_link, download_link, Download.UPDATE, app_patch, app_version, app_name);
                    checker.execute();
                } else
                    networkUtils.no_internet.show();
            });
            new_update.setNegativeButton(R.string.cancel, (dialog, which) -> {
            });
            new_update.show();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    private int getNewPatch(String _url) throws IOException {
        URL link = new URL(_url);
        JsonReader jr = new JsonReader(new InputStreamReader(link.openStream()));
        JsonElement je = JsonParser.parseReader(jr);
        JsonObject jo = je.getAsJsonObject();
        return jo.get("versionCode").getAsInt();
    }

    private String getNewVersion(String _url) throws IOException {
        URL link = new URL(_url);
        JsonReader jr = new JsonReader(new InputStreamReader(link.openStream()));
        JsonElement je = JsonParser.parseReader(jr);
        JsonObject jo = je.getAsJsonObject();
        return jo.get("version").getAsString();
    }

    public enum Download {
        JSON,
        UPDATE
    }
}
