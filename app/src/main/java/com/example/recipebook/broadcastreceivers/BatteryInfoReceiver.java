package com.example.recipebook.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.example.recipebook.R;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

public class BatteryInfoReceiver extends BroadcastReceiver {
    private boolean show ;
    private float criticalPercent ;
    private static BatteryInfoReceiver batteryInfoReceiverInstance = null;

    public static BatteryInfoReceiver getInstance() {
        if (batteryInfoReceiverInstance == null)
            batteryInfoReceiverInstance = new BatteryInfoReceiver();
        return batteryInfoReceiverInstance;
    }

    public BatteryInfoReceiver() {
        init();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // Are we charging / charged?
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = isCharging(status);

        // Get params for battery percent calculations
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPercent = getBatteryPercent(level, scale);

        if (show && batteryPercent < criticalPercent && !isCharging) {

            new FancyGifDialog.Builder(context)
                    .setTitle("Low battery")
                    .setMessage("Please charge your battery or save your recipe, it may be lost")
                    .setTitleTextColor(R.color.browser_actions_title_color)
                    .setDescriptionTextColor(R.color.browser_actions_text_color)
                    .setNegativeBtnText("Don't care")
                    .setPositiveBtnBackground(R.color.common_google_signin_btn_text_dark)
                    .setPositiveBtnText("Ok")
                    .setNegativeBtnBackground(R.color.purple_200)
                    .setGifResource(R.drawable.low_battery_gif)
                    .isCancellable(true)
                    .OnPositiveClicked(() -> {
                        criticalPercent = 10;
                    })
                    .OnNegativeClicked(() -> {
                        show = false;
                    })
                    .build();


        }
        if(isCharging || batteryPercent>20)
            init();



    }

    private void init() {
        show=true;
        criticalPercent=20;
    }


    private float getBatteryPercent(int level, float scale) {
        return level * 100 / scale;
    }

    private boolean isCharging(int status) {
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
    }
}
