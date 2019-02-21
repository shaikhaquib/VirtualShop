package com.virtualshop.virtualshop.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Firebase.Fcm;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;

/**
 * Created by Shaikh Aquib.
 */

public class LoadMoney extends AppCompatActivity {

    EditText Amount, Mobile, Accountno, Ifsc , Notes;
    Button submit;
    boolean cancel= false;
    View focusView = null;
    String orderid , Status;
    String txnid;
    String paymentid;
    String token;
    RequestQueue queue;
    ProgressDialog dialog;
    boolean aBoolean=false;


    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", "Add to money wallet");
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
             //   Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                Log.d("responce",response);

                String[] s = response.split(":");
                Status    =  s[0].substring(s[0].indexOf("=")+1);
                orderid    =  s[1].substring(s[1].indexOf("=")+1);
                 txnid      = s[2].substring(s[2].indexOf("=")+1);
                 paymentid  = s[3].substring(s[3].indexOf("=")+1);
                 token      = s[4].substring(s[4].indexOf("=")+1);

                 if (aBoolean){
                         aBoolean=false;
                         Loadmoney(orderid , txnid , paymentid , token, Status);}
            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
                if (aBoolean){
                    aBoolean=false;
                Loadmoney("0" , "0" , "0" , "0", "0");}

                Log.d("failed",reason);
            }
        };
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadmoney);
        dialog = new ProgressDialog(this);
        queue= Volley.newRequestQueue(this);

        InitializeObject();
       // onTouchField();
       // Validation();
       // onSubmit();
    }
    private void onSubmit() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Amount.getText().toString().equals("")){
                    Amount.setError("Please Enter UserName");
                    focusView = Amount;
                    Amount.requestFocus();
                    cancel = true;
                }else
                if (Mobile.getText().toString().equals("")){
                    Mobile.setError("Please Enter STDcode");
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
                }
            }
        });


    }

    @SuppressLint("ClickableViewAccessibility")
    private void onTouchField() {
        Mobile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (Amount.getText().toString().equals("")){
                    Amount.setError("Please Enter UserName");
                    focusView = Amount;
                    Amount.requestFocus();
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

                if (Amount.getText().toString().equals("")){
                    Amount.setError("Please Enter UserName");
                    focusView = Amount;
                    Amount.requestFocus();
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

                if (Amount.getText().toString().equals("")){
                    Amount.setError("Please Enter UserName");
                    focusView = Amount;
                    Amount.requestFocus();
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


    private void InitializeObject() {
        Amount =findViewById(R.id.loadAmount);
        Mobile=findViewById(R.id.loadmobile);
        Accountno=findViewById(R.id.loadAccountno);
        Ifsc=findViewById(R.id.loadIfsc);
        submit =findViewById(R.id.loadSubmit);
        Notes = findViewById(R.id.loadNote);
        Notes.setVisibility(View.GONE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Amount.getText().toString().isEmpty()){
                   callInstamojoPay(Global.Email , Global.mobile,Amount.getText().toString() ,"" , Global.Email);
                   // String response ="status=success:orderId=2c964245c20f468f8a8df7b74710d922:txnId=None:paymentId=MOJO8514005N81618123:token=5e7qc8WbZCAYEQWrwXMVvRwv7mOWos";

                    aBoolean = true;

                }else {
                    Amount.setError("Please Enter amount");
                }
            }
        });
    }
    private void Loadmoney(final String orderid, final String txnid, String paymentid, String token, final String Status) {

        StringRequest request =new StringRequest(StringRequest.Method.POST, APIs.addmoney, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 1){
                        successDilogue(jsonObject.getString("message"));
                        new Fcm().execute(Global.customerid,jsonObject.getString("message"),"Your account has been credited by ₹"+Amount.getText().toString());

                    }else {

                        new Fcm().execute(Global.customerid,jsonObject.getString("message"),"Transaction failed");
                        failedDilogue("Something went wrong");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("customer_id",Global.customerid);
                params.put("amount",Amount.getText().toString());
                params.put("txn_id",txnid);
                params.put("order_id",orderid);
                params.put("status",Status);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(request);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(new Intent(getApplicationContext(), MainActivity.class)));

    }
    private void successDilogue(String result, LoadMoney recharge) {
        final Dialog dialog = new Dialog(LoadMoney.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.success_dilogue);
        Button button = dialog.findViewById(R.id.btnsucces);
        TextView textView = dialog.findViewById(R.id.successtext);
        textView.setText(result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void failedDilogue(String result) {

        final Dialog dialog = new Dialog(LoadMoney.this);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.failediloge);
        Button button = dialog.findViewById(R.id.btnfailed);
        TextView textView = dialog.findViewById(R.id.failedreson);
        textView.setText(result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void successDilogue(String result) {
        final Dialog dialog = new Dialog(LoadMoney.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.success_dilogue);
        Button button = dialog.findViewById(R.id.btnsucces);
        TextView textView = dialog.findViewById(R.id.successtext);
        textView.setText(result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}
