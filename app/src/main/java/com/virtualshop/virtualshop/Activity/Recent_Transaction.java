package com.virtualshop.virtualshop.Activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recent_Transaction extends AppCompatActivity {


    private ProgressDialog progressDialog;
    List<TransactionModel> transactionModels = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recenttransaction);
        progressDialog = new ProgressDialog(Recent_Transaction.this);
        recyclerView = findViewById(R.id.rvRecent);
        recyclerView.setLayoutManager(new LinearLayoutManager(Recent_Transaction.this));

        getTransactionData();
    }

    private void getTransactionData() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.transaction_detail, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        TransactionModel model = new TransactionModel();

                        if (!TextUtils.isEmpty(object.getString("credit"))) {
                            model.credit = object.getString("credit");
                        }
                        if (!TextUtils.isEmpty(object.getString("debit"))) {
                            model.debit = object.getString("debit");
                        }

                        model.customer_id = object.getString("customer_id");
                        model.trID = object.getString("transaction_id");
                        model.details = object.getString("details");
                        model.date = object.getString("date");
                        model.beTrans = object.getString("before_transaction");
                        model.afTrans = object.getString("after_transaction");
                        model.surcharge = object.getString("surcharge");
                        model.description = object.getString("description");

                        transactionModels.add(model);
                    }

                    recyclerView.setAdapter(new RecyclerView.Adapter() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(Recent_Transaction.this).inflate(R.layout.rvtransaction, parent, false);
                            Holder holder = new Holder(view);
                            return holder;
                        }  

                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hold, int position) {

                            Holder holder = (Holder) hold;
                            final TransactionModel model = transactionModels.get(position);
                            holder.date.setText(model.date);
                            holder.id.setText(model.trID);
                            holder.details.setText(model.details);
                            holder.status.setTag(model);
                            holder.trmore.setTag(model);


                            if (!model.credit.equals("null") && !model.credit.equals("0") ) {

                                String number = model.credit;
                                double amount = Double.parseDouble(number);
                                DecimalFormat formatter = new DecimalFormat("#,###.0");
                                String formatted = formatter.format(amount);

                                holder.amount.setText("Credited : " + formatted);
                                holder.amount.setTextColor(Color.parseColor("#57d843"));
                                holder.status.setImageResource(R.drawable.ic_plus);

                                // holder.status.setText("CREDITED");
                                //holder.status.setTextColor(Color.parseColor("#57d843"));
                            } else if (!model.debit.equals("null") && !model.debit.equals("0")) {

                                String number = model.debit;
                                double amount = Double.parseDouble(number);
                                DecimalFormat formatter = new DecimalFormat("#,###.0");
                                String formatted = formatter.format(amount);
                                holder.status.setImageResource(R.drawable.ic_minusl);

                                holder.amount.setText("Debited : "+ formatted);
                                holder.amount.setTextColor(Color.parseColor("#ff0000"));
                               // holder.status.setText("DEBITED");
                               // holder.status.setTextColor(Color.parseColor("#ff0000"));
                            }



                            holder.trmore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!model.credit.equals("null") && !model.credit.equals("0") ) {   
                                        
                                        CapingLimit(model.credit,model.beTrans,model.afTrans,model.surcharge,model.date,model.details);
                                    }else {
                                        CapingLimit(model.debit,model.beTrans,model.afTrans,model.surcharge,model.date,model.details);
                                    }
                                }
                            });

                        }

                        @Override
                        public int getItemCount() {
                            return transactionModels.size();
                        }

                        class Holder extends RecyclerView.ViewHolder {
                            TextView date, id, amount, details ;
                            ImageView status , trmore;

                            public Holder(@NonNull View itemView) {
                                super(itemView);

                                amount = itemView.findViewById(R.id.tramount);
                                id = itemView.findViewById(R.id.trId);
                                details = itemView.findViewById(R.id.trdetail);
                                date = itemView.findViewById(R.id.trdate);
                                status = itemView.findViewById(R.id.trstatus);
                                trmore = itemView.findViewById(R.id.trmore);

                                // status = itemView.findViewById(R.id.trstat);

                            }
                        }
                    });

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
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void CapingLimit(String credit, String beTrans, String afTrans, String surcharge, String date, String details) {


        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Recent_Transaction.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.transactiondetail, null);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Recent_Transaction.this);
        // builder.setCancelable(false);

        final TextView amount =mView.findViewById(R.id.dgamount);
        final TextView bt     =mView.findViewById(R.id.dgbt);
        final TextView at     =mView.findViewById(R.id.dgat);
        final TextView txtsurcharge =mView.findViewById(R.id.dgsur);
        final TextView  description =mView.findViewById(R.id.dgdesc);
        final TextView txtdate =mView.findViewById(R.id.dgdate);
        Button close =mView.findViewById(R.id.dgclose);

        amount.setText("₹ "+String.valueOf(credit));
        bt.setText("₹ "+String.valueOf(beTrans));
        at.setText("₹ "+String.valueOf(afTrans));
        txtsurcharge.setText(String.valueOf(surcharge));
        description.setText(details);
        txtdate.setText(date);

        builder.setView(mView);
        final android.support.v7.app.AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

}
