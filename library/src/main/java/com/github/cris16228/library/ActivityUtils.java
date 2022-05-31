package com.github.cris16228.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.view.ContentInfoCompat;
import androidx.fragment.app.FragmentActivity;

import com.github.cris16228.library.deviceutils.DeviceUtils;

public class ActivityUtils {

    Context context;

    public static ActivityUtils with(Context _context) {
        ActivityUtils activityUtils = new ActivityUtils();
        activityUtils.context = _context;
        return activityUtils;
    }

    public void restartApp(Context currentActivity, Class<?> destinationActivity) {
        if (currentActivity instanceof FragmentActivity)
            ((FragmentActivity) currentActivity).finish();
        else
            ((Activity) currentActivity).finish();
        if (DeviceUtils.isEmulator())
            System.out.println("Finishing " + currentActivity.getClass().getSimpleName());
        restartActivity(currentActivity, destinationActivity);
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

    public void delayActivity(Class<?> destinationActivity, long delay, boolean finish, Bundle... bundles) {
        if (delay <= 0) {
            startActivity(context, destinationActivity, bundles);
            if (finish)
                ((Activity) context).finish();
        } else {
            new Handler().postDelayed(() -> {
                startActivity(context, destinationActivity, bundles);
                if (finish)
                    ((Activity) context).finish();
            }, delay);
        }
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

    public void restartActivity(Context currentActivity, Class<?> destinationActivity) {
        Intent activity = new Intent(currentActivity, destinationActivity);
        activity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        currentActivity.startActivity(activity);
    }

    public void startActivity(Context currentActivity, Class<?> destinationActivity, Bundle... bundles) {
        Intent activity = new Intent(currentActivity, destinationActivity);
        activity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        for (Bundle bundle : bundles) {
            activity.putExtras(bundle);
        }
        currentActivity.startActivity(activity);
    }

    public void startActivity(Context currentActivity, Class<?> destinationActivity, @ContentInfoCompat.Flags int[] flags) {
        Intent activity = new Intent(currentActivity, destinationActivity);
        if (flags.length > 0)
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
