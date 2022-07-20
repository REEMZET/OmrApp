package com.reemzet.omr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.CircularToggle;
import com.reemzet.omr.Models.OmrModel;
import com.reemzet.omr.Models.RankModel;
import com.reemzet.omr.Models.ScoreModel;
import com.reemzet.omr.Models.StudentsModel;
import com.reemzet.omr.Models.TestDetails;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;


public class Omr extends Fragment {
    RecyclerView omrrecycler;
    DatabaseReference testanswer, testdetails,studentref;
    TextView omrsubmitbtn;
    TestDetails testDetails;
    ArrayList<OmrModel> list;
    ArrayList<String> answerlist;
    ArrayList<String> selectedanswer;
    ArrayList<String> correctanswerlist;
    int notattemped = 0, totalmarksobtained,totalcorrect,unattemped;
    NavController navController;
    FirebaseAuth mAuth;
    String orgcode,testref,studentname,studentimg,studentcity;
    TextView tvtimer,tvunattempted;
    CountDownTimer timer;
    String ticticktime;
    FirebaseFirestore db;
    OmrAdapter.OmrViewholder vholder ;
    ProgressDialog progressDialog;
    Date starttime,endtime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_omr, container, false);
        omrrecycler = view.findViewById(R.id.omrreycler);
        omrsubmitbtn = view.findViewById(R.id.omrsubmitbtn);
        tvtimer=view.findViewById(R.id.tvtimer);
        tvunattempted=view.findViewById(R.id.tvunattempted);
         db= FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        orgcode=getArguments().getString("orgcode");
        testref=getArguments().getString("testref");
        studentname=getArguments().getString("studentname");
        studentimg=getArguments().getString("studentimg");
        studentcity=getArguments().getString("studentcity");



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        omrrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        testanswer=database.getReference("institute").child(orgcode).child("TestList").child(testref).child("answerlist");
        testdetails=database.getReference("institute").child(orgcode).child("TestList").child(testref);
        studentref=FirebaseDatabase.getInstance().getReference("institute").child(orgcode).child("StduentList").child(mAuth.getUid());
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        checkattemptest();
        
        list = new ArrayList<>();
        answerlist = new ArrayList<>();
        selectedanswer = new ArrayList<>();
        correctanswerlist = new ArrayList<>();
        list.clear();

        testdetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                answerlist.clear();
                correctanswerlist.clear();
                selectedanswer.clear();
                testDetails = snapshot.getValue(TestDetails.class);
                testanswer.addListenerForSingleValueEvent(new ValueEventListener() {
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

                        tvunattempted.setText(testDetails.getQuestionno()+" Left");
                        setTimer(60000*Integer.parseInt(testDetails.getTesttime()));
                        starttime = Calendar.getInstance().getTime();
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

        omrsubmitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                submitscoretofirebase( );

                //Toast.makeText(getContext(),String.valueOf(totalmarksobtained)+String.valueOf(totalcorrect),Toast.LENGTH_LONG).show();
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
         OmrModel   omrModel = omrModelArrayList.get(position);
            vholder=holder;
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
                    unattempedCount();
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
                    unattempedCount();
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
                    unattempedCount();
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
                    unattempedCount();
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
           public CheckBox a, b, c, d;



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

    public void unattempedCount(){

        for (int incr = 0; incr < selectedanswer.size(); incr++) {
            if (selectedanswer.get(incr).equals("x")) {
                unattemped++;
            }
        }
        tvunattempted.setText(String.valueOf(unattemped-1)+" Left");
        unattemped=0;
    }
    public void setTimer(int time) {
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long timeLeft = millisUntilFinished / 1000;
                    ticticktime=String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                tvtimer.setText(ticticktime);

            }

            @Override
            public void onFinish() {

                    submitscoretofirebase();
            }
        }.start();
    }
        public void submitscoretofirebase(){
            endtime = Calendar.getInstance().getTime();
            long totalTime = endtime.getTime() - starttime.getTime();
            String timestamp=String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(totalTime),
                    TimeUnit.MILLISECONDS.toSeconds(totalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime)));
        showloding();
            vholder.a.setEnabled(false);
            vholder.b.setEnabled(false);
            vholder.c.setEnabled(false);
            vholder.d.setEnabled(false);

            //code for notattemptedquestion
            for (int incr = 0; incr < selectedanswer.size(); incr++) {
                if (selectedanswer.get(incr).equals("x")) {
                    notattemped++;
                }
            }
            //code for total makrs obtain
            for (int i = 1; i < correctanswerlist.size(); i++) {
                totalmarksobtained = totalmarksobtained + Integer.parseInt(correctanswerlist.get(i));
                if (correctanswerlist.get(i).equals(testDetails.getCorrectmarks())) {
                    totalcorrect = ++totalcorrect;
                }
            }
            //uploadtofirebase
            for (int i = 0; i < selectedanswer.size()-1; i++) {
                int b = 1;
                testdetails.child("StudentResponse").child(mAuth.getUid()).child("SelectedAnswer").child(String.valueOf(b + i)).setValue(selectedanswer.get(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {



                    }
                });
            }

            ScoreModel scoreModel=new ScoreModel(String.valueOf(totalmarksobtained),String.valueOf(totalcorrect),String.valueOf(notattemped-1),String.valueOf(answerlist.size()-1),timestamp);
            testdetails.child("StudentResponse").child(mAuth.getUid()).child("Score").setValue(scoreModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    RankModel rankModel=new RankModel(studentname,mAuth.getUid(),studentimg,timestamp,totalmarksobtained);
                    db.collection("Score").document(orgcode).collection(testref).document(mAuth.getUid()).set(rankModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updatestudenttotalreport();
                            Bundle bundle=new Bundle();
                            bundle.putString("totalmarks",String.valueOf(totalmarksobtained));
                            bundle.putString("totalquestion",String.valueOf(answerlist.size()-1));
                            bundle.putString("unattempted",String.valueOf(notattemped-1));
                            bundle.putString("testname",testDetails.getTestname());
                            progressDialog.dismiss();
                            navController.popBackStack();
                            navController.navigate(R.id.testReport,bundle);
                            list.clear();
                            selectedanswer.clear();
                            answerlist.clear();
                            correctanswerlist.clear();
                        }
                    });
                }
            });

        }
    public void showloding() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialoprogress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    public void updatestudenttotalreport(){
        HashMap<String, Object> updatereport = new HashMap<>();
        studentref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StudentsModel updatemodel=snapshot.getValue(StudentsModel.class);
                int totaltest=Integer.parseInt(updatemodel.getTotaltest());
                int totalmarks=Integer.parseInt(updatemodel.getTotalmarksobtained());
                int maximummarks=Integer.parseInt(updatemodel.getMaximumtestmarks());
                updatereport.put("totaltest",String.valueOf(totaltest+1));
                updatereport.put("totalmarksobtained",String.valueOf(totalmarks+totalmarksobtained));
                updatereport.put("maximumtestmarks",String.valueOf(maximummarks+Integer.parseInt(testDetails.getQuestionno())*Integer.parseInt(testDetails.getCorrectmarks())));
                studentref.updateChildren(updatereport);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void checkattemptest(){
        showloding();
        testdetails.child("StudentResponse").orderByChild(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "you have already attempted", Toast.LENGTH_SHORT).show();
                    navController.popBackStack();
                    navController.navigate(R.id.homeStudent);

                }else {
                    progressDialog.dismiss();
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}