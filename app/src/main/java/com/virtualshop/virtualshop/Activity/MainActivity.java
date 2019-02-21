package com.virtualshop.virtualshop.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SupportActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Config.SQLiteHandler;
import com.virtualshop.virtualshop.Config.SessionManager;
import com.virtualshop.virtualshop.CustomSwipeAdapter;
import com.virtualshop.virtualshop.Firebase.Fcm;
import com.virtualshop.virtualshop.Manual_payment.Manual_BenificiaryList;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.Model.TransactionModel;
import com.virtualshop.virtualshop.R;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SQLiteHandler db;
    private SessionManager session;
    HashMap<String, String> user;
    private double NewVersion;
    private String currentversion;
    ProgressDialog progressDialog;
    String Prize ;
    private int mYear, mMonth, mDay, mHour, mMinute;

    TextView Balance, uName, uEmail, uMobile, txtrefferalid;
    LinearLayout addMoney;
    RecyclerView recyclerView;
    List<TransactionModel> transactionModels = new ArrayList<>();
    ImageView menu, icaddmoney;
    SessionManager sessionManager;
    LinearLayout dashrefer, sendmoney, withdrawmoney,vrSHop,myShop,notiFication,support;
    Button share;
    CircularProgressBar consumbar;
    SwipeRefreshLayout refreshLayout;
    String notificationToken;
    TextView noticount;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    int[] rewardimgArray = {R.drawable.r1,R.drawable.r2,R.drawable.r3};
    DiscreteScrollView RewardView;
    private InfiniteScrollAdapter infiniteAdapter, infRecent;
    SimpleDateFormat SDF = new SimpleDateFormat("ddMMyyyyhhmmss");
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar abar = getSupportActionBar();
        Global.actionbar(MainActivity.this ,abar ,"VIRTUAL SHOP");
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        notificationToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println(notificationToken);
        floatingActionButton = findViewById(R.id.fabsupport);



        progressDialog = new ProgressDialog(MainActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        refreshLayout = findViewById(R.id.mnrefresh);
       // navigationView.setItemIconTintList(g);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            currentversion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        user = db.getUserDetails();
        final String name = user.get("name");
        String email = user.get("email");
        Global.Email = user.get("email");
        Global.mobile = user.get("mobile");
        Global.customerid = user.get("customer_id");
        Global.userid = user.get("customer_id");
        Global.refferalid = user.get("referral_id");
        Global.A_status = user.get("status");
        Global.agentid = user.get("aid");
        Global.name = name;
        initialize();

        checkUpdate();
     //   showWinner();
        Listeners();
        getCurrentlottery();
        getTransactionData();
        UpdateDeviceToken();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        getData();

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.navUserName);
        navUsername.setText(Global.name);
        TextView NavEmail = headerView.findViewById(R.id.navEmail);
        NavEmail.setText(Global.Email);
        noticount=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_notification));
        initializeCountDrawer();

        if (Global.refferalid.equals("0")){
            Addreferral();
        }
        RewardView = findViewById(R.id.rewardSlider);

/*
        infiniteAdapter = InfiniteScrollAdapter.wrap();
*/
        RewardView.setAdapter(new CustomSwipeAdapter(MainActivity.this, rewardimgArray));

    }

    private void Addreferral() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.add_referal, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        ImageView closewinpop = mView.findViewById(R.id.closeadrefpop);
        Button submit = mView.findViewById(R.id.dgSubmit);
        final EditText edRefferal = mView.findViewById(R.id.dgReferralid);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edRefferal.getText().toString().equals("")){
                    edRefferal.setError("Referral Id required!");
                }else {
                    addReferral(edRefferal.getText().toString());
                }
            }
        });

        builder.setView(mView);



        final AlertDialog alert = builder.create();


        final Window dialogWindow = alert.getWindow();
        final WindowManager.LayoutParams dialogWindowAttributes = dialogWindow.getAttributes();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.show();

        closewinpop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }

    private void addReferral(final String referral) {


        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.AddReferral, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        db.deleteUsers();
                        db.addUser(
                                Global.customerid,
                                referral,
                                Global.Email,
                                Global.mobile,
                                Global.name,
                                SDF.format(new Date()),
                                Global.A_status,
                                Global.name);
                     Global.successDilogue(MainActivity.this,jsonObject.getString("result"));
                        new Fcm().execute(Global.customerid,"Referral Added" ,jsonObject.getString("result"));



                    } else {
                        JSONArray object = jsonObject.getJSONArray("result");
                        Global.diloge(MainActivity.this, "Login Error", object.getString(0));
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
                param.put("referral_id",referral);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);


    }

    private void initializeCountDrawer() {

        noticount.setGravity(Gravity.CENTER_VERTICAL);
        noticount.setTypeface(null, Typeface.BOLD);
        noticount.setTextColor(Color.RED);

        SharedPreferences prfs = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        Global.notificationcount = prfs.getInt("notivalue",0);
        if (Global.notificationcount > 0 ){

            noticount.setText(String.valueOf(Global.notificationcount));

        }
    }
    private void Listeners() {
        icaddmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Dialoge for payment detail
                final Dialog dialog1 = new Dialog(MainActivity.this);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.setCancelable(false);
                dialog1.setContentView(R.layout.manualtransfrom);
                Button submit = dialog1.findViewById(R.id.submit);
                ImageView close = dialog1.findViewById(R.id.close);
                final EditText edtAMT = dialog1.findViewById(R.id.mnAMT);
                final EditText edtMode = dialog1.findViewById(R.id.mnMode);
                final EditText edttrId = dialog1.findViewById(R.id.mnTrid);
                final EditText txtDate = dialog1.findViewById(R.id.in_time);
                final Button btnDatePicker = dialog1.findViewById(R.id.btn_time);

                btnDatePicker.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();

                        DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        txtDate.setText( year+ "/" + (monthOfYear + 1) + "/" + dayOfMonth);

                                    }
                                }, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
                        dpd.show();
                    }


                });


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
                        }else if (TextUtils.isEmpty(txtDate.getText().toString())) {
                            txtDate.setError("Please Select Transaction date");
                            focusView = edttrId;
                            cancel = true;
                        }
                        if (cancel) {
                            focusView.requestFocus();
                        } else {
                            dialog1.dismiss();
                            Incert(edtAMT.getText().toString(), edtMode.getText().toString(), edttrId.getText().toString(),txtDate.getText().toString());
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



            }
        });

        dashrefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),Recent_Transaction.class));
             /*   Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Join VIRTUAL SHOP with Refferal Code " + Global.customerid + "\r\n" + "https://play.google.com/store/apps/details?id=com.dealwithusmail.beeding&hl=en");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);*/
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),JivoActivity.class));
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Virtual Shop Providing Real time e-commerce Trading and many services\n" + "\n" + "Get Rs.50 today For sign up "+"\n"+"Join VIRTUAL SHOP with Refferal Code " + Global.customerid + "\r\n" + "https://play.google.com/store/apps/details?id=com.virtualshop.virtualshop");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        sendmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, Send_Money.class));

            }
        });

        withdrawmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fundTransfer();


            }
        });


        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Dialoge for payment detail
                final Dialog dialog1 = new Dialog(MainActivity.this);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.setCancelable(false);
                dialog1.setContentView(R.layout.manualtransfrom);
                Button submit = dialog1.findViewById(R.id.submit);
                ImageView close = dialog1.findViewById(R.id.close);
                final EditText edtAMT = dialog1.findViewById(R.id.mnAMT);
                final EditText edtMode = dialog1.findViewById(R.id.mnMode);
                final EditText edttrId = dialog1.findViewById(R.id.mnTrid);
                final EditText txtDate = dialog1.findViewById(R.id.in_time);
                final Button btnDatePicker = dialog1.findViewById(R.id.btn_time);

                btnDatePicker.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();

                        DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        txtDate.setText( year+ "/" + (monthOfYear + 1) + "/" + dayOfMonth);

                                    }
                                }, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
                        dpd.show();
                    }


                });


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
                        }else if (TextUtils.isEmpty(txtDate.getText().toString())) {
                            txtDate.setError("Please Select Transaction date");
                            focusView = edttrId;
                            cancel = true;
                        }
                        if (cancel) {
                            focusView.requestFocus();
                        } else {
                            dialog1.dismiss();
                            Incert(edtAMT.getText().toString(), edtMode.getText().toString(), edttrId.getText().toString(),txtDate.getText().toString());
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



            }
        });

    }
    class C04225 implements View.OnClickListener {
        C04225() {
        }

        public void onClick(View view) {
            if (Global.A_status.equals("0")) {
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), Agent_Signup.class));
            } else if (Global.A_status.equals("1")) {
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(),BenificiaryList.class));
            } else if (Global.A_status.equals("-1")) {
                Global.diloge(MainActivity.this, "Pending", "Your GNSBOOK wallet is in under verification");
            }
        }
    }

    /* renamed from: com.digital.gnsbook.Activity.MainActivity$6 */
    class C04236 implements View.OnClickListener {
        C04236() {
        }

        public void onClick(View view) {

            if (Global.A_status.equals("0") ) {
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(),Agent_Signup.class));
            } else if (Global.A_status.equals("1")) {
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), Manual_BenificiaryList.class));
            } else if (Global.A_status.equals("-1")) {
                Global.diloge(MainActivity.this, "Pending", "Your GNSBOOK wallet is in under verification");

        }
    }
    }

    private void fundTransfer() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View inflate = getLayoutInflater().inflate(R.layout.transactionmode, null);
        builder.setView(inflate);
        TextView textView = (TextView) inflate.findViewById(R.id.new_FundTrans);
        TextView textView2 = (TextView) inflate.findViewById(R.id.old_FundTrans);
        builder.create().show();
        textView.setOnClickListener(new C04225());
        textView2.setOnClickListener(new C04236());
    }


    private void initialize() {
        Balance = findViewById(R.id.txtBalance);
        uName = findViewById(R.id.uName);
        uEmail = findViewById(R.id.uEmail);
        uMobile = findViewById(R.id.uMobile);
        addMoney = findViewById(R.id.addMoney);
        recyclerView = findViewById(R.id.rvRecenttrance);
        dashrefer = findViewById(R.id.dashrefer);
        txtrefferalid = findViewById(R.id.dshsharefferalid);
        share = findViewById(R.id.dshbtnshrRefferal);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        uName.setText(Global.name);
        uEmail.setText(Global.Email);
        uMobile.setText(Global.mobile);
        txtrefferalid.setText("Refferal code: " + Global.customerid);
        sessionManager = new SessionManager(MainActivity.this);
        db = new SQLiteHandler(MainActivity.this);
        consumbar = findViewById(R.id.balaceProgress);
        icaddmoney = findViewById(R.id.icaddmoney);
        sendmoney = findViewById(R.id.sendmoney);
        withdrawmoney = findViewById(R.id.withdrawmoney);
        vrSHop = findViewById(R.id.vrSHop);
        myShop = findViewById(R.id.myShop);
        notiFication = findViewById(R.id.notiFication);
        support = findViewById(R.id.support);


        vrSHop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ShopProduct.class));
            }
        });
        myShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MyShop.class));
            }
        });
        notiFication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Notification.class));
            }
        });
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),JivoActivity.class));
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_fundraise) {
            // Handle the camera action
            startActivity(new Intent(getApplicationContext(),Mystock.class));
        } else if (id == R.id.nav_shop) {
            startActivity(new Intent(getApplicationContext(),ShopProduct.class));
        }else if (id == R.id.nav_oldLottery) {
            startActivity(new Intent(getApplicationContext(),OldLottery.class));
        }else if (id == R.id.nav_referallist) {
            startActivity(new Intent(getApplicationContext(),ReferralList.class));
        }else if (id == R.id.nav_notification) {
            startActivity(new Intent(getApplicationContext(),Notification.class));
        }else if (id == R.id.nav_myshop) {
            startActivity(new Intent(getApplicationContext(),MyShop.class));
        }else if (id == R.id.nav_Hist) {
            startActivity(new Intent(getApplicationContext(),Recent_Transaction.class));
        }else if (id == R.id.support) {
            callPhoneNumber();


        }else if (id == R.id.nav_logout) {
            Logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == 101)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhoneNumber();
            }
        }
    }

    public void callPhoneNumber()
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }

                Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "7304867675"));
                startActivity(intent1)  ;

            }
            else {
                Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "7304867675"));
                startActivity(intent1)  ;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void getCurrentlottery() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, APIs.recent_lottery, new Response.Listener < String > () {@Override
        public void onResponse(String response) {

            progressDialog.dismiss();

            try {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("status")) {

                    JSONArray object = jsonObject.getJSONArray("result");
                    JSONObject Details = object.getJSONObject(0);

                    Global.ltryId = Details.getString("lottery_id");
                } else {
                    Global.diloge(MainActivity.this, "Error", jsonObject.getString("result"));
                }

            } catch(JSONException e) {
                e.printStackTrace();
            }

        }
        },
                new Response.ErrorListener() {@Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                }
                });
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void showWinner() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.shownotification, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.getBoolean("status")) {


                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject data = jsonArray.getJSONObject(0);

                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                        View mView = layoutInflaterAndroid.inflate(R.layout.notification_dialog, null);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        TextView text = mView.findViewById(R.id.rewardDesc);
                        TextView winamt = mView.findViewById(R.id.winamt);
                        ImageView closewinpop = mView.findViewById(R.id.closewinpop);
                        String[] f = data.getString("f_price").split("\\s*,\\s*");
                        String[] s = data.getString("s_price").split("\\s*,\\s*");
                        String[] t = data.getString("t_price").split("\\s*,\\s*");


                        if (data.getInt("status") ==  11){
                            Prize = Global.CurrencyFormat(f[0]);
                        }else if (data.getInt("status") ==  12){
                            Prize = Global.CurrencyFormat(s[0]);
                        }else if (data.getInt("status") ==  13){
                            Prize = Global.CurrencyFormat(t[0]);
                        }

                        text.setText("Congratulations to \n" + "Mr/Miss "+data.getString("name") + " " + " Has won prize of Rupees ");
                        winamt.setText("Rs "+Prize);

                        builder.setView(mView);



                        final AlertDialog alert = builder.create();


                        final Window dialogWindow = alert.getWindow();
                        final WindowManager.LayoutParams dialogWindowAttributes = dialogWindow.getAttributes();
                        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alert.show();

                        closewinpop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    alert.dismiss();
                            }
                        });

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
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }


    private void checkUpdate() {
        StringRequest request =new StringRequest(StringRequest.Method.GET, APIs.update, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject =new JSONObject(response);

                    if (jsonObject.getInt("status") == 1 ){

                        JSONObject object =jsonObject.getJSONObject("result");
                        NewVersion =object.getDouble("version");
                        System.out.println(NewVersion);
                        final String link=object.getString("link");
                        System.out.println(link);




                        Double i = Double.valueOf(currentversion);
                        if (i < NewVersion)  {
                            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                            View mView = layoutInflaterAndroid.inflate(R.layout.updateapp, null);
                            android.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.app.AlertDialog.Builder(MainActivity.this);
                            alertDialogBuilderUserInput.setView(mView);
                            TextView button=mView.findViewById(R.id.UpdApp);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    session.setLogin(false);
                                    db.deleteUsers();
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(link));
                                    startActivity(intent);
                                }
                            });
                            final android.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                            alertDialogAndroid.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            alertDialogAndroid.setCancelable(false);
                            alertDialogAndroid.show();
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        }){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        AppController.getInstance().addToRequestQueue(request);

    }

    private void getData() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        refreshLayout.setRefreshing(false);

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.balance_detail, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        JSONArray object = jsonObject.getJSONArray("result");
                        JSONObject Details = object.getJSONObject(0);

                        String number = Details.getString("balance");
                        double amount = Double.parseDouble(number);
                        DecimalFormat formatter = new DecimalFormat("#,###.00");
                        String formatted = formatter.format(amount);

                        Balance.setText(formatted);
                        float Totalvalue = Float.parseFloat(Details.getString("balance"));
                        float consumepercent = (Totalvalue / 100000) * 100;
                        consumbar.setProgress(consumepercent);

                    } else {
                        Global.diloge(MainActivity.this, "Login Error", jsonObject.getString("result"));
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
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void getTransactionData() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.stocktransaction_detail, new Response.Listener<String>() {
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

                        transactionModels.add(model);
                    }

                    recyclerView.setAdapter(new RecyclerView.Adapter() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.transhistadapt, parent, false);
                            Holder holder = new Holder(view);
                            return holder;
                        }

                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hold, int position) {

                            Holder holder = (Holder) hold;
                            TransactionModel model = transactionModels.get(position);
                            holder.date.setText(model.date);
                            holder.id.setText(model.trID);
                            holder.details.setText(model.details);
                            holder.status.setTag(model);


                            if (!model.credit.equals("null") && !model.credit.equals("0") ) {

                                String number = model.credit;
                                double amount = Double.parseDouble(number);
                                DecimalFormat formatter = new DecimalFormat("#,###.0");
                                String formatted = formatter.format(amount);

                                holder.amount.setText("₹" + formatted);
                                holder.amount.setTextColor(Color.parseColor("#57d843"));
                                holder.status.setText("CREDITED");
                                holder.status.setTextColor(Color.parseColor("#57d843"));
                            } else if (!model.debit.equals("null") && !model.debit.equals("0")) {

                                String number = model.debit;
                                double amount = Double.parseDouble(number);
                                DecimalFormat formatter = new DecimalFormat("#,###.0");
                                String formatted = formatter.format(amount);

                                holder.amount.setText("₹" + formatted);
                                holder.amount.setTextColor(Color.parseColor("#57d843"));
                                holder.status.setText("PURCHASED");
                                holder.status.setTextColor(Color.parseColor("#57d843"));
                            }



                        }

                        @Override
                        public int getItemCount() {
                            return transactionModels.size();
                        }

                        class Holder extends RecyclerView.ViewHolder {
                            TextView date, id, amount, details ,status;

                            public Holder(@NonNull View itemView) {
                                super(itemView);

                                amount = itemView.findViewById(R.id.tramount);
                                id = itemView.findViewById(R.id.trId);
                                details = itemView.findViewById(R.id.trdetail);
                                date = itemView.findViewById(R.id.trdate);
                                status = itemView.findViewById(R.id.trstat);

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

    private void addMoneytoVS() {



        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.ic_invoice);
        builderSingle.setTitle("Select Transection Mode:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
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
                    final Dialog dialog1 = new Dialog(MainActivity.this);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog1.setCancelable(false);
                    dialog1.setContentView(R.layout.manualtransfrom);
                    Button submit = dialog1.findViewById(R.id.submit);
                    ImageView close = dialog1.findViewById(R.id.close);
                    final EditText edtAMT = dialog1.findViewById(R.id.mnAMT);
                    final EditText edtMode = dialog1.findViewById(R.id.mnMode);
                    final EditText edttrId = dialog1.findViewById(R.id.mnTrid);
                    final EditText txtDate = dialog1.findViewById(R.id.in_time);
                    final Button btnDatePicker = dialog1.findViewById(R.id.btn_time);

                    btnDatePicker.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View view) {
                            final Calendar c = Calendar.getInstance();

                            DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            txtDate.setText( year+ "/" + (monthOfYear + 1) + "/" + dayOfMonth);

                                        }
                                    }, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
                            dpd.show();
                        }


                    });


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
                            }else if (TextUtils.isEmpty(txtDate.getText().toString())) {
                                txtDate.setError("Please Select Transaction date");
                                focusView = edttrId;
                                cancel = true;
                            }
                            if (cancel) {
                                focusView.requestFocus();
                            } else {
                                dialog1.dismiss();
                                Incert(edtAMT.getText().toString(), edtMode.getText().toString(), edttrId.getText().toString(),txtDate.getText().toString());
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

                    startActivity(new Intent(MainActivity.this , LoadMoney.class));

                }
            }
        });
        builderSingle.show();
    }

    private void Incert(final String s, final String s1, final String s2, final String string) {

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
                            builder = new android.app.AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                        } else {
                            builder = new android.app.AlertDialog.Builder(MainActivity.this);
                        }
                        builder.setCancelable(false);
                        builder.setTitle("Transaction Done")
                                .setMessage(jsonObject.getString("result"))
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                                        finish();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    } else {
                        JSONArray object = jsonObject.getJSONArray("result");
                        Global.diloge(MainActivity.this, "Login Error", object.getString(0));
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
                param.put("date", string);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void Logout() {
        session.setLogin(false);
        db.deleteUsers();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class ));
    }
    private void UpdateDeviceToken() {
        StringRequest request =new StringRequest(StringRequest.Method.POST, APIs.devicetoken, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject =new JSONObject(response);
                    System.out.println(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        }){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String>  params = new HashMap<String, String>();

                params.put("customer_id", Global.customerid);
                params.put("token",notificationToken);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(request);
    }

}
