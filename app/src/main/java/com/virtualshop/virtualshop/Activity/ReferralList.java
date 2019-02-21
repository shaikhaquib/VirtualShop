package com.virtualshop.virtualshop.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.Model.TransactionModel;
import com.virtualshop.virtualshop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReferralList extends AppCompatActivity {
    List< TransactionModel > transactionModels = new ArrayList< >();
    ProgressDialog progressDialog;
    RecyclerView rvpList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referrallist);

        ActionBar actionBar = getSupportActionBar();
        Global.actionbar(ReferralList.this ,actionBar,"Referral List");

        progressDialog = new ProgressDialog(ReferralList.this);
        rvpList =  findViewById(R.id.rvReferral);
        rvpList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        viewParticiapnt();

    }

    private void viewParticiapnt() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.referrallist, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        TransactionModel model = new TransactionModel();

                        model.trID = object.getString("email");
                        model.details = object.getString("mobile");

                        transactionModels.add(model);
                    }

                    rvpList.setAdapter(new RecyclerView.Adapter() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(ReferralList.this).inflate(R.layout.translist, parent, false);
                            Holder holder = new Holder(view);
                            return holder;
                        }

                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hold, int position) {

                            Holder holder = (Holder) hold;
                            TransactionModel model = transactionModels.get(position);

                            holder.name.setText(model.trID);
                            holder.mobile.setText(model.details);
                            holder.srno.setText(model.date);

                        }

                        @Override
                        public int getItemCount() {
                            return transactionModels.size();
                        }

                        class Holder extends RecyclerView.ViewHolder {
                            TextView name,
                                    mobile,
                                    srno;
                            public Holder(@NonNull View itemView) {
                                super(itemView);

                                name = itemView.findViewById(R.id.userName);
                                mobile = itemView.findViewById(R.id.userNo);
                                srno = itemView.findViewById(R.id.Serailno);

                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                })

        {
            @Override
            protected Map< String, String > getParams() throws AuthFailureError {
                Map < String, String > param = new HashMap< String, String >();
                param.put("customer_id", Global.userid);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

}
