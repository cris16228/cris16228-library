package com.github.cris16228.library;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkReceiver extends BroadcastReceiver {

    private Core core;

    private int app_patch = 0;
    private String app_version = "";
    private String app_name = "";

    public NetworkReceiver(int app_patch, String app_version, String app_name) {
        this.app_patch = app_patch;
        this.app_version = app_version;
        this.app_name = app_name;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (core == null)
            core = new Core();
        if (core.no_internet == null)
            core.showNoInternet(context);
        if (core.isConnectedTo(context)) {
            core.no_internet.dismiss();
            UpdateChecker updateChecker = new UpdateChecker((Activity) context, UpdateChecker.Download.JSON, app_patch, app_version, app_name);
            updateChecker.execute();
        } else
            core.no_internet.show();
    }
}