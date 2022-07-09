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

import java.util.concurrent.TimeUnit;


public class Register extends Fragment {
        ProgressDialog progressDialog;
        Button registerbtn;
        EditText etteachername,etinstitutename,etorgcode,etcity,etemail,etphone,etotp;
        CheckBox agreecheckbox;
        String teachername,institutename,orgcode,city,teacheremail,teacherphone,otpid;
        boolean fieldboolean;
        FirebaseAuth mAuth;
        NavController navController;
        FirebaseDatabase database;
        DatabaseReference teacherref,instituteref;
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_register, container, false);
        registerbtn=view.findViewById(R.id.registerbtn);
        etteachername=view.findViewById(R.id.etteachername);
        etcity=view.findViewById(R.id.etcity);
        etinstitutename=view.findViewById(R.id.etinstitutename);
        etorgcode=view.findViewById(R.id.etuniquecode);
        etemail=view.findViewById(R.id.etteacheremail);
        etphone=view.findViewById(R.id.etteacherphone);
        agreecheckbox=view.findViewById(R.id.agreecheckbox);
        etotp=view.findViewById(R.id.ettotp);


        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        teacherref=database.getReference("Teacherlist");
        instituteref=database.getReference("institute");
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();



        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (registerbtn.getText().toString().equals("Get Otp")){
                   if (fieldcheck()){
                       showloding();
                        otpsent(teacherphone);
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
            }
        });






    return view;
    }
    private void disableEdittext(){
        etteachername.setEnabled(false);
        etinstitutename.setEnabled(false);
        etorgcode.setEnabled(false);
        etcity.setEnabled(false);
        etemail.setEnabled(false);
        etphone.setEnabled(false);
    }
    private boolean fieldcheck() {

        if (etteachername.getText().toString().isEmpty()) {
            etteachername.setError("field cant be empty");
            fieldboolean = false;
        }else {
            etteachername.setError(null);
            teachername = etteachername.getText().toString();
            fieldboolean = true;
        }if (etcity.getText().toString().isEmpty()) {
            etcity.setError("can't be empty");
            fieldboolean = false;
        } else {
            etcity.setError(null);
            city= etcity.getText().toString();
            fieldboolean = true;
        }


        if (etemail.getText().toString().isEmpty()) {
            etemail.setError("can't be empty");
            fieldboolean = false;
        } else {
            etemail.setError(null);
            teacheremail = etemail.getText().toString();
            fieldboolean = true;
        }
        if (etinstitutename.getText().toString().isEmpty()) {
            etinstitutename.setError("can't be empty");
            fieldboolean = false;
        } else {
            etinstitutename.setError(null);
            institutename = etinstitutename.getText().toString();
            fieldboolean = true;
        }
        if (etorgcode.getText().toString().isEmpty()) {
            etorgcode.setError("can't be empty");
            fieldboolean = false;
        }else if (etorgcode.getText().toString().length()<5){
            etorgcode.setError("please provide 5 letter for unique code");
            fieldboolean = false;
        }
        else {
            etorgcode.setError(null);
            orgcode = etorgcode.getText().toString();
            fieldboolean = true;
        }
        if (etphone.getText().toString().isEmpty()) {
            etphone.setError("can't be empty");
            fieldboolean = false;
        }else if (etphone.getText().toString().length()<10){
            etphone.setError("incorrect phone Number");
            fieldboolean = false;
        }
        else {
            etphone.setError(null);
            teacherphone = etphone.getText().toString();
            fieldboolean = true;
        }
        if (!agreecheckbox.isChecked()) {
            agreecheckbox.setError("can't be empty");
            fieldboolean = false;
        } else {
           agreecheckbox.setError(null);
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
                                             registerbtn.setText("Signup my Account");
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
        InstuteDetails instuteDetails=new InstuteDetails(teachername,institutename,orgcode,city,teacheremail,teacherphone,"TEACHER");
        teacherref.child(mAuth.getUid()).setValue(instuteDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                instituteref.child(mAuth.getUid()).child("InstituteDetails").setValue(instuteDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        navController.popBackStack();
                        navController.navigate(R.id.homeTeacher);


                    }
                });

            }
        });

    }
}