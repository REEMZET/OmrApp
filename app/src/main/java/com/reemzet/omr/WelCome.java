package com.reemzet.omr;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Models.InstuteDetails;
import com.reemzet.omr.Models.StudentsModel;

public class WelCome extends Fragment {
    Button btnstudent,teacherbtn;
    NavController navController;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference teacherref,instituteref,studentref;
    ProgressDialog progressDialog;
    StudentsModel studentsModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_wel_come, container, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        btnstudent=view.findViewById(R.id.studentbtn);
        teacherbtn=view.findViewById(R.id.teacherbtn);
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        teacherref=database.getReference("Teacherlist");
        instituteref=database.getReference("institute");
        studentref=database.getReference("students");
        progressDialog = new ProgressDialog(getActivity());
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Showing", Toast.LENGTH_SHORT).show();
            }

        teacherbtn.setOnClickListener(v -> navController.navigate(R.id.action_welCome_to_register));
        btnstudent.setOnClickListener(v -> { navController.navigate(R.id.action_welCome_to_registerStudent); });

       return view;
    }
    public void showloding() {

        progressDialog.show();
        progressDialog.setContentView(R.layout.dialoprogress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void checkloginuser(){
        showloding();
        if (mAuth.getCurrentUser()!=null&& mAuth.getUid()!=null){
            teacherref.orderByChild("teacheruid").startAt(mAuth.getUid()).endAt(mAuth.getUid()+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        teacherref.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                InstuteDetails instuteDetails = snapshot.getValue(InstuteDetails.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("orgcode", instuteDetails.getOrgcode());
                                bundle.putString("teachername",instuteDetails.getTeachername());
                                bundle.putString("phone",instuteDetails.getTeacherphone());
                                navController.popBackStack();
                                navController.navigate(R.id.homeTeacher, bundle);
                                progressDialog.dismiss();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }});
                    }else {
                        student();
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            progressDialog.dismiss();
        }
    }

    public void student(){
        studentref.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    studentsModel=snapshot.getValue(StudentsModel.class);
                    navController.popBackStack();
                    progressDialog.dismiss();
                    navController.navigate(R.id.homeStudent);

                }else {
                    mAuth.signOut();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        checkloginuser();

    }

}
