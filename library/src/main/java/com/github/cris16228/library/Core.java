package com.github.cris16228.library;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WIFI_SERVICE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.github.cris16228.library.R;

import java.util.Locale;

enum Theme {
    DARK,
    LIGHT,
    AUTO
}

public class Core {

    public final String OFFLINE_PATH = "path";
    public String PREF = "";

    //Theme
    public final String THEME = "theme";
    public final String LIGHT = "light";
    public final String DARK = "dark";
    public final String AUTO = "follow_system";

    //Locale
    public final String LOCALE = "locale";
    public final String EN = "en";
    public final String IT = "it";

    public Dialog no_internet;

    public Theme theme;

    public void setSharedPref(String pref) {
        this.PREF = pref;
    }

    public void restartApp(Context currentActivity, Class<?> destinationActivity) {
        ((Activity) currentActivity).finish();
        startActivity(currentActivity, destinationActivity);
    }

    public void onStartSetLocale(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF, MODE_PRIVATE);
        String language = sharedPreferences.getString(LOCALE, EN);
        setLocale(context, language);
    }

    public void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public Theme getTheme(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
        switch (pref.getString(THEME, LIGHT)) {
            case DARK:
                return Theme.DARK;
            case LIGHT:
                return Theme.LIGHT;
            case AUTO:
                return Theme.AUTO;
        }
        return Theme.LIGHT;
    }

    public void applyTheme(Context context) {
        if (getTheme(context) == Theme.LIGHT)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (getTheme(context) == Theme.DARK)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else if (getTheme(context) == Theme.AUTO)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void delayActivity(Context currentActivity, Class<?> destinationActivity, long delay, boolean finish) {
        if (delay <= 0) {
            startActivity(currentActivity, destinationActivity);
            if (finish)
                ((Activity) currentActivity).finish();
        } else {
            new Handler().postDelayed(() -> {
                startActivity(currentActivity, destinationActivity);
                if (finish)
                    ((Activity) currentActivity).finish();
            }, delay);
        }
    }

    public void startActivity(Context currentActivity, Class<?> destinationActivity) {
        Intent activity = new Intent(currentActivity, destinationActivity);
        activity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        currentActivity.startActivity(activity);
    }

    public String convertToFirstUpper(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toUpperCase();
    }

    private void prependTimeAndUnit(StringBuffer timeBuf, long time, String unit) {
        if (time < 1) {
            return;
        }

        if (timeBuf.length() > 0) {
            timeBuf.insert(0, " ");
        }

        timeBuf.insert(0, unit);
        timeBuf.insert(0, time);
    }

    public String getSize(long size) {
        long n = 1000;
        String s;
        double kb = size / n;
        double mb = kb / n;
        double gb = mb / n;
        double tb = gb / n;
        if (size < n) {
            s = size + " Bytes";
        } else if (size < n * n) {
            s = String.format("%.2f", kb) + " KB";
        } else if (size < n * n * n) {
            s = String.format("%.2f", mb) + " MB";
        } else if (size < n * n * n * n) {
            s = String.format("%.2f", gb) + " GB";
        } else {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }

    public void refreshFragment(FragmentActivity fragmentActivity, Fragment fragment) {
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.detach(fragment).attach(fragment).commit();
    }

    public String toReadableTime(long timeInMillis) {

        if (timeInMillis < 1) {
            return String.valueOf(timeInMillis);
        }

        StringBuffer timeBuf = new StringBuffer();

        // second (1000ms) & above
        long time = timeInMillis / 1000;
        if (time < 1) {
            return "now";
        }

        long seconds = time % 60;
        prependTimeAndUnit(timeBuf, seconds, "s");

        // minute(60s) & above
        time = time / 60;
        if (time < 1) {
            return timeBuf.toString();
        }

        long minutes = time % 60;
        prependTimeAndUnit(timeBuf, minutes, "m");

        // hour(60m) & above
        time = time / 60;
        if (time < 1) {
            return timeBuf.toString();
        }

        long hours = time % 24;
        prependTimeAndUnit(timeBuf, hours, "h");

        // day(24h) & above
        time = time / 24;
        if (time < 1) {
            return timeBuf.toString();
        }

        long day = time % 365;
        prependTimeAndUnit(timeBuf, day, "d");

        // year(365d) ...
        time = time / 365;
        if (time < 1) {
            return timeBuf.toString();
        }

        prependTimeAndUnit(timeBuf, time, "y");

        return timeBuf.toString();
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

    public void showNoInternet(Context context) {
        no_internet = new Dialog(context, R.style.no_internet_dialog);
        no_internet.setContentView(R.layout.no_internet_connection);
        no_internet.setCancelable(false);
        no_internet.setCanceledOnTouchOutside(false);
        /*no_internet.findViewById(R.id.no_internet_turn_on_data_btn).setOnClickListener(v -> {
            Intent settings = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            context.startActivity(settings);
        });*/
        no_internet.findViewById(R.id.no_internet_turn_on_wifi_btn).setOnClickListener(v -> {
            WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
        });
        no_internet.findViewById(R.id.no_internet_turn_on_data_btn).setOnClickListener(v -> {
            Intent settings = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
            context.startActivity(settings);
        });
    }

    public boolean isConnectedTo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    public String fileName(String path) {
        int name = path.lastIndexOf("/");
        path = path.substring(name + 1);
        int ext = path.lastIndexOf(".");
        return path.substring(0, ext);
    }

    public String getPathFromURI(Context context, Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }
}
