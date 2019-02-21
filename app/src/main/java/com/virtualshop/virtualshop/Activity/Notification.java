package com.virtualshop.virtualshop.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.virtualshop.virtualshop.Config.DbHelper;
import com.virtualshop.virtualshop.Model.Global;
import com.virtualshop.virtualshop.Model.Model;
import com.virtualshop.virtualshop.R;

import java.util.ArrayList;

public class Notification extends AppCompatActivity {

    RecyclerView recyclerView;
    DbHelper dbHelper ;
    ArrayList<Model> models =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ActionBar actionBar =getSupportActionBar();

        Global.actionbar(Notification.this , actionBar ,"Notifications");

        SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("notivalue", 0 );
        editor.apply();

        dbHelper = new DbHelper(this);
        recyclerView=findViewById(R.id.rvNotification);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLayoutManager);
        getData();

    }

    private void getData() {

        Cursor cursor = dbHelper.getNotificationlist();
        if(cursor!=null&&cursor.getCount()>0)

        {
            while (cursor.moveToNext()) {
                Model EData = new Model();
                EData.Tittle = cursor.getString(1);
                EData.Desc = cursor.getString(3);
                EData.date = cursor.getString(2);
                models.add(EData);
            }



            recyclerView.setAdapter(new RecyclerView.Adapter() {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(Notification.this).inflate(R.layout.adaptalert, parent, false);
                    return new Holder(view) {
                    };
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                    final Holder hholder = (Holder) holder;
                    final Model model = models.get(position);

                    hholder.date.setText(model.date);
                    hholder.desc.setText(model.Desc);
                    hholder.title.setText(model.Tittle);


                }

                @Override
                public int getItemCount() {
                    return models.size();
                }

                class Holder extends RecyclerView.ViewHolder {

                    TextView title, desc, date;

                    public Holder(View itemView) {
                        super(itemView);

                        title = itemView.findViewById(R.id.notiTitle);
                        desc = itemView.findViewById(R.id.notiDesc);
                        date = itemView.findViewById(R.id.notiDate);

                    }
                }
            });
        }

    }

}
