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
        studenttestlistrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                        if (model.getResultstatus().equals("notpub")){
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
                ScoreModel scoreModel=snapshot.getValue(ScoreModel.class);

                Bundle bundle=new Bundle();

                bundle.putString("orgcode",orgcode);
                bundle.putString("testid",model.getTestid());
                bundle.putString("totalmarks", scoreModel.getTotalmarks());
                bundle.putString("correctquestion", scoreModel.getCorrectquestion());
                bundle.putString("unattempedquestion", scoreModel.getUnattempedquestion());
                bundle.putString("totalquestion", scoreModel.getTotalquestion());
                navController.navigate(R.id.score,bundle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}