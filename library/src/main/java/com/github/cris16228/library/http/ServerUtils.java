package com.github.cris16228.library.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;

public class ServerUtils {

    private static final String[] SSIDs = {"Casa", "Casa5GHz", "AndroidWifi"};
    private final String _local = "http://192.168.1.11/";
    private final String _public = "http://cris16228.com/";
    private Context context;

    public static ServerUtils get(Context context) {
        ServerUtils serverUtils = new ServerUtils();
        serverUtils.context = context;
        return serverUtils;
    }

    public static boolean isConnectedWifi(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
        } catch (SecurityException e) {
            System.out.println("Please add \"android.permission.ACCESS_NETWORK_STATE\" in your AndroidManifest.xml");
        }
        return false;
    }

    public static boolean isHome(Context context) {
        boolean isHome = false;
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String currentSSID = wifiInfo.getSSID();
                if (currentSSID != null) {
                    for (String ssid : SSIDs) {
                        if (ssid.equals(currentSSID)) {
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
        else
            return _public;
    }

    public String getValidURL(@NonNull String url) {
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
