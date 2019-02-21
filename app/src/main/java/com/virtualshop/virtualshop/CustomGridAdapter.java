package com.virtualshop.virtualshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHAIKH AQUIB on 22,September,2018
 */
public class CustomGridAdapter extends BaseAdapter {

    private Context context;
    List<Integer> items = new ArrayList<Integer>();
    LayoutInflater inflater;

    public CustomGridAdapter(Context context, List<Integer> items) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cell, null);
        }
        Button button = (Button) convertView.findViewById(R.id.grid_item);
        button.setText(String.valueOf(items.get(position)));

        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}