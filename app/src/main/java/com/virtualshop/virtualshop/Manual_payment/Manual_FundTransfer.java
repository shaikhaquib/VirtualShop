package com.virtualshop.virtualshop.Manual_payment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.virtualshop.virtualshop.Activity.LoadMoney;
import com.virtualshop.virtualshop.Activity.MainActivity;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shaikh Aquib.
*/

public class Manual_FundTransfer extends AppCompatActivity {

    EditText accountno ,ifsc, amount , surcharge,tdscharge,admincharge , total ;
    String stracc ,strifsc ;
    RequestQueue queue;
    ProgressDialog dialog;
    TextView consume ,remain,withdrawBalance ,txtError;
    LinearLayout layout;
    CircularProgressBar consumbar , remainbar ,balancebar ;
    Button Transfer;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_transfer);
        getSupportActionBar().hide();

      initialize();
      getExtra();
      webService();

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("edited text", editable.toString());



                if (!editable.toString().equals("")) {
                    int i = Integer.parseInt(editable.toString());
                    double entamount = Double.parseDouble(editable.toString());
                    if (Integer.parseInt(amount.getText().toString()) < 5000 && Integer.parseInt(amount.getText().toString()) > 5050)
                    {
                        amount.setError("Amount must be less than or equal to 5000 - 5050");
                        surcharge.setText("0.0");
                        tdscharge.setText("0.0");
                        admincharge.setText("0.0");
                        total.setText("0.0");
                }else {
                        double amount = Double.parseDouble(editable.toString());
                        double res = (amount / 100.0f) * 1.5;
                        double tds = (entamount / 100.0f) * 5;
                        double Admincharge = (entamount / 100.0f) * 5;


                        surcharge.setText(res + "");
                        tdscharge.setText(tds + "");
                        admincharge.setText(Admincharge + "");
                        double totalamt = res+tds+Admincharge-amount;
                        total.setText(String.valueOf(Math.abs(totalamt)));
                    }
                } else{
                    surcharge.setText("0");
                    total.setText("0");
                }}

        });
        balance();
    }

    private void balance() {
        ShowProgress("Processing...");
        StringRequest request =new StringRequest(Request.Method.POST, APIs.Balance, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject =new JSONObject(response);
                    withdrawBalance.setText(jsonObject.getString("customer_balance"));

                    double balance , jolobalance;

                    balance = Double.parseDouble(jsonObject.getString("customer_balance"));
                    jolobalance = Double.parseDouble(jsonObject.getString("jolo_api_balance"));
                    layout.setVisibility(View.VISIBLE);
                    txtError.setVisibility(View.GONE);


                  /*  if ( balance >= 115 && jolobalance >= 1000){
                        layout.setVisibility(View.VISIBLE);
                        txtError.setVisibility(View.GONE);

                    }else {layout.setVisibility(View.GONE);
                        txtError.setVisibility(View.VISIBLE);}*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DissProgress();
                Toast.makeText(getApplicationContext(), "Connection problem please try agin later!", Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("customer_id", Global.customerid);

                return params;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

    }

    private void webService() {

        ShowProgress("Loading data...");

        StringRequest request = new StringRequest(Request.Method.POST, APIs.Benificiarylist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DissProgress();
                //Toast.makeText(FundTransfer.this, response, Toast.LENGTH_SHORT).show();
                try {

                    JSONObject object =new JSONObject(response);

                    JSONObject result = object.getJSONObject("result");
                    JSONObject data =result.getJSONObject("fulldata");
                    JSONArray array=data.getJSONArray("remitter_limit");
                    JSONObject remitter_limit = array.getJSONObject(0);
                    JSONObject jsonObject = remitter_limit.getJSONObject("limit");
                    remain.setText(jsonObject.getString("remaining"));
                    consume.setText(jsonObject.getString("consumed"));

                    float remainvalue = Float.parseFloat(jsonObject.getString("remaining"));
                    float Consumevalue = Float.parseFloat(jsonObject.getString("consumed"));
                    float Totalvalue = Float.parseFloat(jsonObject.getString("total"));

                    float remainpercent = (remainvalue/75000)*100;
                    float consumepercent = (Consumevalue/75000)*100;

                    remainbar.setProgress(remainpercent);
                    consumbar.setProgress(consumepercent);

                    float balance = Float.parseFloat(jsonObject.getString("customer_balance"));

                    float prgbalance = (balance/100000)*100;

                    consumbar.setProgress(prgbalance);



/*
                    for (int i = 0 ; i <= array.length() ; i++){
                    }
*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DissProgress();
                final Dialog dialog = new Dialog(Manual_FundTransfer.this);
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

                dialog.show();
            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("mobile", Global.mobile);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    private void getExtra() {
        stracc=getIntent().getStringExtra("accountno");
        strifsc=getIntent().getStringExtra("ifsc");

        accountno.setText(stracc);
        ifsc.setText(strifsc);
    }

    private void initialize() {

        queue = Volley.newRequestQueue(this);
        dialog=new ProgressDialog(Manual_FundTransfer.this);
        progressDialog=new ProgressDialog(Manual_FundTransfer.this);
        accountno=findViewById(R.id.funAccount);
        ifsc=findViewById(R.id.funIfsc);
        amount=findViewById(R.id.fundamount);
        surcharge=findViewById(R.id.fundsurcharge);
        tdscharge=findViewById(R.id.tds);
        admincharge=findViewById(R.id.admincharge);
        total=findViewById(R.id.fundToatal);
        consume=findViewById(R.id.consumlimit);
        balancebar=findViewById(R.id.balanecustomer);
        consumbar=findViewById(R.id.consumprogrss);
        withdrawBalance=findViewById(R.id.withBalance);
        remain=findViewById(R.id.remainlimit);
        remainbar=findViewById(R.id.remainProgress);
        Transfer=findViewById(R.id.btnTrans);
        layout = findViewById(R.id.fundlayout);
        txtError = findViewById(R.id.txtError);

        ImageView icaddmoney = findViewById(R.id.icaddmoneytr);
        icaddmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoneytoVS();
            }
        });


        amount.setFocusable(false);
        amount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                amount.setFocusableInTouchMode(true);
                return false;
            }
        });

        Transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount.getText().toString().isEmpty()){
                    amount.setError("Enter valid amount");
                    amount.requestFocus();
                }else if (Integer.parseInt(amount.getText().toString()) < 5000 && Integer.parseInt(amount.getText().toString()) > 5050){
                    amount.setError("Amount must be greater than or equal to 5000 - 5050");
                    amount.requestFocus();
                }else{
                  /*  params.put("amount", );
                    params.put("mobile");
                    params.put("beneficiary_id");
                    params.put("customer_id");*/
                    new transferAPI().execute(amount.getText().toString(),Global.mobile,getIntent().getStringExtra("Bid"), Global.customerid);
                }
            }
        });

    }


    public void ShowProgress(String s){
        dialog.setMessage(s);
        dialog.setCancelable(false);
        dialog.show();
    }
    public void DissProgress(){
        dialog.dismiss();
    }

    private void successDilogue(String result, Manual_FundTransfer recharge) {
        final Dialog dialog = new Dialog(Manual_FundTransfer.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.success_dilogue);
        Button button = dialog.findViewById(R.id.btnsucces);
        TextView textView = dialog.findViewById(R.id.successtext);
        textView.setText(result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void failedDilogue(String result, Manual_FundTransfer recharge) {
        final Dialog dialog = new Dialog(Manual_FundTransfer.this);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.failediloge);
        Button button = dialog.findViewById(R.id.btnfailed);
        TextView textView = dialog.findViewById(R.id.failedreson);
        textView.setText(result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
finish();            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(new Intent(getApplicationContext(), MainActivity.class)));

    }

    private class transferAPI extends AsyncTask<String , String ,String> {
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ShowProgress("Processing...");
        }

        @Override
        protected String doInBackground(String... params) {


            try {
                url = new URL(APIs.Manual_FundTransAPi);
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
                        .appendQueryParameter("amount", params[0])
                        .appendQueryParameter("mobile", params[1])
                        .appendQueryParameter("beneficiary_id", params[2])
                        .appendQueryParameter("customer_id", params[3]);
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
            DissProgress();
           // String s = null;
            try {
                JSONObject jsonObject =new JSONObject(s);

                if (jsonObject.getBoolean("status")){
                        /*snackbar= Snackbar.make(layout,"Recharged Successfully",Snackbar.LENGTH_LONG);
                        snackbar.show();*/

                    successDilogue( jsonObject.getString("result"), Manual_FundTransfer.this);
                   new Fcm().execute(Global.customerid,"Money Transfer Success",jsonObject.getString("result"));

                }else {

                    failedDilogue(jsonObject.getString("result"), Manual_FundTransfer.this);
                    new Fcm().execute(Global.customerid,"Money Transfer failed",jsonObject.getString("result"));

                       /* snackbar = Snackbar.make(layout, jsonObject.getString("result"), Snackbar.LENGTH_LONG);
                        snackbar.show();*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }
    private void addMoneytoVS() {



        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Manual_FundTransfer.this);
        builderSingle.setIcon(R.drawable.ic_invoice);
        builderSingle.setTitle("Select Transection Mode:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Manual_FundTransfer.this, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.add("UPI / NEFT / IMPS /Other..");
        arrayAdapter.add("Debit Card / Credit Card / Wallet");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                if (strName.equals("UPI / NEFT / IMPS /Other..")){

                    //Dialoge for payment detail
                    final Dialog dialog1 = new Dialog(Manual_FundTransfer.this);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog1.setCancelable(false);
                    dialog1.setContentView(R.layout.manualtransfrom);
                    Button submit = dialog1.findViewById(R.id.submit);
                    ImageView close = dialog1.findViewById(R.id.close);
                    final EditText edtAMT = dialog1.findViewById(R.id.mnAMT);
                    final EditText edtMode = dialog1.findViewById(R.id.mnMode);
                    final EditText edttrId = dialog1.findViewById(R.id.mnTrid);
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            boolean cancel = false;
                            View focusView = null;


                            if (TextUtils.isEmpty(edtAMT.getText().toString())) {
                                edtAMT.setError(getString(R.string.error_field_required));
                                focusView = edtAMT;
                                cancel = true;
                            } else if (Integer.parseInt(edtAMT.getText().toString().trim()) <= 0) {
                                edtAMT.setError("Enter amount grater ");
                                focusView = edtAMT;
                                cancel = true;
                            } else if (TextUtils.isEmpty(edtMode.getText().toString())) {
                                edtMode.setError(getString(R.string.error_field_required));
                                focusView = edtMode;
                                cancel = true;
                            } else if (TextUtils.isEmpty(edttrId.getText().toString())) {
                                edttrId.setError(getString(R.string.error_field_required));
                                focusView = edttrId;
                                cancel = true;
                            }
                            if (cancel) {
                                focusView.requestFocus();
                            } else {
                                dialog1.dismiss();
                                Incert(edtAMT.getText().toString(), edtMode.getText().toString(), edttrId.getText().toString());
                            }


                        }
                    });

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                        }
                    });
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog1.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog1.show();
                    dialog1.getWindow().setAttributes(lp);



                }else {

                    startActivity(new Intent(Manual_FundTransfer.this , LoadMoney.class));

                }
            }
        });
        builderSingle.show();
    }

    private void Incert(final String s, final String s1, final String s2) {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.offline, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        android.app.AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new android.app.AlertDialog.Builder(Manual_FundTransfer.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                        } else {
                            builder = new android.app.AlertDialog.Builder(Manual_FundTransfer.this);
                        }
                        builder.setCancelable(false);
                        builder.setTitle("Transaction Done")
                                .setMessage(jsonObject.getString("result"))
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(Manual_FundTransfer.this, MainActivity.class));
                                        finish();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    } else {
                        JSONArray object = jsonObject.getJSONArray("result");
                        Global.diloge(Manual_FundTransfer.this, "Login Error", object.getString(0));
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
                Map<String, String> param = new HashMap<String, String>();
                param.put("customer_id", Global.customerid);
                param.put("amount", s);
                param.put("transaction_id", s1);
                param.put("payment_mode", s2);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }


}
