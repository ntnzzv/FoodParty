package com.example.recipebook.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.recipebook.firebase.AuthGoogleService;

public class NetworkStateReceiver extends BroadcastReceiver {
    private static boolean off=false;
    private static NetworkStateReceiver networkStateReceiverInstance = null;

    public static NetworkStateReceiver getInstance() {
        if (networkStateReceiverInstance == null)
            networkStateReceiverInstance = new NetworkStateReceiver();
        return networkStateReceiverInstance;
    }

    private NetworkStateReceiver() {
    }

    public static boolean isOff() {
        return off;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");

        State state = info.getState();

        if (state != State.CONNECTED) {
            Toast.makeText(context, "Network OFF", Toast.LENGTH_LONG).show();
            off=true;
        }
        if(off && state == State.CONNECTED) {
            Toast.makeText(context, "Network is back ON", Toast.LENGTH_LONG).show();
            off=false;
        }
    }
}


