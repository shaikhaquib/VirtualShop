package com.virtualshop.virtualshop.Manual_payment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.virtualshop.virtualshop.Activity.AddBeneficiary;
import com.virtualshop.virtualshop.Activity.MainActivity;
import com.virtualshop.virtualshop.Config.APIs;
import com.virtualshop.virtualshop.Config.AppController;
import com.virtualshop.virtualshop.Model.Benificiary;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shaikh Aquib.
 */

public class Manual_BenificiaryList extends AppCompatActivity {

      FloatingActionButton AddBeneficiary;
      RecyclerView recyclerView;
      ProgressDialog dialog;
      ArrayList<Benificiary> list = new ArrayList< >();
      ColorGenerator generator = ColorGenerator.MATERIAL;
      Button Transfer;
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_benificiary_list);
          // Initialize Variable


          initialize();
         // getBenificary();
          getBenificarydata();

      }

      private void getBenificarydata() {
          ShowProgress();
          StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.Benificiarylist,
                  new Response.Listener <String> () {
                      @Override
                      public void onResponse(String response) {
                          // response
                          Dissmiss();
                          Log.d("Response", response);
                          try {
                              JSONObject jsonObject = new JSONObject(response);
                              //JSONObject data = jsonObject.getJSONObject("result");
                             // JSONObject data1 = data.getJSONObject("fulldata");
                              JSONArray array = jsonObject.getJSONArray("result");

                              for (int i = 0; i < array.length(); i++) {
                                  JSONObject object = array.getJSONObject(i);
                                  Benificiary benificiary = new Benificiary();
                                  /*"id": "137136",
                                          "name": "ifra",
                                          "mobile": "8286577316",
                                          "account": "60084925720",
                                          "bank": "BANK OF MAHARASHTRA",
                                          "status": "1",
                                          "last_success_date": "",
                                          "last_success_name": "",
                                          "last_success_imps": "",
                                          "ifsc": "MAHB0001402",
                                          "imps": "0"*/
                                 // benificiary.id = object.getString("id");
                                  benificiary.account_number = object.getString("account_number");
                                  benificiary.beneficiary_id = object.getString("beneficiary_id");
                                  benificiary.beneficiary_name = object.getString("beneficiary_name");
                                  benificiary.ifsc = object.getString("ifsc");
                                //  benificiary.mobile = object.getString("mobile");
                                  benificiary.status = object.getString("status");
                                  benificiary.benstatus = object.getString("beneficiary_status");
                              //    benificiary.unique_id = object.getString("unique_id");

                                  if (!benificiary.status.equals("0"))
                                  {
                                  list.add(benificiary);}

                              }



                          } catch (JSONException e) {
                              e.printStackTrace();
                          }
                          recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                          recyclerView.setAdapter(new RecyclerView.Adapter() {
                              @Override
                              public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                                  View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.benificary, parent, false);
                                  ViewHolder viewHolder = new ViewHolder(view);
                                  return viewHolder;
                              }

                              @Override
                              public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

                                  final ViewHolder viewHolder = (ViewHolder) holder;
                                  final Benificiary current = list.get(position);


                                  viewHolder.name.setText(current.beneficiary_name);
                                  viewHolder.mobile.setText(current.mobile);
                                  viewHolder.ifsc.setText(current.ifsc);
                                  viewHolder.account.setText(current.account_number);
                               //   viewHolder.imageView.setTag(position);
                                  viewHolder.benCard.setTag(position);

                                  if(current.status.equals("-1")){
                                      viewHolder.benCard.setBackgroundColor(Color.parseColor("#2affa726"));
                                  }

                                  viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {

                                          if(current.status.equals("-1")){
                                              Global.diloge(Manual_BenificiaryList.this,"STATUS PENDING" , "YOUR BENEFICIARY STATUS IS IN PENDING PLEASE CONTACT SUPPORT TO ACTIVATE");
                                          }else if(current.status.equals("1")){
                                          Intent intent = new Intent(getApplicationContext(), Manual_FundTransfer.class);
                                          //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                          intent.putExtra("accountno", current.account_number);
                                          intent.putExtra("ifsc", current.ifsc);
                                          intent.putExtra("Bid",current.beneficiary_id);

                                          startActivity(intent);}
                                      }
                                  });

                                  viewHolder.letter.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          Intent intent = new Intent(getApplicationContext(), Manual_FundTransfer.class);
                                          //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                          intent.putExtra("accountno", current.account_number);
                                          intent.putExtra("ifsc", current.ifsc);
                                          intent.putExtra("Bid",current.beneficiary_id);

                                          startActivity(intent);
                                      }
                                  });


                                  //        Get the first letter of list item
                                  String letter = String.valueOf(current.beneficiary_name.charAt(0));

//        Create a new TextDrawable for our image's background
                                  TextDrawable drawable = TextDrawable.builder().buildRound(letter, generator.getRandomColor());

                                  viewHolder.letter.setImageDrawable(drawable);


                                  viewHolder.menu.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          AlertDialog.Builder builder;
                                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                              builder = new AlertDialog.Builder(Manual_BenificiaryList.this);
                                          } else {
                                              builder = new AlertDialog.Builder(Manual_BenificiaryList.this);
                                          }
                                          builder.setTitle("Delete Beneficiary")
                                                  .setMessage("Are you sure you want to delete this Beneficiary?")
                                                  .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                      public void onClick(DialogInterface dialog, int which) {
                                                          // continue with delete
                                                          deleteBenificiary(Global.customerid,Global.agentid,current.unique_id,current.beneficiary_id);
                                                          list.remove(position);
                                                          notifyItemRemoved(position);
                                                          notifyItemRangeChanged(position, list.size());
                                                      }
                                                  })
                                                  .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                      public void onClick(DialogInterface dialog, int which) {
                                                          // do nothing
                                                      }
                                                  })
                                                  .setIcon(R.drawable.ic_report_problem)
                                                  .show();
                                      }
                                  });

                              }

                              @Override
                              public int getItemCount() {
                                  return list.size();
                              }

                              class ViewHolder extends RecyclerView.ViewHolder {

                                  ImageView  menu ;
                                  ImageView letter;
                                  LinearLayout layout;
                                  TextView name, ifsc, mobile, account;
                                  LinearLayout benCard;
                                  public ViewHolder(View itemView) {
                                      super(itemView);

                                    //  imageView = itemView.findViewById(R.id.FundTrans);
                                      layout=itemView.findViewById(R.id.benlayout);
                                      menu = itemView.findViewById(R.id.bendelit);
                                      name = itemView.findViewById(R.id.beName);
                                      mobile = itemView.findViewById(R.id.beMobile);
                                      ifsc = itemView.findViewById(R.id.beIfsc);
                                      account = itemView.findViewById(R.id.beAccount);
                                      benCard = itemView.findViewById(R.id.benCard);
                                      letter = (ImageView) itemView.findViewById(R.id.gmailitem_letter);
                                  }
                              }
                          });

                      }
                  },
                  new Response.ErrorListener() {
                      @Override
                      public void onErrorResponse(VolleyError error) {
                          // error
                          Dissmiss();
                          final Dialog dialog = new Dialog(Manual_BenificiaryList.this);
                          dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                          dialog.setCancelable(false);
                          dialog.setContentView(R.layout.connectionerror);
                          Button button = dialog.findViewById(R.id.btnOK);
                          button.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  startActivity(new Intent(getApplicationContext(),MainActivity.class));
                              }
                          });

                          dialog.show();
                      }
                  }
          ) {

              @Override
              public String getBodyContentType() {
                  return "application/x-www-form-urlencoded; charset=UTF-8";
              }


              @Override
              protected Map< String, String > getParams() {
                  Map< String, String > params = new HashMap< String, String >();
                  params.put("mobile", Global.mobile);
                  params.put("customer_id", Global.customerid);
                  return params;
              }
          };
          AppController.getInstance().addToRequestQueue(postRequest);
      }

      private void getBenificary() {
          ShowProgress();
          StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.ACTIVEBenificiarylist,
                  new Response.Listener <String> () {
                      @Override
                      public void onResponse(String response) {
                          // response
                          Dissmiss();
                          Log.d("Response", response);
                          try {
                              JSONObject jsonObject = new JSONObject(response);
                              JSONObject data = jsonObject.getJSONObject("result");
                              JSONObject data1 = data.getJSONObject("fulldata");
                              JSONArray array = data1.getJSONArray("beneficiary");

                              for (int i = 0; i < array.length(); i++) {
                                  JSONObject object = array.getJSONObject(i);
                                  Benificiary benificiary = new Benificiary();
                                  /*"id": "137136",
                                          "name": "ifra",
                                          "mobile": "8286577316",
                                          "account": "60084925720",
                                          "bank": "BANK OF MAHARASHTRA",
                                          "status": "1",
                                          "last_success_date": "",
                                          "last_success_name": "",
                                          "last_success_imps": "",
                                          "ifsc": "MAHB0001402",
                                          "imps": "0"*/
                                 // benificiary.id = object.getString("id");
                                  benificiary.account_number = object.getString("account");
                                  benificiary.beneficiary_id = object.getString("beneficiary_id");
                                  benificiary.beneficiary_name = object.getString("name");
                                  benificiary.ifsc = object.getString("ifsc");
                                  benificiary.mobile = object.getString("mobile");
                                  benificiary.status = object.getString("status");
                              //    benificiary.unique_id = object.getString("unique_id");

                                  if (!benificiary.status.equals("-1"))
                                  {
                                  list.add(benificiary);}

                              }



                          } catch (JSONException e) {
                              e.printStackTrace();
                          }
                          recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                          recyclerView.setAdapter(new RecyclerView.Adapter() {
                              @Override
                              public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                                  View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.benificary, parent, false);
                                  ViewHolder viewHolder = new ViewHolder(view);
                                  return viewHolder;
                              }

                              @Override
                              public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

                                  final ViewHolder viewHolder = (ViewHolder) holder;
                                  final Benificiary current = list.get(position);


                                  viewHolder.name.setText(current.beneficiary_name);
                                  viewHolder.mobile.setText(current.mobile);
                                  viewHolder.ifsc.setText(current.ifsc);
                                  viewHolder.account.setText(current.account_number);
                               //   viewHolder.imageView.setTag(position);
                                  viewHolder.benCard.setTag(position);

                                  if(current.status.equals("-1")){
                                      viewHolder.benCard.setCardBackgroundColor(Color.parseColor("#2affa726"));
                                  }

                                  viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {

                                          if(current.status.equals("-1")){
                                              Global.diloge(Manual_BenificiaryList.this,"STATUS PENDING" , "YOUR BENEFICIARY STATUS IS IN PENDING PLEASE CONTACT SUPPORT TO ACTIVATE");
                                          }else if(current.status.equals("1")){
                                          Intent intent = new Intent(getApplicationContext(), Manual_FundTransfer.class);
                                          //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                          intent.putExtra("accountno", current.account_number);
                                          intent.putExtra("ifsc", current.ifsc);
                                          intent.putExtra("Bid",current.beneficiary_id);

                                          startActivity(intent);}
                                      }
                                  });

                                  viewHolder.letter.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          Intent intent = new Intent(getApplicationContext(), Manual_FundTransfer.class);
                                          //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                          intent.putExtra("accountno", current.account_number);
                                          intent.putExtra("ifsc", current.ifsc);
                                          intent.putExtra("Bid",current.beneficiary_id);

                                          startActivity(intent);
                                      }
                                  });


                                  //        Get the first letter of list item
                                  String letter = String.valueOf(current.beneficiary_name.charAt(0));

//        Create a new TextDrawable for our image's background
                                  TextDrawable drawable = TextDrawable.builder().buildRound(letter, generator.getRandomColor());

                                  viewHolder.letter.setImageDrawable(drawable);


                                  viewHolder.menu.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          AlertDialog.Builder builder;
                                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                              builder = new AlertDialog.Builder(Manual_BenificiaryList.this);
                                          } else {
                                              builder = new AlertDialog.Builder(Manual_BenificiaryList.this);
                                          }
                                          builder.setTitle("Delete Beneficiary")
                                                  .setMessage("Are you sure you want to delete this Beneficiary?")
                                                  .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                      public void onClick(DialogInterface dialog, int which) {
                                                          // continue with delete
                                                          deleteBenificiary(Global.customerid,Global.agentid,current.unique_id,current.beneficiary_id);
                                                          list.remove(position);
                                                          notifyItemRemoved(position);
                                                          notifyItemRangeChanged(position, list.size());
                                                      }
                                                  })
                                                  .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                      public void onClick(DialogInterface dialog, int which) {
                                                          // do nothing
                                                      }
                                                  })
                                                  .setIcon(R.drawable.ic_report_problem)
                                                  .show();
                                      }
                                  });

                              }

                              @Override
                              public int getItemCount() {
                                  return list.size();
                              }

                              class ViewHolder extends RecyclerView.ViewHolder {

                                  ImageView  menu ;
                                  ImageView letter;
                                  LinearLayout layout;
                                  TextView name, ifsc, mobile, account;
                                  CardView benCard;
                                  public ViewHolder(View itemView) {
                                      super(itemView);

                                    //  imageView = itemView.findViewById(R.id.FundTrans);
                                      layout=itemView.findViewById(R.id.benlayout);
                                      menu = itemView.findViewById(R.id.bendelit);
                                      name = itemView.findViewById(R.id.beName);
                                      mobile = itemView.findViewById(R.id.beMobile);
                                      ifsc = itemView.findViewById(R.id.beIfsc);
                                      account = itemView.findViewById(R.id.beAccount);
                                      benCard = itemView.findViewById(R.id.benCard);
                                      letter = (ImageView) itemView.findViewById(R.id.gmailitem_letter);
                                  }
                              }
                          });

                      }
                  },
                  new Response.ErrorListener() {
                      @Override
                      public void onErrorResponse(VolleyError error) {
                          // error
                          Dissmiss();
                          final Dialog dialog = new Dialog(Manual_BenificiaryList.this);
                          dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                          dialog.setCancelable(false);
                          dialog.setContentView(R.layout.connectionerror);
                          Button button = dialog.findViewById(R.id.btnOK);
                          button.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  startActivity(new Intent(getApplicationContext(),MainActivity.class));
                              }
                          });

                          dialog.show();
                      }
                  }
          ) {

              @Override
              public String getBodyContentType() {
                  return "application/x-www-form-urlencoded; charset=UTF-8";
              }


              @Override
              protected Map< String, String > getParams() {
                  Map< String, String > params = new HashMap< String, String >();
                  params.put("mobile", Global.mobile);
                  params.put("customer_id", Global.customerid);
                  return params;
              }
          };
          AppController.getInstance().addToRequestQueue(postRequest);
      }

      private void deleteBenificiary(String id, String agentid, final String unique_id, final String beneficiary_id)
      {
          StringRequest request =new StringRequest(StringRequest.Method.POST, APIs.BeniDelete, new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {

                  try {
                      JSONObject jsonObject =new JSONObject(response);
                      Toast.makeText(Manual_BenificiaryList.this, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
                  //Toast.makeText(BenificiaryList.this, response, Toast.LENGTH_SHORT).show();

              }
          }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                  final Dialog dialog = new Dialog(Manual_BenificiaryList.this);
                  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                  dialog.setCancelable(false);
                  dialog.setContentView(R.layout.connectionerror);
                  Button button = dialog.findViewById(R.id.btnOK);
                  button.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          startActivity(new Intent(getApplicationContext(),MainActivity.class));
                      }
                  });

                  dialog.show();              }
          }){

              @Override
              public String getBodyContentType() {
                  return "application/x-www-form-urlencoded; charset=UTF-8";
              }


              @Override
              protected Map<String, String> getParams() throws AuthFailureError {

                  Map<String, String> params = new HashMap<String, String>();

                  params.put("unique_id", unique_id);

                  return params;
              }

          };
          AppController.getInstance().addToRequestQueue(request);
      }

      private void initialize() {
          AddBeneficiary = findViewById(R.id.fabAddbeneficiary);
          recyclerView = findViewById(R.id.benilist);
          dialog = new ProgressDialog(Manual_BenificiaryList.this);
          AddBeneficiary.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(getApplicationContext(), AddBeneficiary.class));
              }
          });


      }



      @Override
      protected void onResume() {
          super.onResume();


      }

      private void ShowProgress() {
          dialog.setMessage("Getting beneficiary data...");
          dialog.setCancelable(false);
          dialog.show();
      }
      private void Dissmiss() {
          dialog.dismiss();
      }

      @Override
      public void onBackPressed() {
          super.onBackPressed();
          startActivity(new Intent(new Intent(getApplicationContext(), MainActivity.class)));

      }
  }