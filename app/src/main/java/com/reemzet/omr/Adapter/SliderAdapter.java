package com.reemzet.omr.Adapter;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.reemzet.omr.Models.SliderMOdel;
import com.reemzet.omr.R;

import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    ArrayList<SliderMOdel> sliderMOdelArrayList;
    ViewPager2 viewPager2;

    public SliderAdapter(ArrayList<SliderMOdel> sliderMOdelArrayList, ViewPager2 viewPager2) {
        this.sliderMOdelArrayList = sliderMOdelArrayList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item,parent,false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
           SliderMOdel mOdel=sliderMOdelArrayList.get(position);
           holder.tvtitle.setText(mOdel.getTitle());
           holder.tvmessage.setText(mOdel.getMessage());
           holder.tvmessage.setTextColor(Color.parseColor(mOdel.getMessagecolor()));
           holder.tvtitle.setTextColor(Color.parseColor(mOdel.getTitlecolor()));
           holder.cardView.setCardBackgroundColor(Color.parseColor(mOdel.getBgcard()));
           if (position==sliderMOdelArrayList.size()-2){
               viewPager2.post(sliderRunnable);
           }
    }

    @Override
    public int getItemCount() {
        return sliderMOdelArrayList.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView tvtitle,tvmessage;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvmessage=itemView.findViewById(R.id.tvmessage);
            tvtitle=itemView.findViewById(R.id.tvtitle);
            cardView=itemView.findViewById(R.id.cardview);
        }
    }
    private Runnable sliderRunnable=new Runnable() {
        @Override
        public void run() {
            sliderMOdelArrayList.addAll(sliderMOdelArrayList);
            notifyDataSetChanged();
        }
    };
}
