package com.virtualshop.virtualshop.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Firebase.Fcm;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.R;

import org.json.JSONArray;
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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SIgnUp extends AppCompatActivity {

    Spinner spnGen ;
    String[] genArray = {"MALE" , "FEMALE"} ;
    LinearLayout prDetail ,addressDeatil;
    Button prfNext,Submit,previous;
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    int intGen=0;
    
    EditText edtfname ,edtlname ,edtemail,edtphone,edtreferral,edtaddress,edtpassword,edtcity,edtpin;
    String fname ,lname ,email,phone,referral,address,password,city,pin,gender = "M";

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);

        //Global.hideKeyboard(this);

        prDetail = findViewById(R.id.spProfileDetaile);
        addressDeatil = findViewById(R.id.addDetail);
        prfNext = findViewById(R.id.spNext);
        spnGen = findViewById(R.id.spGender);
        edtfname = findViewById(R.id.fname);
        edtlname = findViewById(R.id.lname);
        edtemail = findViewById(R.id.email);
        edtphone = findViewById(R.id.mobile);
        edtreferral = findViewById(R.id.referralid);
        edtaddress = findViewById(R.id.address);
        edtpassword = findViewById(R.id.password);
        edtcity = findViewById(R.id.city);
        edtpin = findViewById(R.id.pincode);
        Submit = findViewById(R.id.spsignup);
        previous = findViewById(R.id.previous);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, genArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spnGen.setAdapter(spinnerArrayAdapter);
        
        spnGen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                intGen = i ;
                System.out.println("Gender"+intGen);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        prfNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                fname = edtfname.getText().toString();
                lname = edtlname.getText().toString();
                email = edtemail.getText().toString();
                phone = edtphone.getText().toString();
                password = edtpassword.getText().toString();
                referral = edtreferral.getText().toString();

                profileValidation();
                
              
            }
        });


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                address = edtaddress.getText().toString();
                city = edtcity.getText().toString();
                pin = edtpin.getText().toString();



            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prDetail.setVisibility(View.VISIBLE);
                addressDeatil.setVisibility(View.GONE);
            }
        });
    }



    private void profileValidation() {

        boolean cancel= false;
        View focusView = null;

            Pattern pattern1=Pattern.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
            Matcher matcher1=pattern1.matcher(email);
            String regx = "^[\\p{L} .'-]+$";
            Pattern paname =Pattern.compile(regx);
            Matcher matchfname=paname.matcher(fname);
            Matcher matchlname=paname.matcher(lname);


            if (intGen == 0){
                gender = "M" ;
            }else {
                gender = "F" ;
            }


            if (TextUtils.isEmpty(fname)) {
                edtfname.setError(getString(R.string.error_field_required));
                focusView = edtfname;
                cancel = true;
            }else if (!matchfname.matches()) {
                edtfname.setError("Enter First Name");
                focusView = edtfname;
                cancel = true;
            }else if (TextUtils.isEmpty(lname)) {
                edtlname.setError(getString(R.string.error_field_required));
                focusView = edtlname;
                cancel = true;
            }else if (!matchlname.matches()) {
                edtlname.setError("Enter Last Name");
                focusView = edtlname;
                cancel = true;
            }else if (TextUtils.isEmpty(email)) {
                edtemail.setError(getString(R.string.error_field_required));
                focusView = edtemail;
                cancel = true;
            }else if (!matcher1.matches() && !email.isEmpty()) {
                edtemail.setError("Enter Valid Email");
                focusView = edtemail;
                cancel = true;
            }else if (TextUtils.isEmpty(phone)) {
                edtphone.setError(getString(R.string.error_field_required));
                focusView = edtphone;
                cancel = true;
            }else if (phone.length() < 10) {
                edtphone.setError("Enter valid mobile no");
                focusView = edtphone;
                cancel = true;
            }else if (TextUtils.isEmpty(password)) {
                edtpassword.setError(getString(R.string.error_field_required));
                focusView = edtpassword;
                cancel = true;
            }else if (password.length() <= 5) {
                edtpassword.setError("Password length must be greater than or equal to 6");
                focusView = edtpassword;
                cancel = true;
            }if (cancel) {
                focusView.requestFocus();
            }
            else {
            Authenticate();
            }


    }


    private void Authenticate() {

        progressDialog.setTitle("Submitting...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.RegisterAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status"))
                    {

                        new Fcm().execute(edtreferral.getText().toString(),"Mr/Miss "+edtfname.getText().toString()+ " has joined with your reference. Whenever he/she play any game you will be rewarded." ,"Congratulation");
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(SIgnUp.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(SIgnUp.this);
                        }
                        builder.setCancelable(false);
                        builder.setTitle("Successfully Registered")
                                .setMessage("You have successfully registered. Your login detail and password has been send on your registered Mobile no and   Email address.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(SIgnUp.this,LoginActivity.class));
                                    }
                                })
                                .show();
                    }
                    else {
                        JSONObject json = new JSONObject(response);

                        if (json.has("result")) {

                            JSONObject dataObject = json.optJSONObject("result");

                            if (dataObject != null) {

                                JSONObject error =jsonObject.getJSONObject("result");

                                if (error.has("email") && error.has("mobile")){
                                    Global.diloge(SIgnUp.this,"Signup Error" , error.getJSONArray("email").getString(0) + "\n" + error.getJSONArray("mobile").getString(0) );
                                }
                                else  if (error.has("email"))
                                {
                                    Global.diloge(SIgnUp.this,"Signup Error" , error.getJSONArray("email").getString(0));
                                }
                                else  if (error.has("mobile"))
                                {
                                    Global.diloge(SIgnUp.this,"Signup Error" , error.getJSONArray("mobile").getString(0));
                                }else {
                                    Global.diloge(SIgnUp.this,"Signup Error" , jsonObject.getString("result"));
                                }
                            } else {

                                JSONArray array = json.optJSONArray("data");
                                Global.diloge(SIgnUp.this,"Signup Error" , jsonObject.getString("result"));



                                //Do things with array
                            }
                        } else {
                            // Do nothing or throw exception if "data" is a mandatory field
                        }


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
                String statusCode = String.valueOf(error.networkResponse.statusCode);
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
                param.put("first_name",fname);
                param.put("last_name",lname);
                param.put("email",email);
                param.put("password",password);
                param.put("mobile",phone);
                param.put("sex",gender);
                param.put("address","null");
                param.put("city","null");
                param.put("pincode","000000");
                param.put("referral_id",referral);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }
    private void Social_Login() {

        progressDialog.setTitle("Submitting...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.social_signup, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status"))
                    {

                        new Fcm().execute(edtreferral.getText().toString(),"Mr/Miss "+edtfname.getText().toString()+ " has joined with your reference. Whenever he/she play any game you will be rewarded." ,"Congratulation");
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(SIgnUp.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(SIgnUp.this);
                        }
                        builder.setCancelable(false);
                        builder.setTitle("Successfully Registered")
                                .setMessage("You have successfully registered. Your login detail and password has been send on your registered Mobile no and   Email address.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(SIgnUp.this,LoginActivity.class));
                                    }
                                })
                                .show();
                    }
                    else {
                        JSONObject json = new JSONObject(response);

                        if (json.has("result")) {

                            JSONObject dataObject = json.optJSONObject("result");

                            if (dataObject != null) {

                                JSONObject error =jsonObject.getJSONObject("result");

                                if (error.has("email") && error.has("mobile")){
                                    Global.diloge(SIgnUp.this,"Signup Error" , error.getJSONArray("email").getString(0) + "\n" + error.getJSONArray("mobile").getString(0) );
                                }
                                else  if (error.has("email"))
                                {
                                    Global.diloge(SIgnUp.this,"Signup Error" , error.getJSONArray("email").getString(0));
                                }
                                else  if (error.has("mobile"))
                                {
                                    Global.diloge(SIgnUp.this,"Signup Error" , error.getJSONArray("mobile").getString(0));
                                }else {
                                    Global.diloge(SIgnUp.this,"Signup Error" , jsonObject.getString("result"));
                                }
                            } else {

                                JSONArray array = json.optJSONArray("data");
                                Global.diloge(SIgnUp.this,"Signup Error" , jsonObject.getString("result"));



                                //Do things with array
                            }
                        } else {
                            // Do nothing or throw exception if "data" is a mandatory field
                        }


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
//                String statusCode = String.valueOf(error.networkResponse.statusCode);
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
                param.put("first_name",fname);
                param.put("last_name",lname);
                param.put("email",email);
                param.put("password",password);
                param.put("mobile",phone);
                param.put("sex",gender);
                param.put("address","null");
                param.put("city","null");
                param.put("pincode","000000");
                param.put("referral_id",referral);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public void GNS_Signup(View view) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(SIgnUp.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.gns_login, null);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SIgnUp.this);

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
                    Aunthanticate(email ,password);
                    //    startActivity(new Intent(getActivity(),MainActivity.class));
                }
            }
        });


        builder.setView(mView);
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();

    }

    private void Aunthanticate(final String stremail,final String strpassword) {


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
                        fname = data.getString("name");
                        lname = data.getString("last_name");
                        email = data.getString("email");
                        phone = data.getString("mobile");
                        password = getRandomString();
                        referral = data.getString("referral_id");
                        Social_Login();

                    }                    else
                    {
                        Global.failedDilogue(SIgnUp.this, jsonObject.getString("result"));
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

    private static String getRandomString()
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(8);
        for(int i=0;i<8;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

}
