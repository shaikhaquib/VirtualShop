package com.virtualshop.virtualshop.Firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Shaikh Aquib on 07-May-18.
 */

//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";



    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //calling the method store token and passing token
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        //we will save the token in sharedpreferences later

    }
}