package com.virtualshop.virtualshop.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Firebase.Fcm;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Forgotpass extends AppCompatActivity {

    Button rstButton;
    EditText edtEmail;
    String Email;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);
        getSupportActionBar().hide();

        rstButton = findViewById(R.id.rstPassword);
        edtEmail = findViewById(R.id.fgpEmail);
        progressDialog =new ProgressDialog(Forgotpass.this);

        ActionBar abar = getSupportActionBar();
        Global.actionbar(Forgotpass.this ,abar ,"FORGOT PASSWORD");


        rstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean cancel= false;
                View focusView = null;

                Email = edtEmail.getText().toString();
                Pattern pattern1=Pattern.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
                Matcher matcher1=pattern1.matcher(Email);


                if (TextUtils.isEmpty(Email)) {
                    edtEmail.setError(getString(R.string.error_field_required));
                    focusView = edtEmail;
                    cancel = true;
                }else if (!matcher1.matches()) {
                    edtEmail.setError("Enter Valid Email");
                    focusView = edtEmail;
                    cancel = true;
                }if (cancel) {
                    focusView.requestFocus();
                }
                else {
                    //Authenticate();
                    new transferAPI().execute(edtEmail.getText().toString());
                }

            }
        });
    }



    private class transferAPI extends AsyncTask<String , String ,String> {
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Submitting...");
            progressDialog.setCancelable(true);
            progressDialog.show();        }

        @Override
        protected String doInBackground(String... params) {


            try {
                url = new URL(APIs.frgpass);
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
                        .appendQueryParameter("email", params[0]);

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
                    System.out.println("Result "+result);
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
            progressDialog.dismiss();

            try {

                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("status"))
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(Forgotpass.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(Forgotpass.this);
                    }
                    builder.setCancelable(false);
                    builder.setTitle("Success")
                            .setMessage(jsonObject.getString("result"))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(Forgotpass.this,LoginActivity.class));
                                }
                            })
                            .show();
                }
                else
                {

                    Global.diloge(Forgotpass.this,"Error" , jsonObject.getString("result"));


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

/*
    private void Authenticate() {

        progressDialog.setTitle("Submitting...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.frgpass, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status"))
                    {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(Forgotpass.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(Forgotpass.this);
                        }
                        builder.setCancelable(false);
                        builder.setTitle("Success")
                                .setMessage(jsonObject.getString("result"))
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(Forgotpass.this,LoginActivity.class));
                                    }
                                })
                                .show();
                    }
                    else
                    {

                            Global.diloge(Forgotpass.this,"Error" , jsonObject.getString("result"));


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                String body = null;
                //get status code here
            //    String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                if(error.networkResponse.data!=null) {
                    try {
                        body = new String(error.networkResponse.data,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    System.out.println(body);
                }


            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> param = new HashMap<String,String>();
                param.put("email",Email);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }
*/


}
