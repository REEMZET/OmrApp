package com.reemzet.omr.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reemzet.omr.R;

public class RequestListViewHolder extends RecyclerView.ViewHolder {
    public TextView tvstudentname,tvstudentcity,tvstudentmobile;
          public ImageView tvreject,tvaccept;
    public ImageView studentimg;
    public RequestListViewHolder(@NonNull View itemView) {
        super(itemView);
        tvstudentname=itemView.findViewById(R.id.tvstudentname);
        tvstudentcity=itemView
                .findViewById(R.id.tvstudentcity);
        tvstudentmobile=itemView.findViewById(R.id.tvstudentmob);
        tvreject=itemView.findViewById(R.id.tvreject);
        tvaccept=itemView.findViewById(R.id.tvaccept);
        studentimg=itemView.findViewById(R.id.studentimg);

    }
}
