package com.reemzet.omr;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
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
import com.reemzet.omr.Models.ScoreModel;

import java.util.ArrayList;


public class Score extends Fragment {

    PieChart pieChart;
    FirebaseDatabase database;
    DatabaseReference scoreref;
    ScoreModel scoreModel;
    String orgcode,testid;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    int  totalmarks,correctquestion,unattempedquestion,totalquestion;
    TextView tvobtainedmarks,tvtotalque,tvcorectans,tvincorrect;
    LinearLayout linearleaderboard;
    NavController navController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_score, container, false);
        orgcode=getArguments().getString("orgcode");
        testid=getArguments().getString("testid");
        totalmarks=Integer.parseInt(getArguments().getString("totalmarks"));
        correctquestion=Integer.parseInt(getArguments().getString("correctquestion"));
        unattempedquestion=Integer.parseInt(getArguments().getString("unattempedquestion"));
       totalquestion=Integer.parseInt(getArguments().getString("totalquestion"));
        pieChart=view.findViewById(R.id.scorepiechart);
        tvobtainedmarks=view.findViewById(R.id.tvobtainedmarks);
        tvtotalque=view.findViewById(R.id.tvtotalque);
        tvcorectans=view.findViewById(R.id.tvcorectans);
        tvincorrect=view.findViewById(R.id.tvincorrect);
        linearleaderboard=view.findViewById(R.id.linearleaderboard);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        database=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();

        scoreref=database.getReference("institute").child(orgcode).child("TestList").child(testid).child("StudentResponse").child(mAuth.getUid()).child("Score");
        loadpiechart();

        tvobtainedmarks.setText(String.valueOf(totalmarks));
        tvtotalque.setText(String.valueOf(totalquestion));
        tvcorectans.setText(String.valueOf(correctquestion));
        tvincorrect.setText(String.valueOf(totalquestion-correctquestion-unattempedquestion));

            linearleaderboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("orgcode",orgcode);
                    bundle.putString("testid",testid);
                    navController.navigate(R.id.leaderBoard,bundle);
                }
            });
   return  view;
    }
    public void showloding() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialoprogress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    public void loadpiechart(){
        int marksobtained=totalmarks;
        int deductedmarks=totalquestion*4-(marksobtained);
        ArrayList<PieEntry> records=new ArrayList<>();
        records.add(new PieEntry(marksobtained,"Obtained Marks"));
        records.add(new PieEntry(deductedmarks,"Lost Marks"));
        PieDataSet dataSet=new PieDataSet(records,"Test Report");
        dataSet.setColor(Color.WHITE);

        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(15f);
        PieData pieData=new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("Total Marks\n"+(totalquestion)*4);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterTextSize(16f);
        pieChart.animate();


    }

    @Override
    public void onStart() {
        super.onStart();
        scoreref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scoreModel=snapshot.getValue(ScoreModel.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
