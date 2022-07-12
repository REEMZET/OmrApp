package com.reemzet.omr;

import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.CircularToggle;
import com.reemzet.omr.Models.OmrModel;
import com.reemzet.omr.Models.TestDetails;

import java.util.ArrayList;
import java.util.List;


public class Omr extends Fragment {
    RecyclerView omrrecycler;
    DatabaseReference testanswer, testdetails;
    Button omrsubmitbtn;
    TestDetails testDetails;
    ArrayList<OmrModel> list;
    ArrayList<String> answerlist;
    ArrayList<String> selectedanswer;
    ArrayList<String> correctanswerlist;
    int notattemped = 0, totalmarksobtained,totalcorrect,totalincorrect;
    NavController navController;
    FirebaseAuth mAuth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_omr, container, false);
        omrrecycler = view.findViewById(R.id.omrreycler);
        omrsubmitbtn = view.findViewById(R.id.omrsubmitbtn);
        mAuth=FirebaseAuth.getInstance();



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        omrrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        testanswer = database.getReference("/institute/0XNJvUARCaWGczBt902QWxWWUYM2/TestList/-N6loYxofPBKvW_cmDej/answerlist");
        testdetails=database.getReference("/institute/0XNJvUARCaWGczBt902QWxWWUYM2/TestList/-N6loYxofPBKvW_cmDej");
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();


        list = new ArrayList<>();
        answerlist = new ArrayList<>();
        selectedanswer = new ArrayList<>();
        correctanswerlist = new ArrayList<>();
        list.clear();
        answerlist.clear();
        correctanswerlist.clear();
        selectedanswer.clear();
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

            OmrViewholder holder = new OmrViewholder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull OmrViewholder holder, int position) {
            OmrModel omrModel = omrModelArrayList.get(position);
            holder.questionno.setText(omrModel.getQuestionno());
            switch (selectedanswer.get(holder.getAdapterPosition())){

                case "a":
                    holder.a.setChecked(true);
                    break;
                case "b":
                    holder.b.setChecked(true);
                    break;
                case "c":
                    holder.c.setChecked(true);
                    break;
                case "d":
                    holder.d.setChecked(true);
                    break;
            }
            holder.a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.a.isChecked()){
                        selectedanswer.set(holder.getAdapterPosition(), "a");
                        if (omrModel.getAnswer().equals("a")) {
                            correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getCorrectmarks());
                        } else {
                            correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getWrongmarks());
                        }
                        holder.b.setChecked(false);
                        holder.c.setChecked(false);
                        holder.d.setChecked(false);
                    }else {
                        selectedanswer.set(holder.getAdapterPosition(), "x");
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), "0");
                    }

                }
            });
            holder.b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.b.isChecked()){
                        selectedanswer.set(holder.getAdapterPosition(), "b");
                        if (omrModel.getAnswer().equals("b")) {
                            correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getCorrectmarks());
                        } else {
                            correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getWrongmarks());
                        }
                        holder.a.setChecked(false);
                        holder.c.setChecked(false);
                        holder.d.setChecked(false);
                    }else {
                        selectedanswer.set(holder.getAdapterPosition(), "x");
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), "0");
                    }

                }
            });
            holder.c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.c.isChecked()){
                        selectedanswer.set(holder.getAdapterPosition(), "c");
                        if (omrModel.getAnswer().equals("c")) {
                            correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getCorrectmarks());
                        } else {
                            correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getWrongmarks());
                        }
                        holder.a.setChecked(false);
                        holder.b.setChecked(false);
                        holder.d.setChecked(false);
                    }else {
                        selectedanswer.set(holder.getAdapterPosition(), "x");
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), "0");
                    }

                }
            });
            holder.d.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.d.isChecked()){
                        selectedanswer.set(holder.getAdapterPosition(), "d");
                        if (omrModel.getAnswer().equals("d")) {
                            correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getCorrectmarks());
                        } else {
                            correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), testDetails.getWrongmarks());
                        }
                        holder.a.setChecked(false);
                        holder.b.setChecked(false);
                        holder.c.setChecked(false);
                    }else {
                        selectedanswer.set(holder.getAdapterPosition(), "x");
                        correctanswerlist.set(Integer.parseInt(omrModel.getQuestionno()), "0");
                    }

                }
            });


            omrsubmitbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //code for notattemptedquestion
                    for (int incr = 0; incr < selectedanswer.size(); incr++) {
                        if (selectedanswer.get(incr).equals("x")) {
                          notattemped++;
                        }
                    }
                    //code for total makrs obtain
                    for (int i = 1; i < correctanswerlist.size(); i++) {
                        totalmarksobtained = totalmarksobtained + Integer.parseInt(correctanswerlist.get(i));
                        if (correctanswerlist.get(i).equals("4")) {
                            totalcorrect = ++totalcorrect;
                        }
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("totalmarks", String.valueOf(totalmarksobtained));
                    bundle.putString("correct", String.valueOf(totalcorrect));
                    bundle.putString("unattempted", String.valueOf(notattemped-1));
                     bundle.putString("totalquestion", String.valueOf(answerlist.size()-1));
                    navController.navigate(R.id.testReport, bundle);
                    selectedanswer.clear();
                    correctanswerlist.clear();


                    //Toast.makeText(getContext(),String.valueOf(totalmarksobtained)+String.valueOf(totalcorrect),Toast.LENGTH_LONG).show();
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

}