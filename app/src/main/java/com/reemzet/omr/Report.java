package com.reemzet.omr;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Models.StudentsModel;

import java.util.ArrayList;

import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar;


public class Report extends Fragment {
        ProgressDialog progressDialog;
        ImageView bigimage,smallimage;
        FirebaseDatabase database;
        DatabaseReference studentref;
        String orgcode,checkusertype,uid;
        FirebaseAuth mAuth;
        StudentsModel studentsModel;
        TextView tvname,tvtotalmarks,tvfullmarks,tvnofotest,tvhighscorepercent;
        SemiCircleArcProgressBar pr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       View view= inflater.inflate(R.layout.fragment_report, container, false);
            bigimage=view.findViewById(R.id.bigimage);
            smallimage=view.findViewById(R.id.smallimage);
            orgcode=getArguments().getString("orgcode");
            uid=getArguments().getString("uid");
            tvname=view.findViewById(R.id.name);
            tvtotalmarks=view.findViewById(R.id.tvobtainmarks);
            tvfullmarks=view.findViewById(R.id.tvfullmakars);
            tvnofotest=view.findViewById(R.id.tvnooftest);
            tvhighscorepercent=view.findViewById(R.id.tvhighscoreprecent);
            pr=view.findViewById(R.id.pr);
            mAuth=FirebaseAuth.getInstance();
            database=FirebaseDatabase.getInstance();
            studentref=database.getReference("institute").child(orgcode).child("StduentList").child(uid);




            showloding();


            studentref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        studentsModel=snapshot.getValue(StudentsModel.class);
                        setData();
                        progressDialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });








    return view;
    }

    private void setData() {
        Glide.with(getActivity())
                .load(studentsModel.getImageurl())
                .centerCrop()
                .placeholder(R.drawable.student)
                .into(bigimage);

        Glide.with(getActivity())
                .load(studentsModel.getImageurl())
                .centerCrop()
                .placeholder(R.drawable.student)
                .into(smallimage);
        tvname.setText(studentsModel.getStudenname());
        tvtotalmarks.setText(studentsModel.getTotalmarksobtained());
        tvfullmarks.setText(studentsModel.getMaximumtestmarks());
        tvnofotest.setText(studentsModel.getTotaltest());
        pr.setPercentWithAnimation((int) calculatePercentage(Integer.parseInt(studentsModel.getTotalmarksobtained()),Integer.parseInt(studentsModel.getMaximumtestmarks())));
        tvhighscorepercent.setText(String.valueOf((int) calculatePercentage(Integer.parseInt(studentsModel.getTotalmarksobtained()),Integer.parseInt(studentsModel.getMaximumtestmarks())))+"%");
    }
    public double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }
    public void showloding() {
        if (getActivity() != null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.show();
            progressDialog.setContentView(R.layout.dialoprogress);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }
}