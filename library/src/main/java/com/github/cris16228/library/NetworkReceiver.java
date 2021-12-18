package com.github.cris16228.library;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkReceiver extends BroadcastReceiver {

    private NetworkUtils networkUtils;
    private int app_patch = 0;
    private String app_version = "";
    private String app_name = "";
    private String json_link;
    private String download_link;

    public NetworkReceiver() {
    }

    public NetworkReceiver(String _json_link, String _download_link, int app_patch, String app_version, String app_name) {
        this.json_link = _json_link;
        this.download_link = _download_link;
        this.app_patch = app_patch;
        this.app_version = app_version;
        this.app_name = app_name;
        if (networkUtils == null)
            networkUtils = new NetworkUtils();
    }

    public NetworkReceiver(String _json_link, String _download_link, int app_patch, String app_version, String app_name, NetworkUtils networkUtils) {
        this.json_link = _json_link;
        this.download_link = _download_link;
        this.app_patch = app_patch;
        this.app_version = app_version;
        this.app_name = app_name;
        this.networkUtils = networkUtils;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (networkUtils.no_internet == null)
            networkUtils.showNoInternet(context);
        if (networkUtils.isConnectedTo(context)) {
            networkUtils.no_internet.dismiss();
            UpdateChecker updateChecker = new UpdateChecker((Activity) context, json_link, download_link, UpdateChecker.Download.JSON, app_patch, app_version, app_name);
            updateChecker.check();
        } else
            networkUtils.no_internet.show();
    }
}