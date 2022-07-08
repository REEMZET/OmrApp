package com.reemzet.omr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TestReport extends Fragment {

   TextView totalmarks,correctans,incorrectans,unattempedans;
   String tmarks,tcorrect,tincorrect,unattempted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_test_report, container, false);
        totalmarks=view.findViewById(R.id.totalmarks);
        correctans=view.findViewById(R.id.correct);
        incorrectans=view.findViewById(R.id.incorrect);
        unattempedans=view.findViewById(R.id.unattemped);
        tmarks=getArguments().getString("totalmarks");
        tcorrect=getArguments().getString("correct");
        unattempted=getArguments().getString("unattempted");

        totalmarks.setText("Total marks- "+tmarks);
        correctans.setText("Total correct- "+tcorrect);
        unattempedans.setText("Total left- "+unattempted);



        return view;
    }
}