package com.reemzet.omr.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reemzet.omr.Models.OmrModel;
import com.reemzet.omr.R;

import java.util.ArrayList;

public class OmrAdapter extends RecyclerView.Adapter<OmrAdapter.OmrViewholder> {
        Context context;
        ArrayList<OmrModel> omrModelArrayList;

    public OmrAdapter(Context context, ArrayList<OmrModel> omrModelArrayList) {
        this.context = context;
        this.omrModelArrayList = omrModelArrayList;
    }

    @NonNull
    @Override
    public OmrViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.omrlayout, parent, false);
        return new OmrViewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull OmrViewholder holder, int position) {
            OmrModel omrModel=omrModelArrayList.get(position);
            holder.questionno.setText(omrModel.getQuestionno());
            holder.a.setText(omrModel.getA());
            holder.b.setText(omrModel.getB());
            holder.c.setText(omrModel.getC());
            holder.d.setText(omrModel.getD());
            holder.radioGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.radioGroup.clearCheck();
                }
            });
    }

    @Override
    public int getItemCount() {
        return omrModelArrayList.size();
    }

    public class OmrViewholder extends RecyclerView.ViewHolder{
        TextView questionno;
        RadioButton a,b,c,d;
        RadioGroup radioGroup;

        public OmrViewholder(@NonNull View itemView) {
            super(itemView);
            a=itemView.findViewById(R.id.a);
            b=itemView.findViewById(R.id.b);
            c=itemView.findViewById(R.id.c);
            d=itemView.findViewById(R.id.d);
            questionno=itemView.findViewById(R.id.questionno);
            radioGroup=itemView.findViewById(R.id.radioGroup);


        }
    }
}
