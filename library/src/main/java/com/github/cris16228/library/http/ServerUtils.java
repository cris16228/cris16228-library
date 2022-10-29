package com.github.cris16228.library.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;

import com.github.cris16228.library.deviceutils.DeviceUtils;

public class ServerUtils {

    private static final String[] SSIDs = {"Casa", "Casa5GHz", "It hurts when IP"};
    private final String _local = "http://192.168.1.11/";
    private final String _public = "http://www.cris16228.com/";
    private Context context;

    public static ServerUtils get(Context context) {
        ServerUtils serverUtils = new ServerUtils();
        serverUtils.context = context;
        return serverUtils;
    }

    public boolean isConnectedWifi(Context context) {
        if (DeviceUtils.isEmulator()) {
            return true;
        }
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            @SuppressLint("MissingPermission") NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
        } catch (SecurityException e) {
            System.out.println("Please add \"android.permission.ACCESS_NETWORK_STATE\" in your AndroidManifest.xml");
        }
        return false;
    }

    public boolean isHome(Context context) {
        boolean isHome = false;
        if (DeviceUtils.isEmulator()) {
            return true;
        }
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String currentSSID = wifiInfo.getSSID();
                System.out.println("Your SSID is: " + currentSSID);
                if (DeviceUtils.isEmulator()) {
                    System.out.println(currentSSID);
                }
                if (currentSSID != null) {
                    currentSSID = currentSSID.replace("\"", "");
                    for (String ssid : SSIDs) {
                        if (currentSSID.equals(ssid)) {
                            isHome = true;
                            break;
                        }
                    }
                }
            }
        } catch (SecurityException e) {
            System.out.println("Please add \"android.permission.ACCESS_WIFI_STATE\" in your AndroidManifest.xml");
        }
        return isHome;
    }

    public String webURL(Context context) {
        if (isConnectedWifi(context) && isHome(context))
            return _local;
        else if (isConnectedWifi(context) && !isHome(context))
            return _public;
        else
            return _local;
    }


    public String getValidURL(@NonNull String url) {
        if (url.startsWith(_local) || url.startsWith(_public))
            return url;
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        return webURL(context) + url;
    }

    public enum actions {
        UPLOAD("upload"),
        DOWNLOAD("download");
        private final String action;

        actions(String _action) {
            this.action = _action;
        }

        public String getValue() {
            return action;
        }
    }
}
