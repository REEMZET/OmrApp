package com.reemzet.omr;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class Report extends Fragment {

PieChart pieChart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_report, container, false);
       // pieChart=view.findViewById(R.id.scorepiechart);

      /*  ArrayList<PieEntry> records=new ArrayList<>();
        records.add(new PieEntry(350,"Obtained Marks"));
        records.add(new PieEntry(400,"Total Marks"));


        PieDataSet dataSet=new PieDataSet(records,"Test Report");
        dataSet.setColor(Color.WHITE);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(15f);
        PieData pieData=new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("Total Test-3");
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterTextSize(16f);
        pieChart.animate();

*/
    return view;
    }

}