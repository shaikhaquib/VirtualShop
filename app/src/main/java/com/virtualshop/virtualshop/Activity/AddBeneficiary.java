package com.virtualshop.virtualshop.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;


/**
 * Created by Shaikh Aquib on 10-Apr-18.
 */


public class AddBeneficiary extends AppCompatActivity {

    EditText Name, Mobile, Accountno, Ifsc;
    Button AddBenificiarybtn;
    boolean cancel= false;
    View focusView = null;
    ProgressDialog dialog;
    static PinEntryEditText pinEntry;
    ProgressBar OtpProgress;
    String otp , strSMS ,strEMAIL;
    String benid , uniqueid ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beneficiary);
        getSupportActionBar().hide();


        InitializeObject();
        onTouchField();
        Validation();
        onSubmit();
    }

    private void onSubmit() {
        AddBenificiarybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Name.getText().toString().equals("")){
                    Name.setError("Please Enter Name");
                    focusView = Name;
                    Name.requestFocus();
                    cancel = true;
                }else
                if (Mobile.getText().toString().equals("")){
                    Mobile.setError("Please Enter Mobile No");
                    focusView = Mobile;
                    Mobile.requestFocus();
                    cancel = true;
                }else
                if (Accountno.getText().toString().equals("")){
                    Accountno.setError("Please Enter AccountNo");
                    focusView = Accountno;
                    Accountno.requestFocus();
                    cancel = true;
                }else if (Ifsc.getText().toString().equals("")){
                    Ifsc.setError("Please Enter Amount Code");
                    focusView = Ifsc;
                    Ifsc.requestFocus();
                    cancel = true;
                }else {
                    new addAsync().execute(Global.customerid,Global.agentid,Name.getText().toString(),Mobile.getText().toString(),Accountno.getText().toString(),Ifsc.getText().toString());
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onTouchField() {
        Mobile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (Name.getText().toString().equals("")){
                    Name.setError("Please Enter UserName");
                    focusView = Name;
                    Name.requestFocus();
                    cancel = true;
                }else {
                    focusView = Mobile;
                    Mobile.requestFocus();
                    cancel = true;
                }
                return true;
            }
        });

        Accountno.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (Name.getText().toString().equals("")){
                    Name.setError("Please Enter UserName");
                    focusView = Name;
                    Name.requestFocus();
                    cancel = true;
                }else
                if (Mobile.getText().toString().equals("")){
                    Mobile.setError("Please Enter STDcode");
                    focusView = Mobile;
                    Mobile.requestFocus();
                    cancel = true;
                }
                else if (Mobile.getText().toString().length() != 10){
                    Mobile.setError("Enter valid mobil no");
                    Mobile.requestFocus();
                }
                else {
                    focusView = Accountno;
                    Accountno.requestFocus();
                    cancel = true;
                }
                return true;
            }
        });

        Ifsc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (Name.getText().toString().equals("")){
                    Name.setError("Please Enter UserName");
                    focusView = Name;
                    Name.requestFocus();
                    cancel = true;
                }else
                if (Mobile.getText().toString().equals("")){
                    Mobile.setError("Please Enter STDcode");
                    focusView = Mobile;
                    Mobile.requestFocus();
                    cancel = true;
                }else if (Mobile.getText().toString().length() != 10){
                    Mobile.setError("Enter valid mobil no");
                    Mobile.requestFocus();
                }
                else
                if (Accountno.getText().toString().equals("")){
                    Accountno.setError("Please Enter AccountNo");
                    focusView = Accountno;
                    Accountno.requestFocus();
                    cancel = true;
                }else {
                    focusView = Ifsc;
                    Ifsc.requestFocus();
                    cancel = true;
                }


                return true;
            }
        });


    }

    private void Validation() {
        Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                Pattern paname =Pattern.compile("^[a-zA-Z]+$");
                Matcher matchname=paname.matcher(s);

                if (!matchname.matches()) {
                    Name.setError("Enter valid Name");
                    AddBenificiarybtn.setEnabled(false);
                    focusView = Name;
                    cancel = true;
                }else {
                    AddBenificiarybtn.setEnabled(true);
                }

              }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println(s);
                if (s.toString().contains(" ")) {
                    Name.setError("No Spaces Allowed");
                    AddBenificiarybtn.setEnabled(false);
                    focusView = Name;
                    cancel = true;
                }else {
                    AddBenificiarybtn.setEnabled(true);
                }
            }
        });

        Ifsc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                Pattern paname =Pattern.compile("^[A-Za-z]{4}[a-zA-Z0-9]{7}$");
                Matcher matchname=paname.matcher(s);

                if (!matchname.matches()) {
                    Ifsc.setError("Enter valid Ifsc");
                    AddBenificiarybtn.setEnabled(false);
                    focusView = Ifsc;
                    cancel = true;
                }else {
                    AddBenificiarybtn.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println(s);
            }
        });


    }

    private void InitializeObject() {
        dialog = new ProgressDialog(AddBeneficiary.this);
        Name=findViewById(R.id.bnfName);
        Mobile=findViewById(R.id.bnfMobile);
        Accountno=findViewById(R.id.bnfAccountno);
        Ifsc=findViewById(R.id.bnfifsc);
        AddBenificiarybtn =findViewById(R.id.Addbnf);
    }

    private void showProgress(){
        dialog.setMessage("Wait a second...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(new Intent(getApplicationContext(), MainActivity.class)));

    }

    private void dissmissProgrss(){
        dialog.dismiss();
    }



    class addAsync extends AsyncTask<String , String ,String> implements OTPListener {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected String doInBackground(String... params) {


            try {


                url = new URL(APIs.Addebenificiary);

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
                        .appendQueryParameter("agent_id", params[1])
                        .appendQueryParameter("beneficiary_name", params[2])
                        .appendQueryParameter("beneficiary_mobile", params[3])
                        .appendQueryParameter("beneficiary_account_no", params[4])
                        .appendQueryParameter("beneficiary_ifsc", params[5]);
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
            dissmissProgrss();

            try {
                JSONObject object = new JSONObject(s);
              //  Toast.makeText(AddBeneficiary.this, s, Toast.LENGTH_SHORT).show();


                if (object.getBoolean("status")){

               //     Global.diloge(AddBeneficiary.this,"Result",s);

                    System.out.println(s);
                    String s1 =object.getString("result");

                    JSONObject jsonObject=new JSONObject(s1);
                    benid=jsonObject.getString("beneficiary_id");
                    uniqueid=jsonObject.getString("unique_id");

                    OtpReader.bind(this,"VM-iMONEY");
                    OtpReader.bind(this,"IM-iMONEY");
                    OtpReader.bind(this,"AD-iMONEY");
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(AddBeneficiary.this);
                    View mView = layoutInflaterAndroid.inflate(R.layout.confimotp, null);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(AddBeneficiary.this);
                    alertDialogBuilderUserInput.setView(mView);

                    pinEntry = mView.findViewById(R.id.otp);
                    OtpProgress=mView.findViewById(R.id.otpProgresss);
                    final Button VrOtp=mView.findViewById(R.id.vrotp);
                    final TextView Resend=mView.findViewById(R.id.resendotp);



                    if (pinEntry != null) {
                        pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                            @Override
                            public void onPinEntered(CharSequence str){
                                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(AddBeneficiary.this.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                otp =  str.toString();
                                new ConfirmOTP().execute(Global.customerid  ,Global.agentid,benid,uniqueid,otp);

                            }
                        });
                    }
                    final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();

                    VrOtp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                           /* if (otp.length() == 4){
                                new ConfirmOTP().execute(Global.token,Global.userId,otp);}
                            else{
                                Toast.makeText(getActivity(), "Please Enter Proper OTP", Toast.LENGTH_SHORT).show();
                            }*/
                        }
                    });

                    Resend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ResendOTP();
                        }
                    });

                    alertDialogAndroid.show();


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void otpReceived(String messageText) {
            OtpProgress.setVisibility(View.GONE);
            String numberOnly= messageText.replaceAll("[^0-9]", "");
            pinEntry.setText(numberOnly);
        }
    }

    private void ResendOTP() {
        showProgress();
        StringRequest request =new StringRequest(StringRequest.Method.POST, APIs.reSendOtp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            //    Toast.makeText(AddBeneficiary.this, response, Toast.LENGTH_SHORT).show();
                dissmissProgrss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dissmissProgrss();
                final Dialog dialog = new Dialog(AddBeneficiary.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.connectionerror);
                Button button = dialog.findViewById(R.id.btnOK);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                });

                dialog.show();            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("customer_id", Global.customerid);
                params.put("agentid", Global.agentid);
                params.put("beneficiary_id", benid);
                params.put("unique_id", uniqueid);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(request);
    }

    class ConfirmOTP extends AsyncTask<String , String ,String> {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected String doInBackground(String... params) {


            try {


                url = new URL(APIs.Addebenificiaryotp);

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
                        .appendQueryParameter("agent_id", params[1])
                        .appendQueryParameter("beneficiary_id", params[2])
                        .appendQueryParameter("unique_id", params[3])
                        .appendQueryParameter("otp", params[4]);
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
            dissmissProgrss();

          //  Toast.makeText(AddBeneficiary.this, s, Toast.LENGTH_SHORT).show();

            try {
                JSONObject jsonObject =new JSONObject(s);
                if (jsonObject.getBoolean("status")){
                   /// startActivity(new Intent(AddBeneficiary.this,BenificiaryList.class));
                    //Toast.makeText(AddBeneficiary.this, jsonObject.getString("result"), Toast.LENGTH_LONG).show();
                    successDilogue(jsonObject.getString("result"),AddBeneficiary.this);
                }else {
                    //Toast.makeText(AddBeneficiary.this, jsonObject.getString("result"), Toast.LENGTH_LONG).show();
                    failedDilogue(jsonObject.getString("result"),AddBeneficiary.this);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }




        }


    }

    private void successDilogue(String result, AddBeneficiary recharge) {
        final Dialog dialog = new Dialog(AddBeneficiary.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.success_dilogue);
        Button button = dialog.findViewById(R.id.btnsucces);
        TextView textView = dialog.findViewById(R.id.successtext);
        textView.setText(result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),BenificiaryList.class));
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void failedDilogue(String result, AddBeneficiary recharge) {
        final Dialog dialog1 = new Dialog(AddBeneficiary.this);
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.failediloge);
        Button button = dialog1.findViewById(R.id.btnfailed);
        TextView textView = dialog1.findViewById(R.id.failedreson);
        textView.setText(result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // dialog.show();
        dialog1.getWindow().setAttributes(lp);
    }

}
