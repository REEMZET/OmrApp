package com.reemzet.omr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Adapter.JoinedStudentHolder;
import com.reemzet.omr.Adapter.RequestListViewHolder;
import com.reemzet.omr.Models.StudentsModel;


public class JoinedStudent extends Fragment {


    RecyclerView joinedstudentrecycler;
    FirebaseDatabase database;
    DatabaseReference studentlistref;
    FirebaseRecyclerAdapter<StudentsModel, JoinedStudentHolder> adapter;
    StudentsModel studentsModel;
    String orgcode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_joined_student, container, false);
        orgcode = getArguments().getString("orgcode");
        joinedstudentrecycler = view.findViewById(R.id.jionedstudentrecycler);
        joinedstudentrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        database=FirebaseDatabase.getInstance();
        studentlistref = database.getReference("institute").child(orgcode).child("StduentList");
        getDatafromServer();
        return view;
    }

    private void getDatafromServer() {
        studentlistref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    studentsModel = snapshot.getValue(StudentsModel.class);
                    setData();
                } else {
                    Toast.makeText(getActivity(), "You have not any Request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setData() {
        FirebaseRecyclerOptions<StudentsModel> options=new FirebaseRecyclerOptions.Builder<StudentsModel>()
                .setQuery(studentlistref,StudentsModel.class).build();
        adapter=new FirebaseRecyclerAdapter<StudentsModel, JoinedStudentHolder>(options){

            @NonNull
            @Override
            public JoinedStudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View rview = LayoutInflater.from(parent.getContext()).inflate(R.layout.joinedstudentlayout, parent, false);
                return new JoinedStudentHolder(rview);
            }

            @Override
            protected void onBindViewHolder(@NonNull JoinedStudentHolder holder, int position, @NonNull StudentsModel model) {
                holder.tvjoinedstudentname.setText(model.getStudenname());
                holder.tvjoinedstudentmob.setText("Mob:-"+model.getStudentphone());
                holder.tvjoinedstudentcity.setText("City:-"+model.getStudentcity());
                holder.tvjoinedstudentemail.setText("Email:-"+model.getStudentemail());
                Glide.with(getActivity())
                        .load(model.getImageurl())
                        .centerCrop()
                        .placeholder(R.drawable.student)
                        .into(holder.joinedstudentimg);

            }
        };
        joinedstudentrecycler.setAdapter(adapter);
        adapter.startListening();
    }
}