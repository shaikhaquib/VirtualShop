package com.virtualshop.virtualshop;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

/**
 * Created by Auib on 15-10-2018.
 */

public class CustomSwipeAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    Context context;
    int[] img;

    public CustomSwipeAdapter(Context applicationContext, int[] img) {
        this.context = applicationContext;
        this.img = img;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.swipe_rewards, parent, false);
        return new ViewHolder(v);    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myViewHolder=(ViewHolder) holder;
        myViewHolder.image.setImageResource(img[position]);

    }

    @Override
    public int getItemCount() {
        return img.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image_rewards);
        }
    }
}