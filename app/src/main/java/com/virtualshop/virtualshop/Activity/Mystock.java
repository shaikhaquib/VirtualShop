package com.virtualshop.virtualshop.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Firebase.Fcm;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.Model.ResultItem;
import com.virtualshop.virtualshop.Model.StockModel;
import com.virtualshop.virtualshop.Model.StockPojo;
import com.virtualshop.virtualshop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mystock extends AppCompatActivity {

    RecyclerView recyclerView;
 //   ArrayList<StockModel.Result> models = new ArrayList<>();
    ProgressDialog progressDialog ;
    String stramount ;
    StockPojo dashResp;
    private List<StockModel.Result> models;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystock);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading..");
        progressDialog = new ProgressDialog(Mystock.this);
        recyclerView = findViewById(R.id.rvMystock);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        getDatalist();
    }

    private void getDatalist() {

        dialog.show();
        StringRequest request = new StringRequest(StringRequest.Method.POST, APIs.Mystock, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);


                    if (!object.getString("result").equals("No Data Available")) {
                        Gson gson = new Gson();
                        StockModel notificationResponse = gson.fromJson(response, StockModel.class);
                        models = notificationResponse.getResult();

                        recyclerView.setAdapter(new RecyclerView.Adapter() {
                            @NonNull
                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                                View view = LayoutInflater.from(Mystock.this).inflate(R.layout.stock, viewGroup, false);
                                Holder holder = new Holder(view);
                                return holder;
                            }

                            @Override
                            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
                                final Holder holder = (Holder) viewHolder;
                                final StockModel.Result model = models.get(position);

                                holder.buy_sp.setText("Buying Price : ₹ " + String.valueOf(model.getBuySp()));
                                //      holder.name.setText(model.productName);
                                holder.sell_sp.setText("Selling Price : ₹" + String.valueOf(model.getSellSp()));
                                holder.available_stock.setText("Available Stock : " + String.valueOf(model.getAvailableStock()));
                                holder.total_stock.setText("Purchased Stock : " + String.valueOf(model.getPurchasedStock()));
                                holder.BUY.setText("Resell Stock");

                                holder.BUY.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Mystock.this);
                                        View mView = layoutInflaterAndroid.inflate(R.layout.resellstock, null);
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(Mystock.this);

                                        final TextView availablestock = mView.findViewById(R.id.tsavailblestock);
                                        final TextView prstockprice = mView.findViewById(R.id.tsstockprice);
                                        final EditText stocktosell = mView.findViewById(R.id.tsnofstock);
                                        final EditText stockprice = mView.findViewById(R.id.tsamtofstock);
                                        final TextView profit = mView.findViewById(R.id.tstoatalamount);
                                        final Button btnSell = mView.findViewById(R.id.sellStock);


                                        builder.setView(mView);
                                        final AlertDialog alert = builder.create();


                                        final Window dialogWindow = alert.getWindow();
                                        final WindowManager.LayoutParams dialogWindowAttributes = dialogWindow.getAttributes();
                                        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        alert.show();


                                        availablestock.setText(String.valueOf(model.getAvailableStock()));
                                        prstockprice.setText("₹" + String.valueOf(model.getBuySp()));

                                        stockprice.addTextChangedListener(new TextWatcher() {
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
                                                    int price = Integer.parseInt(editable.toString());
                                                    int stock = Integer.parseInt(stocktosell.getText().toString());
                                                    final int A = stock * model.getBuySp();

                                                    int B = price * stock;
                                                    int finalAmt = B - A;
                                                    profit.setText(String.valueOf(finalAmt));

                                                }

                                            }

                                        });

                                        btnSell.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {


                                                if (!stocktosell.getText().toString().equals("") && !stockprice.getText().toString().equals("")) {
                                                    int stock = Integer.parseInt(stocktosell.getText().toString());
                                                    int amount = Integer.parseInt(stockprice.getText().toString());
                                                    if (stock <= model.getAvailableStock()) {

                                                        if (amount > 0) {
                                                            Sell(model.getTradeId(), model.getSellSp(), amount, stock, stock, profit.getText().toString(), model.getCustomerStockId(), model.getCompanyStockId());
                                                        } else {
                                                            Global.diloge(Mystock.this, "Error ", "Please enter valid Amount");
                                                        }

                                                    } else {
                                                        Global.diloge(Mystock.this, "Error ", "You have only " + model.getAvailableStock() + " you cant sell more than it.");
                                                    }
                                                } else {
                                                    Global.diloge(Mystock.this, "Error ", "Please enter valid details");
                                                }

                                            }
                                        });


                                    }
                                });


                            }

                            @Override
                            public int getItemCount() {
                                return models.size();
                            }


                            class Holder extends RecyclerView.ViewHolder {
                                TextView name, available_stock, buy_sp,
                                        total_stock, sell_sp, prstrstckpr,
                                        prstotlstckpr, pravblstck,
                                        prtotlstck, expsellingrate, BUY, learnmore;

                                ImageView image;
                                ProgressBar progressBar;
                                CardView productCard;
                                LinearLayout layout;

                                public Holder(@NonNull View itemView) {
                                    super(itemView);

                                    available_stock = itemView.findViewById(R.id.availablestock);
                                    total_stock = itemView.findViewById(R.id.totalstock);
                                    buy_sp = itemView.findViewById(R.id.buyingprice);
                                    sell_sp = itemView.findViewById(R.id.sellingprice);
                                    name = itemView.findViewById(R.id.pname);
                                    BUY = itemView.findViewById(R.id.prBuy);

//                                    image = itemView.findViewById(R.id.campaignimage);
//                                  progressBar = itemView.findViewById(R.id.imageLoader);
                                }
                            }

                        });

                    }
                    } catch(JSONException e){
                        e.printStackTrace();
                    }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("customer_id",Global.customerid);
                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

    }

    private void Sell(final String tradeId, final Integer buySp, final int amount, final int stock, final int stock1, final String finalprofit, final String customerStockId, final Integer companyStockId) {

        dialog.show();


        StringRequest request = new StringRequest(StringRequest.Method.POST, APIs.  Resell, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")){
                        Global.successDilogue(Mystock.this,"You have successfully Resell stock ");
                        new Fcm().execute(Global.customerid,"Resell Stock","You have successfully buy stock ");

                    }else {
                        Global.failedDilogue(Mystock.this , jsonObject.getString("result"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("customer_id",Global.customerid);
                param.put("trade_id",tradeId);
                param.put("buy_sp", String.valueOf(buySp));
                param.put("sell_sp", String.valueOf(amount));
                param.put("available_stock", String.valueOf(stock));
                param.put("purchased_stock", String.valueOf(stock1));
                param.put("final_amount",finalprofit);
                param.put("customer_stock_id",customerStockId);
                param.put("company_stock_id", String.valueOf(companyStockId));
                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(request);

    }
}
