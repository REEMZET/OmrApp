package com.reemzet.omr;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Models.OmrModel;
import com.reemzet.omr.Models.TestDetails;

import java.util.ArrayList;


public class SetAnswer extends Fragment {

    DatabaseReference answerlistref, testdetailsref;
    FirebaseAuth mAuth;
    String testkey;
    ArrayList<String> answerlist, selectedanswer;
    TestDetails testDetails;
    RecyclerView answerrecyclerview;
    ArrayList<OmrModel> list;

    Button submitanswerbtn;
    ProgressDialog progressDialog;
    boolean checked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_answer, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        testkey = getArguments().getString("testkey");
        answerlist = new ArrayList<>();
        list = new ArrayList<>();
        selectedanswer = new ArrayList<>();
        answerrecyclerview = view.findViewById(R.id.answerrecycler);
        answerrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        submitanswerbtn = view.findViewById(R.id.submitanswerbtn);

        if (!testkey.isEmpty()) {
            answerlistref = database.getReferenceFromUrl(testkey).child("answerlist");
            testdetailsref = database.getReferenceFromUrl(testkey);
        }

        OmrAdapter omrAdapter = new OmrAdapter(getContext(), list);
        answerrecyclerview.setAdapter(omrAdapter);
        answerrecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 1));
        testdetailsref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                testDetails = snapshot.getValue(TestDetails.class);
                answerlistref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                answerlist.add(postSnapshot.getValue().toString());
                            }
                            int i, questionno = Integer.parseInt(testDetails.getQuestionno());
                            for (i = 0; i < questionno; i++) {
                                int b = 1;
                                list.add(new OmrModel(String.valueOf(b + i), String.valueOf(answerlist.get(i))));

                                selectedanswer.add("x");
                                OmrAdapter omrAdapter = new OmrAdapter(getContext(), list);
                                answerrecyclerview.setAdapter(omrAdapter);
                                answerrecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 1));

                            }

                        } else {
                            int i, questionno = Integer.parseInt(testDetails.getQuestionno());
                            for (i = 0; i <questionno; i++) {
                                int b = 1;
                                list.add(new OmrModel(String.valueOf(b+i), "x"));
                                selectedanswer.add("x");
                                OmrAdapter omrAdapter = new OmrAdapter(getContext(), list);
                                answerrecyclerview.setAdapter(omrAdapter);
                                answerrecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 1));

                            }
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
            OmrModel omrModel = omrModelArrayList.get(position);
            holder.questionno.setText(omrModel.getQuestionno());
            if (!answerlist.isEmpty()) {
                switch (answerlist.get(holder.getAdapterPosition())) {
                    case "a":
                        holder.a.setChecked(true);
                        selectedanswer.set(holder.getAdapterPosition(), "a");
                        break;
                    case "b":
                        holder.b.setChecked(true);
                        selectedanswer.set(holder.getAdapterPosition(), "b");
                        break;
                    case "c":
                        holder.c.setChecked(true);
                        selectedanswer.set(holder.getAdapterPosition(), "c");
                        break;
                    case "d":
                        holder.d.setChecked(true);
                        selectedanswer.set(holder.getAdapterPosition(), "d");
                        break;
                }

            }

            holder.a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.a.isChecked()) {
                        selectedanswer.set(holder.getAdapterPosition(), "a");
                        holder.b.setChecked(false);
                        holder.c.setChecked(false);
                        holder.d.setChecked(false);
                    } else {
                        selectedanswer.set(holder.getAdapterPosition(), "x");
                    }
                }
            });
            holder.b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.b.isChecked()) {
                        selectedanswer.set(holder.getAdapterPosition(), "b");
                        holder.a.setChecked(false);
                        holder.c.setChecked(false);
                        holder.d.setChecked(false);
                    } else {
                        selectedanswer.set(holder.getAdapterPosition(), "x");
                    }

                }
            });
            holder.c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.c.isChecked()) {
                        selectedanswer.set(holder.getAdapterPosition(), "c");
                        holder.a.setChecked(false);
                        holder.b.setChecked(false);
                        holder.d.setChecked(false);
                    } else {
                        selectedanswer.set(holder.getAdapterPosition(), "x");
                    }

                }
            });
            holder.d.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.d.isChecked()) {
                        selectedanswer.set(holder.getAdapterPosition(), "d");
                        holder.a.setChecked(false);
                        holder.b.setChecked(false);
                        holder.c.setChecked(false);
                    } else {
                        selectedanswer.set(holder.getAdapterPosition(), "x");
                    }

                }
            });
            submitanswerbtn.setOnClickListener(v -> {
                    //checkallchecked();
                if (checkallchecked()){
                        for (int i = 0; i < selectedanswer.size(); i++) {
                            int b = 1;
                            answerlistref.child(String.valueOf(b + i)).setValue(selectedanswer.get(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                        }

                    testdetailsref.child("status").setValue("Ready").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "answer set Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    Toast.makeText(getContext(), "Please select all option", Toast.LENGTH_SHORT).show();
                }


            });



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


        public class OmrViewholder extends RecyclerView.ViewHolder {
            TextView questionno;
            CheckBox a, b, c, d;

            public OmrViewholder(@NonNull View itemView) {
                super(itemView);
                a = itemView.findViewById(R.id.checkboxa);
                b = itemView.findViewById(R.id.checkboxb);
                c = itemView.findViewById(R.id.checkboxc);
                d = itemView.findViewById(R.id.checkboxd);
                questionno = itemView.findViewById(R.id.questionno);

            }
        }
    }

    public void showloding() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialoprogress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    public boolean checkallchecked(){
        if (selectedanswer.contains("x")){
            return false;
        }else
            return true;
        }
    }

