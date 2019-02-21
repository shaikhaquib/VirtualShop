package com.virtualshop.virtualshop.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.R;
import com.virtualshop.virtualshop.Config.SQLiteHandler;
import com.virtualshop.virtualshop.Config.SessionManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail ,edtPassword;
    String email,password;
    Button Login;
    ProgressDialog progressDialog ;
    SQLiteHandler db;
    SessionManager session;
    String SocialEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Call the function callInstamojo to start payment here

        progressDialog = new ProgressDialog(this);
        session = new SessionManager(getApplicationContext());
        db=new SQLiteHandler(this);

        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.lgPassword);
        Login = findViewById(R.id.btnLogin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).hide();
        }

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();

                boolean cancel= false;
                View focusView = null;

                if (TextUtils.isEmpty(email)){
                    edtEmail.setError(getString(R.string.error_field_required));
                    focusView = edtEmail;
                    cancel = true;
                } else if (TextUtils.isEmpty(password)){
                    edtPassword.setError(getString(R.string.error_field_required));
                    focusView = edtPassword;
                    cancel = true;
                }if (cancel) {
                    focusView.requestFocus();
                }
                else {
                    //      startActivity(new Intent(getApplicationContext(),MainActivity.class));

                }

                Aunthanticate();
            }
        });
        Context con;
/*
        try {

            con = createPackageContext("com.httpgnsbook.gnsbook", 0);
            SharedPreferences pref = con.getSharedPreferences(
                    "AndroidHiveLogin", Context.MODE_PRIVATE);
            String data = pref.getString("isLoggedIn", "No Value");
            Log.d("Not data shared","Test");

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Not data shared", e.toString());
        }
*/



    }

    public void Aunthanticate() {

        progressDialog.setMessage("Logging...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.LoginAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status"))
                    {
                        session.setLogin(true);
                        JSONObject data=jsonObject.getJSONObject("result");
                        db.addUser(
                                data.getString("customer_id"),
                                data.getString("referral_id"),
                                data.getString("email"),
                                data.getString("mobile"),
                                data.getString("name")+" "+data.getString("last_name"),
                                data.getString("added_time"),
                                data.getString("agent_status"),
                                data.getString("agent_id"));
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                    else
                    {
                        Global.diloge(LoginActivity.this,"Login Error" , jsonObject.getString("result"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> param = new HashMap<String,String> ();
                    param.put("email",edtEmail.getText().toString());
                    param.put("password",edtPassword.getText().toString());
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public void register(View view) {
        startActivity(new Intent(getApplicationContext(),SIgnUp.class));
    }

    public void forgotPass(View view) {
        startActivity(new Intent(getApplicationContext(), Forgotpass.class));
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }


    private void getGnsdata() {
        ArrayList<String> dataSet = new ArrayList<>();

        ContentResolver resolver = getContentResolver();

        Uri uri = Uri.parse("content://com.gnsbook.digital.provider");
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);

       if ((cursor != null) && (cursor.getCount() > 0)) {

            while (cursor.moveToNext()) {
                SocialEmail = cursor.getString(cursor.getColumnIndex("email"));
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(LoginActivity.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.gnsuser, null);
                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);

                Button button = mView.findViewById(R.id.btnGnsUser);
                TextView gnsuseranother = mView.findViewById(R.id.gnsuseranother);
                button.setText("Continue as "+cursor.getString(cursor.getColumnIndex("name")));

                builder.setView(mView);
                final android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SocialLogin();
                        alert.dismiss();
                    }
                });
                gnsuseranother.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GNS_AUTH();
                        alert.dismiss();
                    }
                });
            }


          //  dataSet.add(""+lat + ", " + lng +"\n"+imei);
        }else {

           // No Data available or No user has login in GnsBook
           GNS_AUTH();

       }

      // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataSet);
       // ((ListView)findViewById(R.id.listContacts)).setAdapter(adapter);
    }

    public void Login(View view) {
        GNS_AUTH();


       /* boolean isAppInstalled = appInstalledOrNot("com.httpgnsbook.gnsbook");

        if(isAppInstalled) {

            getGnsdata();


            Log.d("already installed.","True");
        } else {
            // Do whatever we want to do if application not installed
            // For example, Redirect to play store

            Log.d("not installed.","False");
        }*/
    }


    public void GNS_AUTH() {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(LoginActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.gns_login, null);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);

        final  EditText edtEmail ,edtPassword;
        final String email,password;

        Button Login;
        edtEmail = mView.findViewById(R.id.email);
        edtPassword = mView.findViewById(R.id.lgPassword);
        Login = mView.findViewById(R.id.btnLogin);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = edtEmail.getText().toString();
                final String password = edtPassword.getText().toString();

                boolean cancel= false;
                View focusView = null;

                if (TextUtils.isEmpty(email)){
                    edtEmail.setError(getString(R.string.error_field_required));
                    focusView = edtEmail;
                    cancel = true;
                } else if (TextUtils.isEmpty(password)){
                    edtPassword.setError(getString(R.string.error_field_required));
                    focusView = edtPassword;
                    cancel = true;
                }if (cancel) {
                    focusView.requestFocus();
                }
                else {
                    //    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    GnsAunthanticate(email ,password);
                    //    startActivity(new Intent(getActivity(),MainActivity.class));
                }
            }
        });


        builder.setView(mView);
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

    }
    private void GnsAunthanticate(final String stremail,final String strpassword) {


        progressDialog.show();
        progressDialog.setMessage("Authenticating....");
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.GNSLoginAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        JSONObject data = jsonObject.getJSONObject("result");

                        SocialEmail = data.getString("email");
                        SocialLogin();


                    }                    else
                    {
                        Global.failedDilogue(LoginActivity.this, jsonObject.getString("result"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> param = new HashMap<String,String>();
                param.put("email",stremail);
                param.put("password",strpassword);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public void SocialLogin() {

        progressDialog.setMessage("Logging...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.social_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status"))
                    {
                        session.setLogin(true);
                        JSONObject data=jsonObject.getJSONObject("result");
                        db.addUser(
                                data.getString("customer_id"),
                                data.getString("referral_id"),
                                data.getString("email"),
                                data.getString("mobile"),
                                data.getString("name")+" "+data.getString("last_name"),
                                data.getString("added_time"),
                                data.getString("agent_status"),
                                data.getString("agent_id"));
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                    else
                    {
                        Global.diloge(LoginActivity.this,"Login Error" , jsonObject.getString("result"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> param = new HashMap<String,String> ();
                param.put("email",SocialEmail);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

}
