package com.reemzet.omr;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Models.OmrModel;

import java.util.ArrayList;


public class PreviewAnswer extends Fragment {


    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference answerlistref, selectedanswerref;
    ArrayList<String> answerlist, selectedanswer;
    ArrayList<OmrModel> list;
    String orgcode, testid;
    PreviewAnswerAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preview_answer, container, false);
        recyclerView = view.findViewById(R.id.previewanswerrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        testid = getArguments().getString("testid");
        orgcode = getArguments().getString("orgcode");
        answerlist = new ArrayList<>();
        list = new ArrayList<>();
        selectedanswer = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        answerlistref = database.getReference("institute").child(orgcode).child("TestList").child(testid).child("answerlist");
        selectedanswerref = database.getReference("institute").child(orgcode).child("TestList").child(testid).child("StudentResponse").child(mAuth.getUid()).child("SelectedAnswer");


        selectedanswerref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    selectedanswer.add(postSnapshot.getValue().toString());
                }
                answerlistref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            answerlist.add(postSnapshot.getValue().toString());
                        }
                        int i, questionno = answerlist.size();
                        for (i = 0; i < questionno; i++) {
                            int b = 1;
                            list.add(new OmrModel(String.valueOf(b + i), String.valueOf(answerlist.get(i))));
                            adapter = new PreviewAnswerAdapter(list);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }


    public class PreviewAnswerAdapter extends RecyclerView.Adapter<PreviewAnswerAdapter.PreviewAnswerViewHolder> {
        ArrayList<OmrModel> omrModelArrayList;

        public PreviewAnswerAdapter(ArrayList<OmrModel> omrModelArrayList) {
            this.omrModelArrayList = omrModelArrayList;
        }

        @NonNull
        @Override
        public PreviewAnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.answerpreviewlayout, parent, false);
            return new PreviewAnswerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PreviewAnswerViewHolder holder, int position) {
            OmrModel omrModel = omrModelArrayList.get(position);
            holder.questionno.setText(omrModel.getQuestionno());
            holder.ans.setButtonTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.black)));
            holder.ans.setClickable(false);
            holder.a.setClickable(false);
            holder.b.setClickable(false);
            holder.c.setClickable(false);
            holder.d.setClickable(false);
            if (!answerlist.isEmpty()) {
                switch (answerlist.get(holder.getAdapterPosition())) {
                    case "a":
                        holder.ans.setChecked(true);
                        holder.ans.setText("a");
                        break;
                    case "b":
                        holder.ans.setChecked(true);
                        holder.ans.setText("b");
                        break;
                    case "c":
                        holder.ans.setChecked(true);
                        holder.ans.setText("c");
                        break;
                    case "d":
                        holder.ans.setChecked(true);
                        holder.ans.setText("d");
                        break;
                }
                if (!selectedanswer.isEmpty()){
                    setSelectedanswer(selectedanswer.get(holder.getAdapterPosition()),holder);
                }

            }

        }


        @Override
        public int getItemCount() {
            return omrModelArrayList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }


        public class PreviewAnswerViewHolder extends RecyclerView.ViewHolder {
            TextView questionno;
            CheckBox a, b, c, d,ans;
            ImageView indicator;

            public PreviewAnswerViewHolder(@NonNull View itemView) {
                super(itemView);
                a = itemView.findViewById(R.id.checkboxa);
                b = itemView.findViewById(R.id.checkboxb);
                c = itemView.findViewById(R.id.checkboxc);
                d = itemView.findViewById(R.id.checkboxd);
                questionno = itemView.findViewById(R.id.questionno);
                ans=itemView.findViewById(R.id.ans);
                indicator=itemView.findViewById(R.id.indicator);

            }
        }
    }

    public void setSelectedanswer(String option,  PreviewAnswerAdapter.PreviewAnswerViewHolder hold){
        switch (option){
            case "a":
                hold.a.setChecked(true);
                if (!option.equals(answerlist.get(hold.getAdapterPosition()))){
                        hold.indicator.setImageDrawable(getResources().getDrawable(R.drawable.x));
                    hold.a.setButtonTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.pink)));
                }else   hold.indicator.setImageDrawable(getResources().getDrawable(R.drawable.correct));

                break;
            case"b":
                hold.b.setChecked(true);
                if (!option.equals(answerlist.get(hold.getAdapterPosition()))){
                    hold.indicator.setImageDrawable(getResources().getDrawable(R.drawable.x));
                    hold.b.setButtonTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.pink)));
                }else hold.indicator.setImageDrawable(getResources().getDrawable(R.drawable.correct));
                break;
            case"c":
                hold.c.setChecked(true);
                if (!option.equals(answerlist.get(hold.getAdapterPosition()))){
                    hold.indicator.setImageDrawable(getResources().getDrawable(R.drawable.x));
                    hold.c.setButtonTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.pink)));
                }else hold.indicator.setImageDrawable(getResources().getDrawable(R.drawable.correct));
                break;
            case "d":
                hold.d.setChecked(true);
                if (!option.equals(answerlist.get(hold.getAdapterPosition()))){
                    hold.indicator.setImageDrawable(getResources().getDrawable(R.drawable.x));
                    hold.d.setButtonTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.pink)));
                }else hold.indicator.setImageDrawable(getResources().getDrawable(R.drawable.correct));
                break;
        }

    }

}