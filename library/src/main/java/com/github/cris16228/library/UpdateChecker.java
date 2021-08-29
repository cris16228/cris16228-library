package com.github.cris16228.library;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

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
    String json_link = "http://192.168.1.8/update.json";
    String download_link = "http://192.168.1.8/update.apk";
    DownloadController downloadController;
    Core core;
    private int patch = 0;
    private String version = "";

    private int app_patch = 0;
    private String app_version = "";
    private String app_name = "";

    public UpdateChecker(Activity activity, Download download, int _patch, String _version, String _app_name) {
        this.weakActivity = new WeakReference<>(activity);
        this.download = download;
        app_patch = _patch;
        app_version = _version;
        app_name = _app_name;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        if (core == null)
            core = new Core();
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
                if (core.no_internet == null)
                    core.showNoInternet(activity);
                if (core.isConnectedTo(activity.getApplicationContext())) {
                    UpdateChecker checker = new UpdateChecker(activity, Download.UPDATE, app_patch, app_version, app_name);
                    checker.execute();
                } else
                    core.no_internet.show();
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
                if (core.no_internet == null)
                    core.showNoInternet(activity);
                if (core.isConnectedTo(activity.getApplicationContext())) {
                    UpdateChecker checker = new UpdateChecker(activity, Download.UPDATE, app_patch, app_version, app_name);
                    checker.execute();
                } else
                    core.no_internet.show();
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
