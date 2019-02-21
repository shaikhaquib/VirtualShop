package com.virtualshop.virtualshop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InstallReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String referrer = intent.getStringExtra("referrer");
        Log.d("refferer",referrer);

        //Use the referrer
    }
}