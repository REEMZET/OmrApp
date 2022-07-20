package com.reemzet.omr.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reemzet.omr.R;

public class JoinedStudentHolder extends RecyclerView.ViewHolder {
   public TextView tvjoinedstudentname,tvjoinedstudentemail,tvjoinedstudentcity,tvjoinedstudentmob;
   public ImageView imgshowreport,joinedstudentimg;
    public JoinedStudentHolder(@NonNull View itemView) {
        super(itemView);
        tvjoinedstudentname=itemView.findViewById(R.id.tvjoinedstudentname);
        tvjoinedstudentemail=itemView.findViewById(R.id.tvjoinedstudentemail);
        tvjoinedstudentcity=itemView.findViewById(R.id.tvjoinedstudentcity);
        tvjoinedstudentmob=itemView.findViewById(R.id.tvjoinedstudentmob);
        imgshowreport=itemView.findViewById(R.id.imgshowreport);
        joinedstudentimg=itemView.findViewById(R.id.joinedstudentimg);

    }
}
