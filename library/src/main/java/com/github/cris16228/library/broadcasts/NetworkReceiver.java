package com.github.cris16228.library.broadcasts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.cris16228.library.NetworkUtils;
import com.github.cris16228.library.http.updater.UpdateChecker;

public class NetworkReceiver extends BroadcastReceiver {

    private NetworkUtils networkUtils;
    private int app_patch = 0;
    private String app_version = "";
    private String app_name = "";
    private String packageName;
    private String store_url;

    public NetworkReceiver() {
    }


    public NetworkReceiver(String _packageName, String _store_url, int app_patch, String app_version, String app_name) {
        this.packageName = _packageName;
        this.store_url = _store_url;
        this.app_patch = app_patch;
        this.app_version = app_version;
        this.app_name = app_name;
        if (networkUtils == null)
            networkUtils = new NetworkUtils();
    }


    public NetworkReceiver(String _packageName, String _store_url, int app_patch, String app_version, String app_name, NetworkUtils networkUtils) {
        this.packageName = _packageName;
        this.store_url = _store_url;
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
            UpdateChecker checker = new UpdateChecker((Activity) context, UpdateChecker.Download.UPDATE, app_patch, app_version, app_name,
                    packageName, store_url);
            checker.check();
        } else
            networkUtils.no_internet.show();
    }
}