package com.reemzet.omr;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reemzet.omr.Models.InstuteDetails;
import com.reemzet.omr.Models.StudentsModel;

import java.util.concurrent.TimeUnit;


public class RegisterStudent extends Fragment {

EditText etstudenname,etstudentphone,etcity,etstudentemail,etpreparation,etotp;
String studentname,studentphone,studentcity,studentemail,preparation,otpid;
Button studentregisterbtn;
    CheckBox studentagreecheckbox;
    boolean fieldboolean;
    FirebaseAuth mAuth;
    NavController navController;
    FirebaseDatabase database;
    DatabaseReference studentref;
    ProgressDialog progressDialog;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_register_student, container, false);
        etstudenname=view.findViewById(R.id.etstudentname);
        etstudentphone=view.findViewById(R.id.etstudentphone);
        etcity=view.findViewById(R.id.etstudentCity);
        etstudentemail=view.findViewById(R.id.etstudentemail);
        etpreparation=view.findViewById(R.id.etpreparation);
        etotp=view.findViewById(R.id.etstudentotp);
        studentregisterbtn=view.findViewById(R.id.studentregisterbtn);
        studentagreecheckbox=view.findViewById(R.id.agreecheckbox);


        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
       studentref=database.getReference("students");

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();


        studentregisterbtn.setOnClickListener(v -> {
            if (studentregisterbtn.getText().toString().equals("GET OTP")){
                fieldcheck();
                if (fieldboolean){
                    showloding();
                    otpsent(studentphone);

                }

            }else{
                if (etotp.getText().toString().length()<6){
                    etotp.setError("Invalid Otp");
                }else {
                    showloding();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpid, etotp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });








        return view;
    }

    private void disableEdittext(){
       etstudenname.setEnabled(false);
        etstudentemail.setEnabled(false);
        etstudentphone.setEnabled(false);
       etcity.setEnabled(false);
        etpreparation.setEnabled(false);

    }

    private boolean fieldcheck() {

        if (etstudenname.getText().toString().isEmpty()) {
            etstudenname.setError("field cant be empty");
            fieldboolean = false;
        }else {
            etstudenname.setError(null);
            studentname = etstudenname.getText().toString();
            fieldboolean = true;
        }if (etcity.getText().toString().isEmpty()) {
            etcity.setError("can't be empty");
            fieldboolean = false;
        } else {
            etcity.setError(null);
            studentcity= etcity.getText().toString();
            fieldboolean = true;
        }


        if (etstudentemail.getText().toString().isEmpty()) {
            etstudentemail.setError("can't be empty");
            fieldboolean = false;
        } else {
            etstudentemail.setError(null);
            studentemail = etstudentemail.getText().toString();
            fieldboolean = true;
        }
        if (etpreparation.getText().toString().isEmpty()) {
            fieldboolean = false;
            etpreparation.setError("can't be empty");
        } else {
            etpreparation.setError(null);
            preparation = etpreparation.getText().toString();
            fieldboolean = true;
        }

        if (etstudentphone.getText().toString().isEmpty()) {
            etstudentphone.setError("can't be empty");
            fieldboolean = false;
        }else if (etstudentphone.getText().toString().length()<10){
            etstudentphone.setError("incorrect phone Number");
            fieldboolean = false;
        }
        else {
            etstudentphone.setError(null);
            studentphone = etstudentphone.getText().toString();
            fieldboolean = true;
        }


        return fieldboolean;
    }


    public void otpsent(String phone) {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getActivity(), "failed" + e.getMessage(), Toast.LENGTH_LONG).show();

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                otpid=verificationId;
                etotp.setVisibility(View.VISIBLE);
                disableEdittext();
                studentregisterbtn.setText("Signup my Account");
                progressDialog.dismiss();
                etotp.requestFocus();

            }
        };
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(getActivity(),"success",Toast.LENGTH_SHORT).show();
                        sentdatatodatabase();

                    } else {
                        // Sign in failed, display a message and update the UI

                        Toast.makeText(getActivity(), "failed to verify", Toast.LENGTH_SHORT).show();


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
    public void sentdatatodatabase(){
        StudentsModel studentsModel=new StudentsModel(studentname,studentphone,studentcity,studentemail,preparation,"Batch");

        studentref.child(mAuth.getUid()).setValue(studentsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                navController.popBackStack();
                navController.navigate(R.id.homeStudent);

            }
        });

    }
}