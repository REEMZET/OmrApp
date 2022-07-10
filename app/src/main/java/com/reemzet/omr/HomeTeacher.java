package com.reemzet.omr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Adapter.TodaystestlistViewHolder;
import com.reemzet.omr.Models.TestDetails;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class HomeTeacher extends Fragment {


RecyclerView todaystestrecycler;
NavController navController;
FirebaseDatabase database;
DatabaseReference TestListref;
FirebaseAuth mAuth;
TestDetails testDetails;
TextView todaysdate;
    FirebaseRecyclerAdapter<TestDetails, TodaystestlistViewHolder> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home_teacher, container, false);
        todaystestrecycler=view.findViewById(R.id.todaytestrecycler);
        todaysdate=view.findViewById(R.id.todaydate);

        todaystestrecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        TestListref=database.getReference("institute").child(mAuth.getUid()).child("TestList");

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        todaysdate.setText(formattedDate);
        getdatafromserver();

        return view;
    }
    public void getdatafromserver(){
        TestListref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                testDetails=snapshot.getValue(TestDetails.class);
                setData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setData(){
        String date = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(new Date());

        Query query =TestListref.orderByChild("testdate").startAt(date).endAt(date+ "\uf8ff");
        FirebaseRecyclerOptions<TestDetails> options =
                new FirebaseRecyclerOptions.Builder<TestDetails>()
                        .setQuery(query, TestDetails.class)
                        .build();

        adapter=new FirebaseRecyclerAdapter<TestDetails,TodaystestlistViewHolder>(options){

            @NonNull
            @Override
            public TodaystestlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View rview = LayoutInflater.from(parent.getContext()).inflate(R.layout.todaytestlistlayout, parent, false);
                return new TodaystestlistViewHolder(rview);
            }

            @Override
            protected void onBindViewHolder(@NonNull TodaystestlistViewHolder holder, int position, @NonNull TestDetails model) {
                    holder.testname.setText(model.getTestname());
                    holder.totalquestion.setText("No.of Ques-"+model.getQuestionno());
                    holder.testtime.setText(model.getStarttime());
                    int totalmarks=Integer.parseInt(model.getCorrectmarks())*Integer.parseInt(model.getQuestionno());
                    holder.tvtotalmarks.setText("Marks-"+totalmarks);
                    totalmarks=0;
                    holder.duration.setText("  Duration\n   "+model.getTesttime());
                    holder.date.setText("Date\n"+model.getTestdate());
            }
        };
        todaystestrecycler.setAdapter(adapter);
        adapter.startListening();
    }
}