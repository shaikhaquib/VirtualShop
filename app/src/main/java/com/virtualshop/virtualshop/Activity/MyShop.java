package com.virtualshop.virtualshop.Activity;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.Model.ProductModel;
import com.virtualshop.virtualshop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyShop extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ProductModel> models = new ArrayList<>();
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myshop);

        ActionBar abar = getSupportActionBar();

        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("MYSHOP");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setHomeButtonEnabled(true);


        progressDialog = new ProgressDialog(MyShop.this);
        recyclerView = findViewById(R.id.rvmyproduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyShop.this));
        /*FloatingActionButton fab = view.findViewById(R.id.fabHome);
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setBehavior(new FAB_Float_on_Scroll());
        fab.setLayoutParams(p);
*/
        getDatalist();

    }

    private void getDatalist() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.Myshop, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("status")){


                        JSONArray jsonArray = jsonObject.getJSONArray("result");

                        for (int i = 0 ; i < jsonArray.length() ; i++){

                            JSONObject object = jsonArray.getJSONObject(i);
                            ProductModel productModel = new ProductModel();


                            productModel.date = object.getString("date");
                            productModel.img = object.getString("p_link");
                            productModel.name = object.getString("p_name");
                            productModel.id = object.getString("product_id");
                            productModel.status = object.getString("status");
                            productModel.rate = object.getString("rate");
                            productModel.current_rate = object.getString("current_rate");
                            productModel.s_stock_price = object.getString("s_stock_price");
                            productModel.t_stock_price = object.getString("t_stock_price");
                            productModel.available_stock = object.getString("stock");
                            productModel.total_stock = object.getString("total_stock");
                            productModel.expected_date = object.getString("created_at");
                            productModel.expected_m_s_rate = object.getString("expected_m_s_rate");
                            productModel.description = object.getString("description");

                            models.add(productModel);

                        }
                        recyclerView.setAdapter(new RecyclerView.Adapter() {
                            @NonNull
                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(MyShop.this).inflate(R.layout.mainadpt, parent, false);
                                Holder holder = new Holder(view);
                                return holder;
                            }

                            @Override
                            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hold, int position) {

                                final Holder holder =(Holder) hold;
                                final ProductModel model=models.get(position);

                                holder.name.setText(model.name);
                                System.out.println("Name AT" + position + "is" +model.name) ;
                                String[] splited = model.date.split("\\s+");
                                holder.date.setText("Date : "+splited[0]);
                                holder.prRate.setText("Rate : "+model.rate);
                                holder.prCrate.setText("Current Rate : "+model.current_rate);
                                holder.prDesc.setText(model.description);
                                holder.prDesc.setVisibility(View.GONE);
                                holder.prstrstckpr.setVisibility(View.VISIBLE);
                                holder.prstrstckpr.setText("Exp Date : "+model.expected_date);
                                holder.prstotlstckpr.setVisibility(View.GONE);
                                holder.pravblstck.setText("You have own : "+model.available_stock + " Stocks");
                                holder.pravblstck.setTextSize(16);
                                holder.pravblstck.setTypeface(null, Typeface.BOLD);
                                holder.prtotlstck.setVisibility(View.GONE);
                                holder.expsellingrate.setText("Expected Monthly Selling Rate : "+model.expected_m_s_rate);
                                holder.expsellingrate.setVisibility(View.GONE);
                                holder.layout.setTag(model);
                                holder.layout.setVisibility(View.VISIBLE);
                                holder.productCard.setTag(model);
                                holder.BUY.setTag(model);
                                holder.BUY.setVisibility(View.GONE);
                                holder.learnmore.setVisibility(View.GONE);
                                holder.lastPayment.setVisibility(View.VISIBLE);
                                holder.nextPayment.setVisibility(View.VISIBLE);
                                holder.paymentDetail.setTextColor(getResources().getColor(R.color.colorPrimary));
                                holder.paymentDetail.setText("Get PaymentDetails");
                                holder.paymentDetail.setTag(model);
                                holder.paymentDetail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String[] splited = model.date.split("\\s+");
                                        getPaymentDetail(Global.userid,model.id,splited[0],holder.lastPayment,holder.nextPayment);
                                    }
                                });

                            }

                            @Override
                            public int getItemCount() {
                                return models.size();
                            }

                            class Holder extends  RecyclerView.ViewHolder {
                                TextView name ,prRate,prCrate, date ,prDesc ,prstrstckpr, prstotlstckpr ,pravblstck,prtotlstck,expsellingrate ,BUY ,learnmore ,paymentDetail , lastPayment ,nextPayment;

                                ImageView image;
                                ProgressBar progressBar;
                                CardView productCard;
                                LinearLayout layout;
                                public Holder(@NonNull View itemView) {
                                    super(itemView);

                                    name = itemView.findViewById(R.id.campaignName);
                                    date = itemView.findViewById(R.id.prexpdate);
                                    prRate = itemView.findViewById(R.id.prRate);
                                    prCrate = itemView.findViewById(R.id.prCrate);
                                    productCard = itemView.findViewById(R.id.productCard);
                                    prDesc = itemView.findViewById(R.id.prDesc);
                                    prstrstckpr = itemView.findViewById(R.id.expdate);
                                    prstotlstckpr = itemView.findViewById(R.id.prstotlstckpr);
                                    pravblstck = itemView.findViewById(R.id.pravblstck);
                                    BUY = itemView.findViewById(R.id.prBuy);
                                    prtotlstck = itemView.findViewById(R.id.prtotlstck);
                                    expsellingrate = itemView.findViewById(R.id.expsellingrate);
                                    learnmore = itemView.findViewById(R.id.learnmore);
                                    layout = itemView.findViewById(R.id.productExtra);
                                    paymentDetail = itemView.findViewById(R.id.prstrstckpr);
                                    lastPayment = itemView.findViewById(R.id.lastpayment);
                                    nextPayment = itemView.findViewById(R.id.nextpayment);
//                                    image = itemView.findViewById(R.id.campaignimage);
//                                  progressBar = itemView.findViewById(R.id.imageLoader);
                                }
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> param = new HashMap<String,String>();
                param.put("customer_id",Global.customerid);
                    return param;
            }

        };

        AppController.getInstance().addToRequestQueue(stringRequest);


    }

    private void getPaymentDetail(String userid, final String id, final String date, final TextView lastPayment, final TextView nextPayment) {


        {

            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.stock_payment, new Response.Listener<String>() {


                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("status")){

                            if (jsonObject.has("result")){

                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                JSONObject lastPayemntdetail  = jsonArray.getJSONObject(jsonArray.length()-1);

                                lastPayment.setText("Last Payment : "+lastPayemntdetail.getString("date"));

                            }else {
                                lastPayment.setText("Last Payment : -- ");

                            }
                            nextPayment.setText("Next Payment : "+jsonObject.getString("date"));

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
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map <String,String> param = new HashMap<String,String>();
                    param.put("customer_id",Global.customerid);
                    param.put("product_id",id);
                    param.put("date",date);
                    return param;
                }

            };

            AppController.getInstance().addToRequestQueue(stringRequest);


        }

    }

}
