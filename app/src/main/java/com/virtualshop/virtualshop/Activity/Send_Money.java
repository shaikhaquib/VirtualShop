package com.virtualshop.virtualshop.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

public class Send_Money extends AppCompatActivity {


    EditText edtID , edtAMT;
    Button btnSubmit;
    RequestQueue queue;
    ProgressDialog dialog;
    TextView balance;
    ActionBar abar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmoney);
        queue= Volley.newRequestQueue(this);
        dialog=new ProgressDialog(this);

        // Set your custom view
        abar = getSupportActionBar();
        Global.actionbar(Send_Money.this ,abar ,"LOTTERY DETAILS");


        initialize();
    }
    @SuppressLint("ClickableViewAccessibility")
    private void initialize() {

        edtID=findViewById(R.id.payid);
        edtAMT=findViewById(R.id.payamt);
        btnSubmit=findViewById(R.id.btnpay);


        edtAMT.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edtID.getText().toString().isEmpty()){
                    edtID.setError("Please enter Custumer id");
                }
                return false;
            }
        });



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amt = Integer.parseInt(edtAMT.getText().toString());
                if (edtID.getText().toString().isEmpty()){
                    edtID.setError("Please enter Custumer id");
                }else if (edtAMT.getText().toString().isEmpty()){
                    edtAMT.setError("Please enter Amount");
                }else if (amt < 9){
                    edtAMT.setError("Amount must be greater or equal to 10 ");
                }else {
                    pay();
                }
            }
        });
    }

    private void pay() {
        showProgress();
        StringRequest request=new StringRequest(StringRequest.Method.POST, APIs.payurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dissmissProgrss();

                try {

                    JSONObject jsonObject =new JSONObject(response);
                    if (jsonObject.getString("status").equals("1")){
                        successDilogue(jsonObject.getString("message"),Send_Money.this);
                    }else {
                        failedDilogue(jsonObject.getString("message"),Send_Money.this);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dissmissProgrss();
                failedDilogue("Connection Problem",Send_Money.this);
            }
        }){            @Override
        protected Map<String, String> getParams() throws AuthFailureError {

            Map<String, String> params = new HashMap<String, String>();

            params.put("sender_id", Global.customerid);
            params.put("receiver_id", edtID.getText().toString());
            params.put("amount", edtAMT.getText().toString() );

            return params;
        }
        };

        AppController.getInstance().addToRequestQueue(request);
    }

    public  void successDilogue(String result, Activity activity) {
        final Dialog dialog = new Dialog(activity);
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
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public  void failedDilogue(String result, Activity activity) {
        final Dialog dialog1 = new Dialog(activity);
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

    private void showProgress(){
        dialog.setMessage("Processing...");
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

    public void ShowProgress(String s){
        dialog.setMessage(s);
        dialog.setCancelable(false);
        dialog.show();
    }
    public void DissProgress(){
        dialog.dismiss();
    }
}