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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;


public class Core {

    public String PREF = "";
    public Dialog no_internet;

    public String convertToFirstUpper(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toUpperCase();
    }

    public void refreshFragment(FragmentActivity fragmentActivity, Fragment fragment) {
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.detach(fragment).attach(fragment).commit();
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
}
