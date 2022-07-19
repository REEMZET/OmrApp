package com.reemzet.omr.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.reemzet.omr.R;

public class RankHolder extends RecyclerView.ViewHolder {
    public TextView tvstudentname,tvstudentcity;
   public CircularImageView studentimg;
    public RankHolder(@NonNull View itemView) {
        super(itemView);
        tvstudentname=itemView.findViewById(R.id.tvstudentname);
        tvstudentcity=itemView.findViewById(R.id.tvstudentcity);
        studentimg=itemView.findViewById(R.id.studentimg);

    }
}
