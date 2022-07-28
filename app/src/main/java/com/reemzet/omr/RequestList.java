package com.reemzet.omr;

import android.app.ProgressDialog;
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

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.reemzet.omr.Adapter.RequestListViewHolder;

import com.reemzet.omr.Models.StudentsModel;

import java.util.HashMap;

public class RequestList extends Fragment  {

    RecyclerView recyclerView;
    String orgcode;
    FirebaseDatabase database;
    DatabaseReference requestlistref,studentlistref,instituteref;
    FirebaseRecyclerAdapter<StudentsModel, RequestListViewHolder> adapter;
    StudentsModel studentsModel;
    NavController navController;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_request_list, container, false);
        orgcode=getArguments().getString("orgcode");
        recyclerView=view.findViewById(R.id.requestlistrecyclerview);

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        database=FirebaseDatabase.getInstance();
        requestlistref=database.getReference("institute").child(orgcode).child("BatchrequestList");
        studentlistref=database.getReference("students");
        instituteref=database.getReference("institute");
        getDatafromServer();



        return view;
    }

    private void getDatafromServer() {
    requestlistref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()){
               studentsModel=snapshot.getValue(StudentsModel.class);
                    setData();
            }else {
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
                .setQuery(requestlistref,StudentsModel.class).build();


        adapter=new FirebaseRecyclerAdapter<StudentsModel, RequestListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestListViewHolder holder, int position, @NonNull StudentsModel model) {
                    holder.tvstudentname.setText(model.getStudenname());
                    holder.tvstudentcity.setText("City-"+model.getStudentcity());
                    holder.tvstudentmobile.setText("Mob-"+model.getStudentphone());
                Glide.with(getActivity())
                        .load(model.getImageurl())
                        .centerCrop()
                        .placeholder(R.drawable.student)
                        .into(holder.studentimg);

            holder.tvreject.setOnClickListener(v -> {
                showloding();

                    requestlistref.child(model.getStudentuid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            studentlistref.child(model.getStudentuid()).child("requestedbatch").setValue("NoRequestedBatch")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                          progressDialog.dismiss();
                                        }
                                    });
                        }
                    });
            });
            holder.tvaccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showloding();
                    requestlistref.child(model.getStudentuid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            HashMap<String,Object> accept=new HashMap();
                            accept.put("requestedbatch","NoRequestedBatch");
                            accept.put("batch",orgcode);
                            studentlistref.child(model.getStudentuid()).updateChildren(accept)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            model.setBatch(orgcode);
                                            model.setRequestedbatch("NoRequestedBatch");
                                            instituteref.child(orgcode).child("StduentList").child(model.getStudentuid()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                }
            });
            }


            @NonNull
            @Override
            public RequestListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View rview = LayoutInflater.from(parent.getContext()).inflate(R.layout.requestlistlayout, parent, false);
                return new RequestListViewHolder(rview);
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    public void showloding() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialoprogress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }

}