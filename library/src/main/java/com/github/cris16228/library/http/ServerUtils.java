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

    private static String[] SSIDs = {"BobHouse", "BobHouse 5GHz"};
    /*private String localIP = "http://192.168.1.5/";*/
    private String publicIP = "https://www.cris16228.com/";
    private Context context;

    public static String[] getSSIDs() {
        return SSIDs;
    }

    public static void setSSIDs(String[] SSIDs) {
        ServerUtils.SSIDs = SSIDs;
    }

    /*public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }*/

    public String getPublicIP() {
        return publicIP;
    }

    public void setPublicIP(String publicIP) {
        this.publicIP = publicIP;
    }

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
        /*if (isConnectedWifi(context)) {
            if (isHome(context)) {
                return getLocalIP();
            } else if (!isHome(context))
                return getPublicIP();
        } else {
            return getPublicIP();
        }
        return null;*/
        return getPublicIP();
    }


    public String getValidURL(@NonNull String url) {
        if (/*url.startsWith(getLocalIP()) ||*/ url.startsWith(getPublicIP()))
            return url;
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        return webURL(context) + url;
    }

    public String getRawURL(@NonNull String url) {
        /*if (url.startsWith(getLocalIP())) {
            return url.replace(getLocalIP(), "");
        } else */
        if (url.startsWith(getPublicIP())) {
            return url.replace(getPublicIP(), "");
        } else {
            return url;
        }
    }
}
