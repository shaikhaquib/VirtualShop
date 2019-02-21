package com.virtualshop.virtualshop.Firebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Shaikh Aquib on 14-May-18.
 */

public class GcmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("com.demo.FirebaseMessagingReceiveService");
        i.setClass(context,MessagingService.class);
        context.startService(i);
    }
}
