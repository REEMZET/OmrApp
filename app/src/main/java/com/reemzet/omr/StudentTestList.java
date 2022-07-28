package com.reemzet.omr;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Adapter.TeacherTestListViewHolder;
import com.reemzet.omr.Models.ScoreModel;
import com.reemzet.omr.Models.TestDetails;

import java.io.BufferedInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class StudentTestList extends Fragment {

    RecyclerView studenttestlistrecycler;
    NavController navController;
    FirebaseDatabase database;
    DatabaseReference TestListref;
    TestDetails testDetails;
    String orgcode;
    FirebaseRecyclerAdapter<TestDetails, TeacherTestListViewHolder> adapter;
    String testkey;
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.studenttestlist, container, false);
        studenttestlistrecycler=view.findViewById(R.id.studenttestlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
       studenttestlistrecycler.setLayoutManager(linearLayoutManager);
        mAuth=FirebaseAuth.getInstance();

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        orgcode=getArguments().getString("orgcode");

        database=FirebaseDatabase.getInstance();
        TestListref=database.getReference("institute").child(orgcode).child("TestList");

        getdatafromserver();

    return view;
    }

    public void getdatafromserver(){
        TestListref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                testDetails=snapshot.getValue(TestDetails.class);
                testkey=snapshot.getKey();
                setData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setData(){

        FirebaseRecyclerOptions<TestDetails> options =
                new FirebaseRecyclerOptions.Builder<TestDetails>()
                        .setQuery(TestListref, TestDetails.class)
                        .build();

        adapter=new FirebaseRecyclerAdapter<TestDetails,TeacherTestListViewHolder>(options){

            @NonNull
            @Override
            public TeacherTestListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View rview = LayoutInflater.from(parent.getContext()).inflate(R.layout.teachertestlistlayout, parent, false);
                return new TeacherTestListViewHolder(rview);
            }

            @Override
            protected void onBindViewHolder(@NonNull TeacherTestListViewHolder holder, int position, @NonNull TestDetails model) {
                holder.testname.setText(model.getTestname());
                holder.totalquestion.setText("No.of Ques-"+model.getQuestionno());
                holder.testtime.setText(model.getStarttime());
                int totalmarks=Integer.parseInt(model.getCorrectmarks())*Integer.parseInt(model.getQuestionno());
                holder.tvtotalmarks.setText("Marks-"+totalmarks);
                totalmarks=0;
                holder.duration.setText("Duration:-"+model.getTesttime()+"mins");
                holder.date.setText("Date:-"+model.getTestdate());
                holder.tteststatus.setText(model.getStatus());
                holder.tvtestcode.setText("pass-"+model.getTestcode());
                if (holder.tteststatus.getText().equals("Answer not Set")){
                    holder.tteststatus.setTextColor(Color.RED);
                }
                holder.tvsetanswer.setVisibility(View.INVISIBLE);
                holder.editnow.setVisibility(View.INVISIBLE);
                holder.tvdeletebtn.setVisibility(View.INVISIBLE);
                holder.tvtestcode.setVisibility(View.INVISIBLE);
                holder.tvreport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( checktestperoid(model.getStarttime(),Integer.parseInt(model.getTesttime()))|| model.getResultstatus().equals("pub")){
                            sendtoscoreview(model);

                        }else Toast.makeText(getActivity(), "Result not Published. Contact to teacher", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        studenttestlistrecycler.setAdapter(adapter);
        adapter.startListening();
    }

    private void sendtoscoreview(TestDetails model) {
        DatabaseReference scoreref=database.getReference("institute").child(orgcode).child("TestList").child(model.getTestid()).child("StudentResponse").child(mAuth.getUid()).child("Score");
        scoreref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ScoreModel scoreModel=snapshot.getValue(ScoreModel.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("orgcode",orgcode);
                    bundle.putString("testid",model.getTestid());
                    bundle.putString("teststarttime",model.getStarttime());
                    bundle.putString("testduration",model.getTesttime());
                    bundle.putString("totalmarks", scoreModel.getTotalmarks());
                    bundle.putString("correctquestion", scoreModel.getCorrectquestion());
                    bundle.putString("unattempedquestion", scoreModel.getUnattempedquestion());
                    bundle.putString("totalquestion", scoreModel.getTotalquestion());
                    bundle.putString("eachcorrectmarks",model.getCorrectmarks());
                    navController.navigate(R.id.score,bundle);
                }else {
                    Toast.makeText(getActivity(), "you have not attempted this test", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public boolean checktestperoid(String testtime,int duration){
        SimpleDateFormat df = new SimpleDateFormat("h:mm:ss a");
        Date d = null;
        try {
            d = df.parse(testtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, duration);
        String newTime = df.format(cal.getTime());

        String currentTime = new SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(new Date());
        String pattern = "h:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(newTime);
            Date date2 = sdf.parse(currentTime);

            if(date1.after(date2)) {

                return false;

            } else {

                return true;

            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }



}