package com.github.cris16228.library;

import static android.content.Context.WIFI_SERVICE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.provider.Settings;

public class NetworkUtils {

    public Dialog no_internet;
    Context context;

    public static NetworkUtils with(Context _context) {
        NetworkUtils networkUtils = new NetworkUtils();
        networkUtils.context = _context;
        return networkUtils;
    }

    public void showNoInternet(Context context) {
        no_internet = new Dialog(context, R.style.no_internet_dialog);
        no_internet.setContentView(R.layout.no_internet_connection);
        no_internet.getWindow().setBackgroundDrawable(null);
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
