package com.virtualshop.virtualshop.Activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jh.circularlist.CircularAdapter;
import com.jh.circularlist.CircularListView;
import com.jh.circularlist.CircularTouchListener;
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

public class LotteryWinnerlist extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    List< TransactionModel > transactionModels = new ArrayList < > ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_winnerlist);
        ActionBar abar = getSupportActionBar();
        Global.actionbar(LotteryWinnerlist.this ,abar ,"LOTTERY WINNERS");

        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        WinnerList();
    }


    private void WinnerList() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.Showinner, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        TransactionModel model = new TransactionModel();

                        model.trID = object.getString("name");
                        model.details = object.getString("mobile");
                        model.credit  = object.getString("serial_no");

                        if (object.getInt("status") == 11) {
                            model.date = getIntent().getStringExtra("1st");
                        } else if (object.getInt("status") == 12) {
                            model.date = getIntent().getStringExtra("2nd");
                        } else if (object.getInt("status") == 13) {
                            model.date = getIntent().getStringExtra("3rd");
                        }

                        transactionModels.add(model);
                    }

                    recyclerView.setAdapter(new RecyclerView.Adapter() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(LotteryWinnerlist.this).inflate(R.layout.winnerlayout, parent, false);
                            Holder holder = new Holder(view);
                            return holder;
                        }

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hold, int position) {

                            final Holder holder = (Holder) hold;
                            TransactionModel model = transactionModels.get(position);

                            holder.name.setText("Mr/Miss : " + model.trID);
                            holder.mobile.setText(model.details);
                            holder.srno.setText(model.date);
                            holder.fst.setTag(model);
                            holder.scd.setTag(model);
                            holder.thrd.setTag(model);
                            holder.frth.setTag(model);
                            holder.fth.setTag(model);
                            holder.sixt.setTag(model);



                            RotateAnimation animation= new RotateAnimation(0,720, Animation.RELATIVE_TO_SELF,
                                    0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                            animation.setDuration(2000);
                           holder.circularListView.setAnimation(animation);
                           holder.circularListView.setActivated(false);

                           for( int i = Integer.parseInt(model.credit); i < Integer.parseInt(model.credit)+6 ;i++){
                              if (holder.fst.getText().toString().isEmpty()){
                                  holder.fst.setText(model.credit);
                              }else if (holder.scd.getText().toString().isEmpty()){
                                  holder.scd.setText(String.valueOf(i));
                              }else if (holder.thrd.getText().toString().isEmpty()){
                                  holder.thrd.setText(String.valueOf(i));
                              }else if (holder.frth.getText().toString().isEmpty()){
                                  holder.frth.setText(String.valueOf(i));
                              }else if (holder.fth.getText().toString().isEmpty()){
                                  holder.fth.setText(String.valueOf(i));
                              }else if (holder.sixt.getText().toString().isEmpty()){
                                  holder.sixt.setText(String.valueOf(i));
                              }
                            }

                        }

                        @Override
                        public int getItemCount() {
                            return transactionModels.size();
                        }

                        @Override
                        public int getItemViewType(int position)
                        {
                            return position;
                        }

                        @Override
                        public long getItemId(int position) {
                            return position;
                        }

                        @Override
                        public void setHasStableIds(boolean hasStableIds) {
                            super.setHasStableIds(hasStableIds);
                        }

                        class Holder extends RecyclerView.ViewHolder {
                            TextView name, mobile, srno;
                            RelativeLayout circularListView;

                            TextView fst,scd,thrd,frth,fth,sixt;

                            public Holder(@NonNull View itemView) {
                                super(itemView);

                                name = itemView.findViewById(R.id.wuserName);
                                mobile = itemView.findViewById(R.id.wuserNo);
                                srno = itemView.findViewById(R.id.wamt);
                                circularListView = itemView.findViewById(R.id.winner_wheel);
                                fst= itemView.findViewById(R.id.fst);
                                scd= itemView.findViewById(R.id.scd);
                                thrd= itemView.findViewById(R.id.thrd);
                                frth= itemView.findViewById(R.id.frth);
                                fth= itemView.findViewById(R.id.fth);;
                                sixt= itemView.findViewById(R.id.sixt);;
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
                param.put("lottery_id", getIntent().getStringExtra("lid"));
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }


}
