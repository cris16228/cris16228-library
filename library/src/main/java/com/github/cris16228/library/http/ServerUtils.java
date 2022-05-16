package com.github.cris16228.library.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class ServerUtils {

    private static final String[] SSIDs = {"Casa", "Casa5GHz"};
    private final String _local = "http://192.168.1.11";
    private final String _public = "http://cris16228.com";

    public static boolean isConnectedWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
    }

    public static boolean isHome(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            String currentSSID = wifiInfo.getSSID();
            if (currentSSID != null) {
                for (String ssid : SSIDs) {
                    if (ssid.equals(currentSSID))
                        return true;
                }
            }
        }
        return false;
    }

    public String webURL(Context context) {
        if (isConnectedWifi(context) && isHome(context))
            return _local;
        else
            return _public;
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
