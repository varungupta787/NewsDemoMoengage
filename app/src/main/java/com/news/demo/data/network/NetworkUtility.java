package com.news.demo.data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.news.demo.NewsApplication;

public class NetworkUtility {

    private static WifiManager sWifiManager = null;
    private static ConnectivityManager sManager = null;

    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = getConnectivityManager();
        WifiManager wifiManager = getWifiManager();

        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        if ((netInfo != null && netInfo.isConnected())
                || (wifiManager != null && wifiManager.isWifiEnabled())) {
            return true;
        } else {
            return false;
        }
    }

    public static WifiManager getWifiManager() {
        if (null == sWifiManager)
            sWifiManager = (WifiManager) NewsApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return sWifiManager;
    }

    public static ConnectivityManager getConnectivityManager() {
        if (null == sManager)
            sManager = (ConnectivityManager) NewsApplication.getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return sManager;
    }
}
