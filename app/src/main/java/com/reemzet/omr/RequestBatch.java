package com.reemzet.omr;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Adapter.BatchlistViewHolder;
import com.reemzet.omr.Adapter.TodaystestlistViewHolder;
import com.reemzet.omr.Models.InstuteDetails;
import com.reemzet.omr.Models.StudentsModel;
import com.reemzet.omr.Models.TestDetails;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class RequestBatch extends Fragment {

    NavController navController;
    FirebaseDatabase database;
    DatabaseReference batchref,studentref,instituteref,serverkeyref;
    RecyclerView recyclerView;
    EditText editText;
    String city;
    InstuteDetails instuteDetails;
    Query batch;
    TextView tvsuggestion;
    FirebaseRecyclerAdapter<InstuteDetails, BatchlistViewHolder> adapter;
    FirebaseAuth mAuth;
    StudentsModel studentsModel;
    String serverkey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_request_batch, container, false);
        recyclerView=view.findViewById(R.id.batchrecyclerview);
        editText=view.findViewById(R.id.etsearch);
        tvsuggestion=view.findViewById(R.id.tvsuggestion);
        city=getArguments().getString("city");

        mAuth=FirebaseAuth.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        database=FirebaseDatabase.getInstance();
        batchref=database.getReference("Organisation");
        instituteref=database.getReference("institute");
        studentref=database.getReference().child("students").child(mAuth.getUid());
        serverkeyref=database.getReference("sahreimgurl");
        getStudentData();
        loadserverkey();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    if (s.length()==0){
                        tvsuggestion.setVisibility(View.VISIBLE);
                        batch =batchref.orderByChild("city").startAt(city).endAt(city+ "\uf8ff");
                        getdatafromserver();
                    }else {
                        tvsuggestion.setVisibility(View.GONE);
                        batch =batchref.orderByChild("orgcode").startAt(s.toString()).endAt(s+ "\uf8ff");
                        getdatafromserver();
                    }
            }
        });



        return view;
    }

    private void getStudentData() {
        studentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    studentsModel=snapshot.getValue(StudentsModel.class);
                    batch =batchref.orderByChild("city").startAt(city).endAt(city+ "\uf8ff");
                    getdatafromserver();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void getdatafromserver(){

        batchref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    instuteDetails=snapshot.getValue(InstuteDetails.class);
                    setData();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setData() {
        FirebaseRecyclerOptions<InstuteDetails> options =
                new FirebaseRecyclerOptions.Builder<InstuteDetails>()
                        .setQuery(batch, InstuteDetails.class)
                        .build();


        adapter=new FirebaseRecyclerAdapter<InstuteDetails, BatchlistViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BatchlistViewHolder holder, int position, @NonNull InstuteDetails model) {
                if (studentsModel.getRequestedbatch().equals(model.getOrgcode())){
                    holder.tvsendrequest.setText("Request Sent");
                    holder.tvsendrequest.setBackgroundResource(R.drawable.bg_gray);
                    holder.tvsendrequest.setTextColor(getResources().getColor(R.color.dselect));
                    holder.tvsendrequest.setEnabled(false);
                }else {
                    holder.tvsendrequest.setText("Send Request");
                    holder.tvsendrequest.setEnabled(true);
                    holder.tvsendrequest.setTextColor(Color.WHITE);
                    holder.tvsendrequest.setBackgroundResource(R.drawable.editnowbg);
                }
                if (studentsModel.getBatch().equals(model.getOrgcode())){
                    holder.tvsendrequest.setText("Joined");
                    holder.tvsendrequest.setBackgroundResource(R.drawable.bg_gray);
                    holder.tvsendrequest.setTextColor(getResources().getColor(R.color.dselect));
                    holder.tvsendrequest.setEnabled(false);
                }
                holder.tvteachername.setText("Teacher:-"+model.getTeachername());
                holder.tvorgcode.setText("OrgCode:-"+model.getOrgcode());
                holder.tvinstitutecity.setText("City:-"+model.getCity());
                holder.tvinstitutestate.setText("State:-"+model.getState());
                holder.tvinsitutename.setText(model.getInstitutename());

                Picasso.get()
                        .load(model.getInstituteimage())
                        .placeholder(R.drawable.university)
                        .error(R.drawable.university)
                        .into(holder.instituteimage);
                holder.tvsendrequest.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                            if (studentsModel.getRequestedbatch().equals("NoRequestedBatch")){
                                instituteref.child(model.getOrgcode()).child("BatchrequestList").child(mAuth.getUid()).setValue(studentsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        studentref.child("requestedbatch").setValue(model.getOrgcode()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/admin"+model.getOrgcode(), "You have new request", studentsModel.getStudenname()+" wants to join your institute", getActivity(), getActivity(),serverkey);
                                                notificationsSender.SendNotifications();
                                            }
                                        });
                                    }
                                });
                            }else {
                                instituteref.child(studentsModel.getRequestedbatch()).child("BatchrequestList").child(mAuth.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        instituteref.child(model.getOrgcode()).child("BatchrequestList").child(mAuth.getUid()).setValue(studentsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                studentref.child("requestedbatch").setValue(model.getOrgcode()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/admin"+model.getOrgcode(), "You have new request", studentsModel.getStudenname()+" wants to join your institute", getContext(), getActivity(),serverkey);
                                                        notificationsSender.SendNotifications();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                    }
                });
            }

            @NonNull
            @Override
            public BatchlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View rview = LayoutInflater.from(parent.getContext()).inflate(R.layout.batchlistlayout, parent, false);
                return new BatchlistViewHolder(rview);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public void loadserverkey(){
        serverkeyref.child("url").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    serverkey=snapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}