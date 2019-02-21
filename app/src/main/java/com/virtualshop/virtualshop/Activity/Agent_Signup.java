package com.virtualshop.virtualshop.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Config.SQLiteHandler;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Agent_Signup extends AppCompatActivity {

    EditText Pincode;
    Button btnAgentsignup;
    ProgressDialog progressDialog;
    SQLiteHandler db;
    SimpleDateFormat SDF = new SimpleDateFormat("ddMMyyyyhhmmss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_signup);

        db=new SQLiteHandler(this);



        progressDialog = new ProgressDialog(this);
        Pincode = findViewById(R.id.agPincode);
        btnAgentsignup = findViewById(R.id.btnAgentsignup);

        btnAgentsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Pincode.getText().length() < 6){
                    Global.diloge(Agent_Signup.this , "Invalid Pin" ,"Enter Valid 6 Digit Pincode");
                }else{
                    agent_signup();
                }
            }
        });

    }

    private void agent_signup() {

        progressDialog.setTitle("Submitting...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.AgentSignup, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status"))
                    {

                        db.deleteUsers();
                        db.addUser(
                                Global.customerid,
                                Global.refferalid,
                                Global.Email,
                                Global.mobile,
                                Global.name,
                                SDF.format(new Date()),
                                "1",
                                jsonObject.getString("agent_id"));


                        Global.successDilogue(Agent_Signup.this,jsonObject.getString("result"));

                    }
                    else
                    {
                        Global.failedDilogue(Agent_Signup.this , jsonObject.getString("result"));
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
                param.put("customer_id",Global.customerid);
                param.put("name",Global.name);
                param.put("mobile",Global.mobile);
                param.put("pincode",Pincode.getText().toString());
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }
}
