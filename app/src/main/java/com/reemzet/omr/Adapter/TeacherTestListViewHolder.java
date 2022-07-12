package com.reemzet.omr.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reemzet.omr.R;

public class TeacherTestListViewHolder extends RecyclerView.ViewHolder {
    public TextView testname,testtime,totalquestion,tvtotalmarks,editnow,duration,date,tteststatus,tvsetanswer,tvdeletebtn,tvreport;
    public TeacherTestListViewHolder(@NonNull View itemView) {
        super(itemView);
        testname=itemView.findViewById(R.id.testname);
        testtime=itemView.findViewById(R.id.testtime);
        tvtotalmarks=itemView.findViewById(R.id.testtotalmarks);
        totalquestion=itemView.findViewById(R.id.totalques);
        editnow=itemView.findViewById(R.id.editnowbtn);
        duration=itemView.findViewById(R.id.tvduration);
        date=itemView.findViewById(R.id.tvdate);
        tteststatus=itemView.findViewById(R.id.tteacherstatus);
        tvsetanswer=itemView.findViewById(R.id.tvsetanswer);
        tvdeletebtn=itemView.findViewById(R.id.tvdelete);
        tvreport=itemView.findViewById(R.id.tvreport);

    }
}
