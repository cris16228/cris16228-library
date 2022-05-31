package com.github.cris16228.library.deviceutils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

public class PackageUtils {

    Context context;

    public static PackageUtils with(Context _context) {
        PackageUtils packageUtils = new PackageUtils();
        packageUtils.context = _context;
        return packageUtils;
    }

    public PackageInfo appFromPackage(String packageName) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(packageName, 0);
    }

    public PackageInfo getPermissions(PackageInfo app) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS);
    }

    public PackageInfo getActivities(PackageInfo app) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(app.packageName, PackageManager.GET_ACTIVITIES);
    }

    public PackageInfo getReceivers(PackageInfo app) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(app.packageName, PackageManager.GET_RECEIVERS);
    }

    public PackageInfo getProviders(PackageInfo app) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(app.packageName, PackageManager.GET_PROVIDERS);
    }

    public PackageInfo getServices(PackageInfo app) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(app.packageName, PackageManager.GET_SERVICES);
    }

    public boolean isSystemApp(PackageInfo p) {
        return ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM);
    }

    public boolean isAppOld(int versionCode, int onlineVersionCode) {
        int value = Integer.compare(versionCode, onlineVersionCode);
        /*Returns: the value 0 if x == y; a value less than 0 if x < y; and a value greater than 0 if x > y*/
        if (value == 0)
            return false;
        return value < 0;
    }

    public void openApp(PackageInfo app) {
        Intent open_app_intent = context.getPackageManager().getLaunchIntentForPackage(app.packageName);
        if (open_app_intent != null)
            context.startActivity(open_app_intent);
    }


    public void uninstallApp(PackageInfo app, Activity activity) {
        Intent delete_intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        Uri uri = Uri.fromParts("package", app.packageName, null);
        delete_intent.setData(uri);
        context.startActivity(delete_intent);
        activity.finish();
    }

    public void infoApp(PackageInfo app) {
        Intent open_info_intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", app.packageName, null);
        open_info_intent.setData(uri);
        context.startActivity(open_info_intent);
    }
}
