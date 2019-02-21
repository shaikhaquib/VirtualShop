package com.virtualshop.virtualshop.Fragement;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Firebase.Fcm;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.Model.ResultItem;
import com.virtualshop.virtualshop.Model.StockPojo;
import com.virtualshop.virtualshop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frg_Product extends Fragment {

    RecyclerView recyclerView;
    ProgressDialog progressDialog ;
    String stramount ;
    StockPojo dashResp;
    private List<ResultItem> Result =new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view =inflater.inflate(R.layout.activity_virtualshop,container,false);
    progressDialog = new ProgressDialog(getActivity());
    recyclerView = view.findViewById(R.id.rvProduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    getDatalist();
        return view;
}


    private void getDatalist() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.get_stock, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getBoolean("status")){

                        JSONArray array=jsonObject.getJSONArray("result");
                        for (int i = 0 ; i < array.length() ; i++)
                        {
                            ResultItem resultItem = new ResultItem();
                            JSONObject jsonObj = array.getJSONObject(i);

                            resultItem.tradeId        = jsonObj.getString("trade_id");
                            resultItem.customer_id    = jsonObj.getString("customer_id");
                            resultItem.createdAt      = jsonObj.getString("created_at");
                            resultItem.productName    = jsonObj.getString("c_product_name");
                            resultItem.companyStockId = jsonObj.getString("company_stock_id");
                            resultItem.sellSp         = jsonObj.getInt("sell_sp");
                            resultItem.sellingPrice   = jsonObj.getInt("c_sell_sp");
                            resultItem.availablestock = jsonObj.getInt("available_stock");
                           // resultItem.totalQty       = jsonObj.getInt("total_qty");
                           // resultItem.qtyPerStock    = jsonObj.getInt("qty_per_stock");
                           // resultItem.availableQty   = jsonObj.getInt("available_qty");
                            resultItem.gst            = jsonObj.getInt("gst");
                            resultItem.totalStock     = jsonObj.getInt("total_stock");
                            resultItem.buySp          = jsonObj.getInt("buy_sp");
                           // resultItem.price          = jsonObj.getInt("price");
                            resultItem.productId      = jsonObj.getInt("c_id");
                            resultItem.id             = jsonObj.getInt("id");
                            resultItem.status         = jsonObj.getInt("status");
                            resultItem.customerStockId= jsonObj.getString("customer_stock_id");

                            Result.add(resultItem);
                        }
                        Collections.sort(Result, new Sortbyroll());
                        recyclerView.setAdapter(new RecyclerView.Adapter() {
                            @NonNull
                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(getActivity()).inflate(R.layout.stock, parent, false);
                                Holder holder = new Holder(view);
                                return holder;
                            }

                            @Override
                            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hold, int position) {

                                final Holder holder =(Holder) hold;

                                final  ResultItem  model =Result.get(position);

                                holder.buy_sp.setText("Buying Price : ₹ " +String.valueOf(model.buySp));
                                holder.name.setText(model.productName);
                                holder.sell_sp.setText("Selling Price : ₹" +String.valueOf(model.sellSp));
                                holder.available_stock.setText("Available Stock : " +String.valueOf(model.availablestock));
                                holder.total_stock.setText("Total Stock : " +String.valueOf(model.totalStock));
                                holder.BUY.setTag(model);

                                if (model.availablestock == 0 ){
                                    holder.BUY.setText("SOLD OUT");
                                    holder.BUY.setEnabled(false);
                                }
                                if (model.status == -1 ){
                                    holder.BUY.setText("INACTIVE");
                                    holder.BUY.setEnabled(false);
                                }




                                holder.BUY.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
                                        View mView = layoutInflaterAndroid.inflate(R.layout.buy_stock, null);
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                        final EditText prdAmount = mView.findViewById(R.id.trnofstock);
                                        final TextView GST  = mView.findViewById(R.id.trgstTotal);
                                        final TextView name  = mView.findViewById(R.id.stockname);
                                        final TextView availablestock  = mView.findViewById(R.id.travailblestock);
                                        final TextView stockprice  = mView.findViewById(R.id.trstockprice);
                                        final TextView totalamunt  = mView.findViewById(R.id.trtotal);
                                        //final TextView amt  = mView.findViewById(R.id.prdamt);
                                        final TextView gsttext  = mView.findViewById(R.id.trgstText);
                                        gsttext.setText("Total GST ("+ String.valueOf(model.gst) +"%)");
                                        name.setText(model.productName);
                                        final TextView finalamt     = mView.findViewById(R.id.trtoatalamountgst);


                                        availablestock.setText(String.valueOf(model.availablestock));
                                        stockprice.setText(String.valueOf(model.buySp));

                                        final Button submit = mView.findViewById(R.id.buystock);

                                        GST.setEnabled(false);
                                        finalamt.setEnabled(false);
                                        GST.setKeyListener(null);
                                        finalamt.setKeyListener(null);
                                        builder.setView(mView);

                                        prdAmount.addTextChangedListener(new TextWatcher() {
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
                                                    int Stockqty = Integer.parseInt(editable.toString());
                                                    if (Stockqty <= model.availablestock && Stockqty > 0) {
                                                        int tota = Stockqty * model.buySp;
                                                        totalamunt.setText(String.valueOf(tota));


                                                        double amount = Double.parseDouble(String.valueOf(tota));
                                                        double res = (amount / 100.0f) * model.gst;
                                                        GST.setText(String.valueOf(res));
                                                        // amt.setText(editable.toString());

                                                        double total = res + tota;
                                                        stramount = String.valueOf(total);

                                                        finalamt.setText(stramount);
                                                        //  total.setText(amount - res + "");
                                                    }else {


                                                            prdAmount.setError("Enter Valid Stock");
                                                            prdAmount.requestFocus();

                                                    }
                                                }else {

                                                    prdAmount.setError("Enter Valid Stock");
                                                    prdAmount.requestFocus();
                                                }
                                            }

                                        });



                                        final AlertDialog alert = builder.create();


                                        final Window dialogWindow = alert.getWindow();
                                        final WindowManager.LayoutParams dialogWindowAttributes = dialogWindow.getAttributes();
                                        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        alert.show();


                                        submit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                alert.dismiss();
                                               int amount = 0;
                                                if (!prdAmount.getText().toString().equals("")){
                                                 amount = Integer.parseInt(prdAmount.getText().toString());}


                                                if (model.status == 1) {

                                                    if (!prdAmount.getText().toString().equals("") && amount > 0) {

                                                        if (amount<= model.availablestock) {


                                                            android.app.AlertDialog.Builder builder;
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                builder = new android.app.AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
                                                            } else {
                                                                builder = new android.app.AlertDialog.Builder(getActivity());
                                                            }
                                                            builder.setCancelable(false);
                                                            builder.setTitle("Confirm Amount")
                                                                    .setMessage("\nTotal Amount = RS " + totalamunt.getText().toString() + "\n \n" + "Total GST ( " + model.gst + "%) = RS " + GST.getText().toString() + "\n" + "--------------------------------------------\n" + "Final  Amount   =   RS " + finalamt.getText().toString() + "\n \n" + "Are you sure do you Want to continue ! \nAfter Confirmation RS " + finalamt.getText().toString() + " Amount will be deduct from your virtual shop account.")
                                                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                            buy(model.tradeId, prdAmount.getText().toString(), model.buySp, model.sellSp, model.availablestock, model.customerStockId, model.customer_id, finalamt.getText().toString(), model.companyStockId);
                                                                        }
                                                                    })
                                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                                    .show();
                                                        }else {

                                                            Global.diloge(getActivity(), "Error Out of Stock !", "Only " +String.valueOf(model.availablestock)+ " Stocks available .");

                                                        }


                                                    } else {
                                                        Global.diloge(getActivity(), "Error", "Please enter stock");

                                                    }
                                                }else {

                                                    Global.diloge(getActivity()," Stock Error! " , "Sorry for the inconvenience ! Stock is currently InActive. \n Please contact support for the more detail..");
                                                }



                                            }
                                        });


                                    }
                                });


                            }

                            @Override
                            public int getItemCount() {
                                return Result.size();
                            }

                            class Holder extends  RecyclerView.ViewHolder {
                                TextView name ,available_stock,buy_sp,
                                        total_stock ,sell_sp ,prstrstckpr,
                                        prstotlstckpr ,pravblstck,
                                        prtotlstck,expsellingrate
                                        ,BUY ,learnmore;

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
                param.put("p_type","2");
                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);


    }

    private void buy(final String tradeId, final String s, final int buySp, final int sellSp, final int availablestock, final String customerStockId, final String seller_customerid, final String final_amount, final String companyStockId) {
        progressDialog.setMessage("SENDING REQUEST");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.BUY_STock, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")){
                        Global.successDilogue(getActivity(),"You have successfully buy product stock ");
                        new Fcm().execute(Global.customerid,"Buy Stock","You have successfully buy product stock ");

                    }else {
                        Global.failedDilogue(getActivity() , jsonObject.getString("result"));
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
                param.put("buyer_customer_id",Global.customerid);
                param.put("seller_customer_id",seller_customerid);
                param.put("trade_id",tradeId);
                param.put("buy_sp", String.valueOf(buySp));
                param.put("sell_sp",String.valueOf(sellSp));
                param.put("purchased_stock",s);
                param.put("available_stock", String.valueOf(availablestock));
                param.put("customer_stock_id", String.valueOf(customerStockId));
                param.put("final_amount", final_amount);
                param.put("company_stock_id", companyStockId);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

     class Sortbyroll implements Comparator<ResultItem>
    {
        // Used for sorting in ascending order of
        // roll number
        @Override
        public int compare(ResultItem o1, ResultItem o2) {
            return o2.availablestock-o1.availablestock  ;
        }
    }
}
