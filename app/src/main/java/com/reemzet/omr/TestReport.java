package com.reemzet.omr;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class TestReport extends Fragment {

   TextView tvtestresponse;
   String tmarks,testname,tquestion,unattempted;
    PieChart pieChart;
    ImageView btnhome;
    NavController navController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_test_report, container, false);
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        tvtestresponse=view.findViewById(R.id.tvtestresponse);
        tmarks=getArguments().getString("totalmarks");
        testname=getArguments().getString("testname");


        unattempted=getArguments().getString("unattempted");
        tquestion=getArguments().getString("totalquestion");
        pieChart=view.findViewById(R.id.piechart);
        btnhome=view.findViewById(R.id.homebtn);
        int attemped=Integer.parseInt(tquestion)-Integer.parseInt(unattempted);
        int totalquestion=Integer.parseInt(tquestion);
        int halfquestion=totalquestion/2;

        ArrayList<PieEntry> records=new ArrayList<>();
        records.add(new PieEntry(attemped,"Attemped Questions"));
        records.add(new PieEntry(Integer.parseInt(unattempted),"Unattemped Question "));
        PieDataSet dataSet=new PieDataSet(records,"Test Report");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(22f);
        PieData pieData=new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Total Question\n"+totalquestion);
        pieChart.setCenterTextSize(23f);
        pieChart.animate();


        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
                navController.navigate(R.id.homeStudent);
            }
        });
        if (attemped<halfquestion){
            tvtestresponse.setText("Average Attemped");
        }else tvtestresponse.setText("Good Attemped");
        return view;
    }
}