package com.virtualshop.virtualshop.Firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.virtualshop.virtualshop.Activity.MainActivity;
import com.virtualshop.virtualshop.Config.DbHelper;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Shaikh Aquib on 07-May-18.
 */

public class MessagingService extends FirebaseMessagingService {

    DbHelper dbHelper = new DbHelper(this);
    String MyPREFERENCES = "MyPREFERENCES";
    int i = 0;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("notification", String.valueOf(remoteMessage.getData()));
        try {
        JSONObject jsonObject =new JSONObject(remoteMessage.getData());

            i++ ;

            SharedPreferences prfs = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
            Global.notificationcount = prfs.getInt("notivalue",0);
            Global.notificationcount++;

            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("notivalue", Global.notificationcount);
            editor.apply();
            System.out.println(Global.notificationcount);
            dbHelper.addNotification(jsonObject.getString("Title"),jsonObject.getString("Message"),jsonObject.getString("Date"));
            showNotification(jsonObject.getString("Message"),jsonObject.getString("Date"),jsonObject.getString("Title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(String Message , String Date , String Title) {



        NotificationCompat.BigTextStyle style=new NotificationCompat.BigTextStyle();
        style.bigText(Message);
        style.setBigContentTitle(Title);
        style.setSummaryText("Virtual Shop");

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        Intent intent =new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("menuFragment", "favoritesMenuItem");
                            intent.setAction("Notify");

                          NotificationCompat.Builder nbuilder=(NotificationCompat.Builder)new NotificationCompat.Builder(MessagingService.this)
                                .setSmallIcon(R.drawable.ic_ecommerce)
                                .setContentTitle(Title)
                                .setContentText(Message)
                                .setAutoCancel(true)
                                .setStyle(style).setBadgeIconType(R.drawable.ic_ecommerce)
                                .setPriority(Notification.PRIORITY_MAX)
                                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_ecommerce))
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setGroup("Complete Wallet")
                                .setTicker("Complete Wallet");


                            PendingIntent pendingIntent =PendingIntent.getActivity(getApplicationContext(),m,intent,0);
                            nbuilder.setContentIntent(pendingIntent);

                            //long[] vibrate = { 0, 100, 200, 300 };
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            nbuilder.setSound(alarmSound);
                         //   nbuilder.setVibrate(vibrate);
        nbuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        nbuilder.setLights(Color.RED, 3000, 3000);

        //Ton
                            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(m,nbuilder.build());
    }
}
