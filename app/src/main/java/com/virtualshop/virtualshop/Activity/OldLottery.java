package com.virtualshop.virtualshop.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.Model.LotteryModel;
import com.virtualshop.virtualshop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OldLottery extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    ArrayList<LotteryModel> Model = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_lottery);

        ActionBar abar = getSupportActionBar();
       Global.actionbar(OldLottery.this ,abar ,"LOTTERY DETAILS");

        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.oldLottery);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        getLotteryDetais();
    }



    private void getLotteryDetais() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.oldLottery, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {


                        JSONArray object = jsonObject.getJSONArray("result");
                        for (int i = 0; i < object.length(); i++) {

                            JSONObject Details = object.getJSONObject(i);

                            LotteryModel lotteryModel = new LotteryModel();

                            lotteryModel.ltryId = Details.getString("lottery_id");
                            lotteryModel.strentryfees = Details.getString("entry_fees");

                            String[] f = Details.getString("f_price").split("\\s*,\\s*");
                            String[] s = Details.getString("s_price").split("\\s*,\\s*");
                            String[] t = Details.getString("t_price").split("\\s*,\\s*");

                            lotteryModel.fprcount = " x 1";
                            lotteryModel.sprcount = " x " + s[1];
                            lotteryModel.tprcount = " x " + t[1];


                            lotteryModel.firstPrize = "₹" + Global.CurrencyFormat(f[0]);
                            lotteryModel.secondPrize = "₹" + Global.CurrencyFormat(s[0]);
                            lotteryModel.thirdPrize = "₹" + Global.CurrencyFormat(t[0]);
                            lotteryModel.entryfees="Entry Fees :" + "₹" + Global.CurrencyFormat(Details.getString("entry_fees"));
                            int wcount = 1 + Integer.parseInt(s[1]) + Integer.parseInt(t[1]);
                            lotteryModel.zone ="Win Count: " + String.valueOf(wcount);


                            lotteryModel.total = Integer.parseInt(Details.getString("total_participate"));


                            float second = Float.parseFloat(s[1]);
                            float third = Float.parseFloat(t[1]);

                            float count = 1 + second + third;

                            float consumepercent = (count / lotteryModel.total) * 100;

                            DecimalFormat df = new DecimalFormat();
                            df.setMaximumFractionDigits(2);
                            lotteryModel.winningcount = "Winning \n" + String.valueOf(df.format(consumepercent)) + "%";

                            Model.add(lotteryModel);
                        }

                        recyclerView.setAdapter(new RecyclerView.Adapter() {
                            @NonNull
                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                                View view = LayoutInflater.from(OldLottery.this).inflate(R.layout.oldadapt, viewGroup, false);
                                Holder holder = new Holder(view);
                                return holder;
                            }

                            @Override
                            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                                Holder holder = (Holder) viewHolder;
                                final LotteryModel model = Model.get(i);


                                holder.fprcount.setText(model.fprcount);
                                holder.sprcount.setText(model.sprcount);
                                holder.tprcount.setText(model.tprcount);
                                holder.firstPrize.setText(model.firstPrize);
                                holder.secondPrize.setText(model.secondPrize);
                                holder.thirdPrize.setText(model.thirdPrize);
                                holder.entryfees.setText(model.entryfees);
                                holder.winningcount.setText(model.winningcount);
                                holder.zone.setText(model.zone);
                                holder.showinner.setTag(model);


                                holder.showinner.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(OldLottery.this , LotteryWinnerlist.class);
                                        intent.putExtra("1st",model.firstPrize);
                                        intent.putExtra("2nd",model.secondPrize);
                                        intent.putExtra("3rd",model.thirdPrize);
                                        intent.putExtra("lid",model.ltryId);
                                        startActivity(intent);

                                    }
                                });


                            }

                            @Override
                            public int getItemCount() {
                                return Model.size();
                            }

                            class Holder extends RecyclerView.ViewHolder {

                                TextView mTextView, firstPrize, secondPrize, thirdPrize, entryfees, viewParticipant, zone, join, showinner, txtrefferalid;
                                 TextView fprcount, sprcount, tprcount, winningcount;

                                public Holder(@NonNull View itemView) {
                                    super(itemView);

                                    firstPrize = itemView.findViewById(R.id.ADfirstPrize);
                                    secondPrize = itemView.findViewById(R.id.ADsecondPrize);
                                    thirdPrize = itemView.findViewById(R.id.ADthirdPrize);
                                    entryfees = itemView.findViewById(R.id.ADentryfees);
                                    zone = itemView.findViewById(R.id.ADltzone);
                                    showinner = itemView.findViewById(R.id.ADwinnerList);
                                    fprcount = itemView.findViewById(R.id.ADfprcount);
                                    sprcount = itemView.findViewById(R.id.ADsprcount);
                                    tprcount = itemView.findViewById(R.id.ADtprcount);
                                    winningcount = itemView.findViewById(R.id.ADwinningcount);

                                }
                            }

                        });

                    } else {
                        Global.diloge(OldLottery.this, "Error", jsonObject.getString("result"));
                    }
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

        {};
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

}