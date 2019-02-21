package com.virtualshop.virtualshop.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.CustomGridAdapter;
import com.virtualshop.virtualshop.Firebase.Broadcast_FCM;
import com.virtualshop.virtualshop.Firebase.Fcm;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.Model.LotteryModel;
import com.virtualshop.virtualshop.R;
import com.virtualshop.virtualshop.Model.TransactionModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FundRaise extends AppCompatActivity {

    CountDownTimer mCountDownTimer;
    String balance;

    Button share;
    TextView txtrefferalid ;
    long startTime;
    SwipeRefreshLayout refreshLayout;
    Random rand = new Random();

    ProgressDialog progressDialog;

    List < String > srlno = new ArrayList < > ();

    List < TransactionModel > transactionModels = new ArrayList < > ();
    ArrayList<LotteryModel> Model = new ArrayList<>();
    RecyclerView rvpList , recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_fundraise);

        //    CountDownView countDownTimer =  view.findViewById(R.id.count_down);

        ActionBar abar = getSupportActionBar();
        Global.actionbar(FundRaise.this ,abar ,"FUND RAISE");

        progressDialog = new ProgressDialog(FundRaise.this);

        rvpList = findViewById(R.id.rvparticipantlist);
        recyclerView = findViewById(R.id.rvlottery);
        refreshLayout = findViewById(R.id.frRefresh);

        rvpList.setLayoutManager(new LinearLayoutManager(FundRaise.this));
        rvpList.setNestedScrollingEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(FundRaise.this));
        recyclerView.setNestedScrollingEnabled(true);

        txtrefferalid = findViewById(R.id.sharefferalid);
        share = findViewById(R.id.btnshrRefferal);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                share();

            }
        });




        rvpList.setVisibility(View.VISIBLE);


        //   startTimer("16.09.2018, 15:05:36");
        txtrefferalid.setText("Refferal code: " + Global.customerid);
        getData();
        getLotteryDetais();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                getLotteryDetais();
            }
        });
    }
    private void Selectno(int total, final String fess, final String ltryId) {

        // Prepare grid view

        final Dialog dialog = new Dialog(FundRaise.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.gridview);
        GridView gridView = dialog.findViewById(R.id.gridview);
        ImageView close = dialog.findViewById(R.id.closediloge);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final List < Integer > mList = new ArrayList < Integer > ();
        for (int i = 1; i < total + 1; i++) {

            if (!srlno.contains(String.valueOf(i))) {
                mList.add(i);
            }
        }

        CustomGridAdapter gridAdapter = new CustomGridAdapter(FundRaise.this, mList);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > parent, View view, final int position, long id) {
                // do something here
              /*  AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(FundRaise.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(FundRaise.this);
                }

                builder.setCancelable(false);
                builder.setTitle("Confirm").setMessage("Are you sure do you want to continue with " + String.valueOf(mList.get(position))).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        JOINNOW(String.valueOf(mList.get(position)) ,ltryId ,fess);
                        dialog.dismiss();
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();

                dialog.dismiss();*/



                final Dialog dialog = new Dialog(FundRaise.this);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.fundraise_diloge);
                Button button = dialog.findViewById(R.id.dgConfirm);
                TextView first = dialog.findViewById(R.id.dgfirst);
                TextView second = dialog.findViewById(R.id.dgsecond);
                TextView third = dialog.findViewById(R.id.dgthird);
                TextView messege = dialog.findViewById(R.id.dgText);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JOINNOW(String.valueOf(mList.get(position)) ,ltryId ,fess);
                        //     dialog.dismiss();
                    }
                });

                for(int i =0; i < Integer.parseInt(ltryId); i++)
                {
                    int answer = rand.nextInt(10) + 1;
                    System.out.println(answer);

                    first.setText("First Prize :     "+String.valueOf(rand.nextInt(10) + 1));
                    third.setText("Second Prize :    "+String.valueOf(rand.nextInt(10) + 1));
                    second.setText("Third Prize :     "+String.valueOf(rand.nextInt(10) + 1));
                }

                messege.setText("Are you sure do you want to continue with " + String.valueOf(mList.get(position)));

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);




            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // Set grid view to alertDialog

    }

    private void share() {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Virtual Shop Providing Real time e-commerce Trading and many services\n" + "\n" + "Get Rs.50 today For sign up "+"\n"+"Join VIRTUAL SHOP with Refferal Code " + Global.customerid + "\r\n" + "https://play.google.com/store/apps/details?id=com.virtualshop.virtualshop");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);




    }


    private void JOINNOW(final String srno, final String ltryId, final String strentryfees) {

        progressDialog.setMessage("Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.lottery_join, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        successDilogue(jsonObject.getString("result"));
                        new Fcm().execute(Global.customerid,jsonObject.getString("result"),"You have Successfully Joined lottery with No "+ srno+" Keep Your Finger's Cross ;)");
                        new Broadcast_FCM().execute(Global.customerid,jsonObject.getString("result"),"Mr/Miss "+Global.name + " has joined lottery of amount ₹" + strentryfees +" with Number " + srno +". Hurry Up Limited Numbers are Available !");

                    } else {
                        failedDilogue(String.valueOf(jsonObject.getString("result")));
                        new Fcm().execute(Global.customerid,jsonObject.getString("result"),"SOMETHING WENT WRONG please try after some time");

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
            protected Map < String, String > getParams() throws AuthFailureError {
                Map < String, String > param = new HashMap < String,
                        String > ();
                param.put("customer_id", Global.customerid);
                param.put("amount", strentryfees);
                param.put("name", Global.name);
                param.put("mobile", Global.mobile);
                param.put("lottery_id", ltryId);
                param.put("serial_no", srno);
                param.put("referral_id", Global.refferalid);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void getLotteryDetais() {

        refreshLayout.setRefreshing(false);
        Model.clear();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.lottery_details, new Response.Listener < String > () {
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
                            lotteryModel.endDate = Details.getString("end_date");
                            lotteryModel.status = Details.getInt("status");
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
                            lotteryModel.zone ="Zone: " + Details.getString("zone");


                            lotteryModel.total = Integer.parseInt(Details.getString("total_participate"));
                            lotteryModel.active = Integer.parseInt(Details.getString("active_participate"));


                            float second = Float.parseFloat(s[1]);
                            float third = Float.parseFloat(t[1]);

                            float count = 1 + second + third;
                            lotteryModel.count = 1 + Integer.parseInt(s[1]) + Integer.parseInt(t[1]);
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
                                View view = LayoutInflater.from(FundRaise.this).inflate(R.layout.lottery, viewGroup, false);
                                Holder holder = new Holder(view);
                                return holder;
                            }

                            @Override
                            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

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
                                holder.zone.setText("Winner Count : "+String.valueOf( model.count));
                                holder.showinner.setTag(model);
                                holder.viewParticipant.setTag(model);
                                holder.join.setTag(model);

                                holder.day.setTag(model);
                                holder.hour.setTag(model);
                                holder.minutes.setTag(model);
                                holder.second.setTag(model);
                                holder.d.setTag(model);
                                holder.h.setTag(model);
                                holder.m.setTag(model);
                                holder.s.setTag(model);
                                holder.viewParticipant.setText("PARTICIPANT" + "(" + model.active + " / " + model.total + ")");


                                if (model.active >= model.total) {
                                    holder.join.setVisibility(View.GONE);
                                }

                                if (model.status == 0) {
                                    holder.join.setVisibility(View.GONE);
                                    holder.viewParticipant.setVisibility(View.GONE);
                                    holder.showinner.setVisibility(View.VISIBLE);
                                    holder.join.setVisibility(View.GONE);
                                    holder.timerlayout.setVisibility(View.GONE);
                                } else {
                                    startTimer(model.endDate,holder.day ,holder.hour,holder.minutes,holder.second,holder.d ,holder.h,holder.m,holder.s);
                                }

                                holder.showinner.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(FundRaise.this , LotteryWinnerlist.class);
                                        intent.putExtra("1st",model.firstPrize);
                                        intent.putExtra("2nd",model.secondPrize);
                                        intent.putExtra("3rd",model.thirdPrize);
                                        intent.putExtra("lid",model.ltryId);
                                        startActivity(intent);

                                    }
                                });



                                holder.viewParticipant.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        transactionModels.clear();
                                        if (model.active > 0){
                                        viewParticiapnt(model.ltryId);
                                        }else{
                                            Global.diloge(FundRaise.this,"No Participant","Join and become the first participant");
                                        }
                                    }
                                });

                                holder.join.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        float bal = Float.parseFloat(balance);
                                        float entryfee = Float.parseFloat(model.strentryfees);

                                        if (bal >= entryfee) {

                                            if (model.active > 0) {
                                                getsrlno(model.ltryId,model.total,model.strentryfees);
                                            } else {
                                                Selectno(model.total , model.strentryfees , model.ltryId);
                                            }

                                        } else {

                                            AlertDialog.Builder builder;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                builder = new AlertDialog.Builder(FundRaise.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                                            } else {
                                                builder = new AlertDialog.Builder(FundRaise.this);
                                            }
                                            builder.setCancelable(false);
                                            builder.setTitle("Insufficient Balance").setMessage("Sorry your balance is Insufficient to enroll ! Please add Balance.").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).setIcon(android.R.drawable.ic_dialog_alert).show();

                                        }

                                    }
                                });

                            }

                            @Override
                            public int getItemCount() {
                                return Model.size();
                            }

                            class Holder extends RecyclerView.ViewHolder {

                                TextView mTextView, firstPrize, secondPrize, thirdPrize, entryfees, viewParticipant, zone, join, showinner;
                                TextView d, h, m, s, fprcount, sprcount, tprcount, winningcount,hour,minutes,second,day;
                                LinearLayout timerlayout;

                                public Holder(@NonNull View itemView) {
                                    super(itemView);

                                    firstPrize = itemView.findViewById(R.id.firstPrize);
                                    secondPrize = itemView.findViewById(R.id.secondPrize);
                                    thirdPrize = itemView.findViewById(R.id.thirdPrize);
                                    entryfees = itemView.findViewById(R.id.entryfees);
                                    zone = itemView.findViewById(R.id.ltzone);
                                    showinner = itemView.findViewById(R.id.winnerList);
                                    fprcount = itemView.findViewById(R.id.fprcount);
                                    sprcount = itemView.findViewById(R.id.sprcount);
                                    tprcount = itemView.findViewById(R.id.tprcount);
                                    winningcount = itemView.findViewById(R.id.winningcount);

                                    viewParticipant = itemView.findViewById(R.id.viewParticipant);
                                    join = itemView.findViewById(R.id.JoinNow);

                                    d = itemView.findViewById(R.id.sfD);
                                    h = itemView.findViewById(R.id.sfH);
                                    m = itemView.findViewById(R.id.sfM);
                                    s = itemView.findViewById(R.id.sfS);

                                    day     = itemView.findViewById(R.id.ctDay);
                                    hour    = itemView.findViewById(R.id.ctHour);
                                    minutes = itemView.findViewById(R.id.ctMinutes);
                                    second  = itemView.findViewById(R.id.ctSeconds);

                                    timerlayout = itemView.findViewById(R.id.timerlayout);

                                }
                            }

                        });

                    } else {
                        Global.diloge(FundRaise.this, "Error", jsonObject.getString("result"));
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

        {
            @Override
            protected Map < String, String > getParams() throws AuthFailureError {
                Map < String, String > param = new HashMap < String, String > ();
                param.put("customer_id", Global.customerid);
                param.put("lottery_id", Global.ltryId);

                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void startTimer(String endTime, final TextView day, final TextView hour, final TextView minutes, final TextView second, final TextView d, final TextView h, final TextView m, final TextView s) {
        long milliseconds = 0;
        long diff;

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        formatter.setLenient(false);

        Date endDate;
        try {
            endDate = formatter.parse(endTime);
            milliseconds = endDate.getTime();

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        startTime = System.currentTimeMillis();

        diff = milliseconds - startTime;

        mCountDownTimer = new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                startTime = startTime - 1;
                Long serverUptimeSeconds = (millisUntilFinished - startTime) / 1000;

                String daysLeft = String.format("%01d", serverUptimeSeconds / 86400);
                day.setVisibility(View.VISIBLE);
                d.setVisibility(View.VISIBLE);
                if (daysLeft.length() > 1) {
                    day.setText(daysLeft);
                } else if (daysLeft.length() == 0) {
                    day.setText("00");
                } else {
                    day.setText("0" + daysLeft);
                }

                String hoursLeft = String.format("%01d", (serverUptimeSeconds % 86400) / 3600);

                if (hoursLeft.length() > 1) {
                    hour.setText(hoursLeft);
                } else if (hoursLeft.length() == 0) {
                    hour.setText("00");
                } else {
                    hour.setText("0" + hoursLeft);
                }

                hour.setVisibility(View.VISIBLE);
                h.setVisibility(View.VISIBLE);

                String minutesLeft = String.format("%01d", ((serverUptimeSeconds % 86400) % 3600) / 60);
                if (minutesLeft.length() > 1) {
                    minutes.setText(minutesLeft);
                } else if (minutesLeft.length() == 0) {
                    minutes.setText("00");
                } else {
                    minutes.setText("0" + minutesLeft);
                }

                minutes.setVisibility(View.VISIBLE);
                m.setVisibility(View.VISIBLE);

                String secondsLeft = String.format("%01d", ((serverUptimeSeconds % 86400) % 3600) % 60);
                if (secondsLeft.length() > 1) {
                    second.setText(secondsLeft);
                } else if (secondsLeft.length() == 1) {
                    second.setText("00");
                } else {
                    second.setText("0" + secondsLeft);
                }

                second.setVisibility(View.VISIBLE);
                s.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFinish() {
                System.out.println("Finish");
            }
        }.start();

    }

    private void viewParticiapnt(final String ltryId) {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.viewParticiapnt, new Response.Listener < String > () {
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
                        model.date = object.getString("serial_no");

                        transactionModels.add(model);
                    }

                    rvpList.setAdapter(new RecyclerView.Adapter() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(FundRaise.this).inflate(R.layout.translist, parent, false);
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
            protected Map < String, String > getParams() throws AuthFailureError {
                Map < String, String > param = new HashMap < String, String > ();
                param.put("lottery_id", ltryId);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void getsrlno(final String ltryId, final int total, final String strentryfees) {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.viewParticiapnt, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        srlno.add(object.getString("serial_no"));
                        //    transactionModels.add(model);
                    }

                    Selectno(total,strentryfees,ltryId);

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
            protected Map < String, String > getParams() throws AuthFailureError {
                Map < String, String > param = new HashMap < String, String > ();
                param.put("lottery_id", ltryId);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void getData() {
        refreshLayout.setRefreshing(false);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.balance_detail, new Response.Listener < String > () {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        JSONArray object = jsonObject.getJSONArray("result");
                        JSONObject Details = object.getJSONObject(0);

                        balance = Details.getString("balance");
                            /*      double amount = Double.parseDouble(number);
                        DecimalFormat formatter = new DecimalFormat("#,###.00");
                        String formatted = formatter.format(amount);

                        Balance.setText(formatted);*/

                    } else {
                        Global.diloge(FundRaise.this, "Login Error", jsonObject.getString("result"));
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

        {
            @Override
            protected Map < String, String > getParams() throws AuthFailureError {
                Map < String, String > param = new HashMap < String, String > ();
                param.put("customer_id", Global.customerid);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void failedDilogue(String result) {

        final Dialog dialog = new Dialog(FundRaise.this);
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
        final Dialog dialog = new Dialog(FundRaise.this);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.success_dilogue);
        Button button = dialog.findViewById(R.id.btnsucces);
        TextView textView = dialog.findViewById(R.id.successtext);
        textView.setText(result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FundRaise.this, MainActivity.class));
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