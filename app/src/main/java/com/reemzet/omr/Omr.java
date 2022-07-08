package com.reemzet.omr;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Models.OmrModel;
import com.reemzet.omr.Models.TestDetails;

import java.util.ArrayList;


public class Omr extends Fragment {
    RecyclerView omrrecycler;
    DatabaseReference testanswer, testdetails;
    Button omrsubmitbtn;
    TestDetails testDetails;
    ArrayList<OmrModel> list;
    ArrayList<String> answerlist;
    ArrayList<String> selectedanswer;
    ArrayList<String> correctanswerlist;
    int notattemped = 0, totalmarksobtained,totalcorrect;
    NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_omr, container, false);
        omrrecycler = view.findViewById(R.id.omrrecycyler);
        omrsubmitbtn = view.findViewById(R.id.omrsubmitbtn);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        omrrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        testanswer = database.getReference("/institute/udisdvjvvdfvdf/tests/nvdjsjdsnjdsn/answers");
        testdetails = database.getReference("institute/udisdvjvvdfvdf/tests/nvdjsjdsnjdsn/testdetails/institute/udisdvjvvdfvdf/tests/nvdjsjdsnjdsn/test details");

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();


        list = new ArrayList<>();
        answerlist = new ArrayList<>();
        selectedanswer = new ArrayList<>();
        correctanswerlist = new ArrayList<>();

        testdetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                testDetails = snapshot.getValue(TestDetails.class);
                testanswer.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        answerlist.add("0");
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            answerlist.add(postSnapshot.getValue().toString());
                        }

                        int i, questionno = Integer.parseInt(testDetails.getQuestionno());
                        for (i = 0; i <= questionno; i++) {
                            if (i != 0) {
                                list.add(new OmrModel(String.valueOf(i), String.valueOf(answerlist.get(i))));

                            }
                            selectedanswer.add("x");
                            correctanswerlist.add("0");
                            OmrAdapter omrAdapter = new OmrAdapter(getContext(), list);
                            omrrecycler.setAdapter(omrAdapter);
                            omrrecycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
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


    public class OmrAdapter extends RecyclerView.Adapter<com.reemzet.omr.Omr.OmrAdapter.OmrViewholder> {
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

            holder.a.setOnClickListener(v -> {
                if (holder.a.isSelected()) {
                    holder.a.setChecked(false);
                    holder.a.setSelected(false);
                    selectedanswer.set(Integer.parseInt(omrModel.getQuestionno()), "x");
                    correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), "0");

                } else {
                    holder.a.setChecked(true);
                    holder.a.setSelected(true);
                    selectedanswer.set(Integer.parseInt(omrModel.getQuestionno()), "a"); //add selectedanswer to arraylist
                    // marks count for right or wrong answer
                    if (omrModel.getAnswer().equals("a")) {
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getCorrectmarks());
                    } else {
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getWrongmarks());
                    }
                }

            });
            holder.b.setOnClickListener(v -> {
                if (holder.b.isSelected()) {
                    holder.b.setChecked(false);
                    holder.b.setSelected(false);
                    selectedanswer.set(Integer.parseInt(omrModel.getQuestionno()), "x");
                    correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), "0");
                } else {
                    holder.b.setChecked(true);
                    holder.b.setSelected(true);
                    selectedanswer.set(Integer.parseInt(omrModel.getQuestionno()), "b");
                    // marks count for right or wrong answer
                    if (omrModel.getAnswer().equals("b")) {
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getCorrectmarks());
                    } else {
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getWrongmarks());
                    }

                }
            });
            holder.c.setOnClickListener(v -> {
                if (holder.c.isSelected()) {
                    holder.c.setChecked(false);
                    holder.c.setSelected(false);
                    selectedanswer.set(Integer.parseInt(omrModel.getQuestionno()), "x");
                    correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), "0");
                } else {
                    holder.c.setChecked(true);
                    holder.c.setSelected(true);
                    selectedanswer.set(Integer.parseInt(omrModel.getQuestionno()), "c");
                    // marks count for right or wrong answer
                    if (omrModel.getAnswer().equals("c")) {
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getCorrectmarks());
                    } else {
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getWrongmarks());
                    }

                }

            });
            holder.d.setOnClickListener(v -> {
                if (holder.d.isSelected()) {
                    holder.d.setChecked(false);
                    holder.d.setSelected(false);
                    selectedanswer.set(Integer.parseInt(omrModel.getQuestionno()), "x");
                    correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), "0");
                } else {
                    holder.d.setChecked(true);
                    holder.d.setSelected(true);
                    selectedanswer.set(Integer.parseInt(omrModel.getQuestionno()), "d");
                    // marks count for right or wrong answer
                    if (omrModel.getAnswer().equals("d")) {
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getCorrectmarks());
                    } else {
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getWrongmarks());
                    }

                }

            });


            omrsubmitbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //code for notattemptedquestion
                    for (int i = 1; i < selectedanswer.size(); i++) {
                        if (selectedanswer.get(i).equals("x")) {
                            notattemped = ++notattemped;
                        }
                    }

                   // Toast.makeText(getContext(), String.valueOf(notattemped), Toast.LENGTH_LONG).show();
                    //code for total makrs obtain
                    for (int i = 1; i < correctanswerlist.size(); i++) {
                        totalmarksobtained = totalmarksobtained + Integer.parseInt(correctanswerlist.get(i));
                        if (correctanswerlist.get(i).equals("4")){
                            totalcorrect=++totalcorrect;
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("totalmarks", String.valueOf(totalmarksobtained));
                    bundle.putString("correct", String.valueOf(totalcorrect));
                    bundle.putString("unattempted", String.valueOf(notattemped));
                   // bundle.putString("incorrect", String.valueOf(notattemped));
                    navController.navigate(R.id.testReport,bundle);

                      //Toast.makeText(getContext(),String.valueOf(totalmarksobtained)+String.valueOf(totalcorrect),Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return omrModelArrayList.size();
        }


        public class OmrViewholder extends RecyclerView.ViewHolder {
            TextView questionno;
            RadioButton a, b, c, d;
            RadioGroup radioGroup;

            public OmrViewholder(@NonNull View itemView) {
                super(itemView);
                a = itemView.findViewById(R.id.a);
                b = itemView.findViewById(R.id.b);
                c = itemView.findViewById(R.id.c);
                d = itemView.findViewById(R.id.d);
                questionno = itemView.findViewById(R.id.questionno);
                radioGroup = itemView.findViewById(R.id.radioGroup);


            }

        }
    }


}