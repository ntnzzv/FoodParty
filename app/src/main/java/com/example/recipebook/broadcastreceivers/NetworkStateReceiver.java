package com.example.recipebook.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");

        State state = info.getState();

        if (state != State.CONNECTED)
            Toast.makeText(context, "Network OFF", Toast.LENGTH_LONG).show();


    }
}


