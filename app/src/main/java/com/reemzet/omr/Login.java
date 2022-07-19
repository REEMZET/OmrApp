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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Models.InstuteDetails;
import com.reemzet.omr.Models.TestDetails;

import java.util.concurrent.TimeUnit;


public class Login extends Fragment {
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth mAuth;
    NavController navController;
    FirebaseDatabase database;
    DatabaseReference teacherref;
    String otpid,phone;
    Button submit;
    EditText etphone,etotp;
    ProgressDialog progressDialog;
    InstuteDetails instuteDetails;
    DatabaseReference instituteorgcoderef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_login, container, false);
        submit=view.findViewById(R.id.registerbtn);
        etphone=view.findViewById(R.id.etphone);
        etotp=view.findViewById(R.id.ettotp);
        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        teacherref=database.getReference("Teacherlist");
        instituteorgcoderef=database.getReference("Organisation");
        phone=getArguments().getString("phone");

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();


        if (!phone.equals("null")){
            etphone.setText(phone);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submit.getText().toString().equals("GET OTP")){
                    if (etphone.getText().length()<10){
                        etphone.setError("Invalid Phone");
                    }else {
                        showloding();
                        phone=etphone.getText().toString();
                        checkuserexit();
                    }
                }else {
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
                progressDialog.dismiss();
                etotp.setVisibility(View.VISIBLE);
                etphone.setEnabled(false);
                submit.setText("Signin my Account");
                progressDialog.dismiss();
                etotp.requestFocus();
                Toast.makeText(getActivity(),"Please enter Otp",Toast.LENGTH_SHORT).show();
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
                        onverified();
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
    public void checkuserexit(){
        instituteorgcoderef.orderByChild("teacherphone").startAt(phone).endAt(phone+ "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    otpsent(phone);
                }else {progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Don't have account Please register", Toast.LENGTH_SHORT).show();
                    navController.popBackStack();
                    navController.navigate(R.id.register);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void onverified(){
        teacherref.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                instuteDetails=snapshot.getValue(InstuteDetails.class);
                progressDialog.dismiss();
                navController.popBackStack();
                Bundle bundle=new Bundle();
                bundle.putString("orgcode",instuteDetails.getOrgcode());
                navController.navigate(R.id.homeTeacher,bundle);
                Toast.makeText(getActivity(), "Login Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}