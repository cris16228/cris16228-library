package com.github.cris16228.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ActivityUtils {

    Core core;
    Context context;

    public ActivityUtils with(Core _core) {
        core = _core;
        return this;
    }

    public ActivityUtils with(Context _context) {
        context = _context;
        return this;
    }

    public ActivityUtils with(Core _core, Context _context) {
        context = _context;
        core = _core;
        return this;
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
}
