package com.buc.gradution.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.widget.Toast;

public class NetworkService {
    public void getNetworkState(Context context, ConnectivityManager.NetworkCallback networkCallback){
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
                .build();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }
    public static boolean isConnected(Context context){
        boolean conn = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if(capabilities != null){
            if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)){
                conn = true;
            }
        }
        return conn;
    }
    public static void connectionFailed(Context context){
        Toast.makeText(context, "Network not available", Toast.LENGTH_SHORT).show();
    }
}
