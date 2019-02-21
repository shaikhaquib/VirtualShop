package com.virtualshop.virtualshop.Fragement;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.text.TextUtils;
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
import com.virtualshop.virtualshop.Model.ProductModel;
import com.virtualshop.virtualshop.Model.TransactionModel;
import com.virtualshop.virtualshop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Frg_Category extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ProductModel> models = new ArrayList<>();
    ProgressDialog progressDialog ;
    String stramount ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.activity_virtualshop,container,false);
        progressDialog = new ProgressDialog(getActivity());
        recyclerView = view.findViewById(R.id.rvProduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*FloatingActionButton fab = view.findViewById(R.id.fabHome);
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setBehavior(new FAB_Float_on_Scroll());
        fab.setLayoutParams(p);
*/
        getDatalist();
        return view;
    }


    private void getDatalist() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.ProductlistAPI, new Response.Listener<String>() {


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


                            productModel.date = object.getString("created_at");
                            productModel.img = object.getString("p_link");
                            productModel.name = object.getString("p_name");
                            productModel.id = object.getString("id");
                            productModel.status = object.getString("status");
                            productModel.rate = object.getString("rate");
                            productModel.current_rate = object.getString("current_rate");
                            productModel.s_stock_price = object.getString("s_stock_price");
                            productModel.t_stock_price = object.getString("t_stock_price");
                            productModel.available_stock = object.getString("available_stock");
                            productModel.total_stock = object.getString("total_stock");
                            productModel.expected_date = object.getString("expected_date");
                            productModel.expected_m_s_rate = object.getString("expected_m_s_rate");
                            productModel.description = object.getString("description");
                            productModel.gst = object.getInt("gst");

                            models.add(productModel);

                        }

                        recyclerView.setAdapter(new RecyclerView.Adapter() {
                            @NonNull
                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(getActivity()).inflate(R.layout.mainadpt, parent, false);
                                Holder holder = new Holder(view);
                                return holder;
                            }

                            @Override
                            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hold, int position) {

                                final Holder holder =(Holder) hold;
                                final ProductModel model=models.get(position);

                                holder.name.setText(model.name);
                                System.out.println("Name AT" + position + "is" +model.name) ;
                                holder.date.setText("Exp Date : "+model.date);
                                holder.prRate.setText("Rate : "+model.rate);
                                holder.prCrate.setText("Current Rate : "+model.current_rate);
                                holder.prDesc.setText(model.description);
                                holder.prstrstckpr.setText("starting Stock Price : ₹"+model.s_stock_price);
                                holder.prstotlstckpr.setText("Total Stock Price : ₹"+model.t_stock_price);
                                holder.pravblstck.setText("Available Stock : "+model.available_stock);
                                holder.prtotlstck.setText("Total Stock : "+model.total_stock);
                                holder.expsellingrate.setText("Expected Monthly Selling Rate : "+model.expected_m_s_rate);

                                holder.layout.setTag(model);
                                holder.productCard.setTag(model);
                                holder.BUY.setTag(model);

                                holder.BUY.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
                                        View mView = layoutInflaterAndroid.inflate(R.layout.buy_prompt, null);
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                        ImageView closewinpop = mView.findViewById(R.id.byclose);
                                        final EditText prdAmount = mView.findViewById(R.id.prdAmount);
                                        final EditText GST  = mView.findViewById(R.id.prdgst);
                                        final TextView amt  = mView.findViewById(R.id.prdamt);
                                        final TextView gsttext  = mView.findViewById(R.id.totalgsttext);
                                        gsttext.setText("Total GST ("+ String.valueOf(model.gst) +"%)   = RS");
                                        final EditText totalamt     = mView.findViewById(R.id.prdtotalamt);

                                        final Button submit = mView.findViewById(R.id.btnSubmit);

                                        GST.setEnabled(false);
                                        totalamt.setEnabled(false);
                                        GST.setKeyListener(null);
                                        totalamt.setKeyListener(null);

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
                                                    int i = Integer.parseInt(editable.toString());

                                                    double amount = Double.parseDouble(editable.toString());
                                                    double res = (amount / 100.0f) * model.gst;
                                                    GST.setText(String.valueOf(res) );
                                                    amt.setText(editable.toString());

                                                    double total = res + i;
                                                    stramount =String.valueOf(total);
                                                    totalamt.setText(stramount);
                                                    //  total.setText(amount - res + "");
                                                }
                                            }

                                        });



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

                                        submit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                alert.dismiss();

                                                int min = Integer.parseInt(model.s_stock_price);
                                                int max = Integer.parseInt(model.t_stock_price);
                                                if (!prdAmount.getText().toString().equals("") )
                                                {
                                                    int amount = Integer.parseInt(prdAmount.getText().toString());

                                                    if (amount >= min && amount >0 ){
                                                        if (amount  < max ){



                                                            android.app.AlertDialog.Builder builder;
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                builder = new android.app.AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
                                                            } else {
                                                                builder = new android.app.AlertDialog.Builder(getActivity());
                                                            }
                                                            builder.setCancelable(false);
                                                            builder.setTitle("Confirm Amount")
                                                                    .setMessage("\nEntered Amount = RS "+prdAmount.getText().toString() + "\n \n" + "Total GST (18%) = RS " + GST.getText().toString() + "\n" +"--------------------------------------------\n"+ "Total  Amount   =   RS "+totalamt.getText().toString() +"\n \n" + "Are you sure do you Want to continue ! \nAfter Confirmation RS " +totalamt.getText().toString() +" Amount will be deduct from your virtual shop account.")
                                                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                            buy(prdAmount.getText().toString() , model.id ,GST.getText().toString());
                                                                        }
                                                                    })
                                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                                    .show();



                                                        }else {
                                                            Global.diloge(getActivity() , "Error" , "Amount must be less then total stock price");
                                                        }
                                                    }else {
                                                        Global.diloge(getActivity() , "Error" , "Amount must be greater then total stock price");

                                                    }
                                                }else {
                                                    Global.diloge(getActivity() , "Error" , "Please enter amount");

                                                }




                                            }
                                        });


                                    }
                                });

                                holder.learnmore  .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!model.Visible){
                                            model.Visible = true;
                                            holder.layout.setVisibility(View.VISIBLE);
                                        }else if (model.Visible){
                                            model.Visible = false;
                                            holder.layout.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }

                            @Override
                            public int getItemCount() {
                                return models.size();
                            }

                            class Holder extends  RecyclerView.ViewHolder {
                                TextView name ,prRate,prCrate, date ,prDesc ,prstrstckpr, prstotlstckpr ,pravblstck,prtotlstck,expsellingrate ,BUY ,learnmore;

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
                                    prstrstckpr = itemView.findViewById(R.id.prstrstckpr);
                                    prstotlstckpr = itemView.findViewById(R.id.prstotlstckpr);
                                    pravblstck = itemView.findViewById(R.id.pravblstck);
                                    BUY = itemView.findViewById(R.id.prBuy);
                                    prtotlstck = itemView.findViewById(R.id.prtotlstck);
                                    expsellingrate = itemView.findViewById(R.id.expsellingrate);
                                    learnmore = itemView.findViewById(R.id.learnmore);
                                    layout = itemView.findViewById(R.id.productExtra);
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
                param.put("p_type","1");
                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);


    }

    private void buy(final String amount, final String id, final String gst) {
        progressDialog.setMessage("SENDING REQUEST");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, APIs.BUY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")){
                        Global.successDilogue(getActivity(),jsonObject.getString("result"));
                        new Fcm().execute(Global.customerid,jsonObject.getString("result"),"You have successfully buy product stock ");

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
                param.put("customer_id",Global.customerid);
                param.put("amount",amount);
                param.put("product_id",id);
                param.put("gst",gst);
                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }
}
