package com.virtualshop.virtualshop.Activity;

import android.app.Activity;
import instamojo.library.InstapayListener;
import instamojo.library.InstamojoPay;
import instamojo.library.Config;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.IntentFilter;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;


import com.virtualshop.virtualshop.R;
import com.virtualshop.virtualshop.sdk.JivoDelegate;
import com.virtualshop.virtualshop.sdk.JivoSdk;

import java.util.Locale;

//************************

//**********
public class JivoActivity extends Activity implements JivoDelegate {

    //**************
    JivoSdk jivoSdk;

    
    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", true);
            pay.put("send_email", true);
 } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }
    
    InstapayListener listener;

    
    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jivo);
        // Call the function callInstamojo to start payment here

        String lang = Locale.getDefault().getLanguage().indexOf("ru") >= 0 ? "ru": "en";

      //  callInstamojoPay("A@gmail.com","8425918611","500","test","Aquib");
        //*********************************************************
        jivoSdk = new JivoSdk((WebView) findViewById(R.id.webview), lang);
        jivoSdk.delegate = this;
        jivoSdk.prepare();
    }

    //*********************************************
    @Override
    public void onEvent(String name, String data) {
        if(name.equals("url.click")){
            if(data.length() > 2){
                String url = data.substring(1, data.length() - 1);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        }
    }


}
