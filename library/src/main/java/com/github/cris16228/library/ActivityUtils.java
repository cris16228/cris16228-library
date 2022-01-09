package com.github.cris16228.library;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.view.ContentInfoCompat;

public class ActivityUtils {

    Context context;

    public static ActivityUtils with(Context _context) {
        ActivityUtils activityUtils = new ActivityUtils();
        activityUtils.context = _context;
        return activityUtils;
    }

    public void restartApp(Context currentActivity, Class<?> destinationActivity) {
        ((Activity) currentActivity).finish();
        startActivity(currentActivity, destinationActivity);
    }

    public void delayActivity(Class<?> destinationActivity, long delay, boolean finish) {
        if (delay <= 0) {
            startActivity(context, destinationActivity);
            if (finish)
                ((Activity) context).finish();
        } else {
            new Handler().postDelayed(() -> {
                startActivity(context, destinationActivity);
                if (finish)
                    ((Activity) context).finish();
            }, delay);
        }
    }

    public void delayActivity(Class<?> destinationActivity, long delay, boolean finish, String name, Bundle value) {
        if (delay <= 0) {
            startActivity(context, destinationActivity, name, value);
            if (finish)
                ((Activity) context).finish();
        } else {
            new Handler().postDelayed(() -> {
                startActivity(context, destinationActivity, name, value);
                if (finish)
                    ((Activity) context).finish();
            }, delay);
        }
    }

    public void openApp(PackageInfo app) {
        Intent open_app_intent = context.getPackageManager().getLaunchIntentForPackage(app.packageName);
        if (open_app_intent != null)
            context.startActivity(open_app_intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresPermission(anyOf = {Manifest.permission.REQUEST_DELETE_PACKAGES, Manifest.permission.DELETE_PACKAGES})
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

    public void enableFullscreen(Window window) {
        if (window == null) {
            return;
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final View decorView = window.getDecorView();
        if (decorView != null) {
            int uiOption = decorView.getSystemUiVisibility();
            uiOption |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOption);
        }
    }

    public void disableFullscreen(Window window) {
        if (window == null) {
            return;
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        final View decorView = window.getDecorView();
        if (decorView != null) {
            int uiOption = decorView.getSystemUiVisibility();
            uiOption &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
            uiOption &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            uiOption &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOption);
        }
    }

    public void startActivity(Context currentActivity, Class<?> destinationActivity) {
        Intent activity = new Intent(currentActivity, destinationActivity);
        activity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        currentActivity.startActivity(activity);
    }

    public void startActivity(Context currentActivity, Class<?> destinationActivity, String name, Bundle value) {
        Intent activity = new Intent(currentActivity, destinationActivity);
        activity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.putExtra(name, value);
        currentActivity.startActivity(activity);
    }

    public void startActivity(Context currentActivity, Class<?> destinationActivity, @ContentInfoCompat.Flags int[] flags) {
        Intent activity = new Intent(currentActivity, destinationActivity);
        for (int flag : flags) {
            activity.addFlags(flag);
        }
        currentActivity.startActivity(activity);
    }

    public void startActivity(Context currentActivity, Class<?> destinationActivity, boolean finish, String name, Bundle value) {
        Intent activity = new Intent(currentActivity, destinationActivity);
        activity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.putExtra(name, value);
        currentActivity.startActivity(activity);
        if (finish)
            try {
                ((Activity) context).finish();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), e.toString());
            }
    }

    public void startActivity(Context currentActivity, Class<?> destinationActivity, boolean finish) {
        Intent activity = new Intent(currentActivity, destinationActivity);
        activity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        currentActivity.startActivity(activity);
        if (finish)
            try {
                ((Activity) context).finish();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), e.toString());
            }
    }
}
