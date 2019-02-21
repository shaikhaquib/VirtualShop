package com.virtualshop.virtualshop.Firebase;

import android.net.Uri;
import android.os.AsyncTask;

import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Model.Global;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Shaikh Aquib on 15-May-18.
 */

public class Fcm extends AsyncTask<String , String ,String> {
    HttpURLConnection conn;
    URL url = null;


    @Override
    protected String doInBackground(String... params) {


        try {
            url = new URL(APIs.fcm);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "exception";
        }
        try {
            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(Global.READ_TIMEOUT);
            conn.setConnectTimeout(Global.CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");

            // setDoInput and setDoOutput method depict handling of both send and receive
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Append parameters to URL
            Uri.Builder builder = new Uri.Builder()

                    .appendQueryParameter("customer_id", params[0])
                    .appendQueryParameter("title", params[1])
                    .appendQueryParameter("message", params[2]);
            // / .appendQueryParameter("ifsc", params[5]);
            String query = builder.build().getEncodedQuery();

            // Open connection for sending data
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return "exception";
        }

        try {

            int response_code = conn.getResponseCode();

            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Pass data to onPostExecute method
                return(result.toString());

            }else{

                return("unsuccessful");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "exception";
        } finally {
            conn.disconnect();
        }


    }

    @Override
    protected void onPostExecute(String s) {

        //  Toast.makeText(AddBeneficiary.this, s, Toast.LENGTH_SHORT).show();

    }


}
