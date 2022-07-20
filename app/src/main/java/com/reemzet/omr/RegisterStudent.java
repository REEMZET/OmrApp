package com.reemzet.omr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.reemzet.omr.Models.StudentsModel;

import java.util.concurrent.TimeUnit;


public class RegisterStudent extends Fragment {

EditText etstudenname,etstudentphone,etstudentemail,etpreparation,etotp;
String studentname,studentphone,studentemail,preparation,otpid;
Button studentregisterbtn;
    StudentsModel students;
    boolean fieldboolean;
    FirebaseAuth mAuth;
    NavController navController;
    FirebaseDatabase database;
    DatabaseReference studentref,organisationlistref;
    ProgressDialog progressDialog;
    private String selectedDistrict, selectedState;                 //vars to hold the values of selected State and District
    private TextView tvStateSpinner, tvDistrictSpinner;             //declaring TextView to show the errors
    private Spinner stateSpinner, districtSpinner;                  //Spinners
    private ArrayAdapter<CharSequence> stateAdapter, districtAdapter;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_register_student, container, false);
        etstudenname=view.findViewById(R.id.etstudentname);
        etstudentphone=view.findViewById(R.id.etstudentphone);
        etstudentemail=view.findViewById(R.id.etstudentemail);
        etpreparation=view.findViewById(R.id.etpreparation);
        etotp=view.findViewById(R.id.etstudentotp);
        studentregisterbtn=view.findViewById(R.id.studentregisterbtn);


        stateSpinner = view.findViewById(R.id.spinner_indian_states);
        districtSpinner = view.findViewById(R.id.spinner_indian_districts);
        tvDistrictSpinner=view.findViewById(R.id.textView_indian_districts);
        tvStateSpinner=view.findViewById(R.id.textView_indian_states);
        stateSpinner = view.findViewById(R.id.spinner_indian_states);

        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
          studentref=database.getReference("students");
       organisationlistref=database.getReference("Organisation");

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        setstatecity();

        studentregisterbtn.setOnClickListener(v -> {
            if (studentregisterbtn.getText().toString().equals("GET OTP")){
                if (fieldcheck()){
                 showloding();
                 teachercheck();
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

        etpreparation.setEnabled(false);

    }

    private boolean fieldcheck() {
        if (etstudenname.getText().toString().isEmpty()){
            etstudenname.setError("field can't be empty");
            etstudenname.requestFocus();
            fieldboolean = false;
        }else if (etstudentemail.getText().toString().isEmpty()){
            etstudentemail.setError("can't be empty");
            etstudentemail.requestFocus();
            fieldboolean = false;
        }else if (etpreparation.getText().toString().isEmpty()){
            etpreparation.setError("field can't empty");
            fieldboolean = false;
        }else if (etstudentphone.getText().toString().length()<10){
            etstudentphone.setError("invalid phone");
            etstudentphone.requestFocus();
            fieldboolean = false;
        }else if(selectedState.equals("Select Your State") ){
            tvStateSpinner.setError("please select");
            tvDistrictSpinner.requestFocus();
            fieldboolean=false;
        }else if (selectedDistrict.equals("Select Your District")){
            tvDistrictSpinner.setError("please select");
            tvDistrictSpinner.requestFocus();
            fieldboolean=false;
        }
        else{
            studentname = etstudenname.getText().toString();
            studentemail = etstudentemail.getText().toString();
            preparation = etpreparation.getText().toString();
            studentphone = etstudentphone.getText().toString();
            fieldboolean=true;
        }
        return fieldboolean;
    }
    private void teachercheck(){
        organisationlistref.orderByChild("teacherphone").startAt(studentphone).endAt(studentphone+ "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "you have teacher account", Toast.LENGTH_SHORT).show();
                    navController.popBackStack();
                    Bundle bundle=new Bundle();
                    bundle.putString("phone",studentphone);
                    navController.navigate(R.id.login,bundle);
                }else {
                    otpsent(studentphone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    studentref.orderByChild("studentuid").startAt(mAuth.getUid()).endAt(mAuth+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    progressDialog.dismiss();
                    navController.popBackStack();
                    navController.navigate(R.id.homeStudent);
                } else {
                    StudentsModel studentsModel=new StudentsModel(studentname,studentphone,selectedDistrict,selectedState,studentemail,preparation,"Nobatch",mAuth.getUid(),"NoRequestedBatch","Noimage","0","0","0");
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
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setstatecity(){
        stateAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.array_indian_states, R.layout.spinner_layout);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()  {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Define City Spinner but we will populate the options through the selected state
                selectedState = stateSpinner.getSelectedItem().toString();      //Obtain the selected State

                int parentID = parent.getId();
                if (parentID == R.id.spinner_indian_states){
                    switch (selectedState){
                        case "Select Your State": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_default_districts, R.layout.spinner_layout);
                            break;
                        case "Andhra Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_andhra_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Arunachal Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_arunachal_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Assam": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_assam_districts, R.layout.spinner_layout);
                            break;
                        case "Bihar": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_bihar_districts, R.layout.spinner_layout);
                            break;
                        case "Chhattisgarh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_chhattisgarh_districts, R.layout.spinner_layout);
                            break;
                        case "Goa": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_goa_districts, R.layout.spinner_layout);
                            break;
                        case "Gujarat": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_gujarat_districts, R.layout.spinner_layout);
                            break;
                        case "Haryana": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_haryana_districts, R.layout.spinner_layout);
                            break;
                        case "Himachal Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_himachal_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Jharkhand": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_jharkhand_districts, R.layout.spinner_layout);
                            break;
                        case "Karnataka": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_karnataka_districts, R.layout.spinner_layout);
                            break;
                        case "Kerala": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_kerala_districts, R.layout.spinner_layout);
                            break;
                        case "Madhya Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_madhya_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Maharashtra": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_maharashtra_districts, R.layout.spinner_layout);
                            break;
                        case "Manipur": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_manipur_districts, R.layout.spinner_layout);
                            break;
                        case "Meghalaya": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_meghalaya_districts, R.layout.spinner_layout);
                            break;
                        case "Mizoram": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_mizoram_districts, R.layout.spinner_layout);
                            break;
                        case "Nagaland": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_nagaland_districts, R.layout.spinner_layout);
                            break;
                        case "Odisha": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_odisha_districts, R.layout.spinner_layout);
                            break;
                        case "Punjab": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_punjab_districts, R.layout.spinner_layout);
                            break;
                        case "Rajasthan": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_rajasthan_districts, R.layout.spinner_layout);
                            break;
                        case "Sikkim": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_sikkim_districts, R.layout.spinner_layout);
                            break;
                        case "Tamil Nadu": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_tamil_nadu_districts, R.layout.spinner_layout);
                            break;
                        case "Telangana": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_telangana_districts, R.layout.spinner_layout);
                            break;
                        case "Tripura": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_tripura_districts, R.layout.spinner_layout);
                            break;
                        case "Uttar Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_uttar_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Uttarakhand": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_uttarakhand_districts, R.layout.spinner_layout);
                            break;
                        case "West Bengal": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_west_bengal_districts, R.layout.spinner_layout);
                            break;
                        case "Andaman and Nicobar Islands": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_andaman_nicobar_districts, R.layout.spinner_layout);
                            break;
                        case "Chandigarh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_chandigarh_districts, R.layout.spinner_layout);
                            break;
                        case "Dadra and Nagar Haveli": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_dadra_nagar_haveli_districts, R.layout.spinner_layout);
                            break;
                        case "Daman and Diu": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_daman_diu_districts, R.layout.spinner_layout);
                            break;
                        case "Delhi": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_delhi_districts, R.layout.spinner_layout);
                            break;
                        case "Jammu and Kashmir": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_jammu_kashmir_districts, R.layout.spinner_layout);
                            break;
                        case "Lakshadweep": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_lakshadweep_districts, R.layout.spinner_layout);
                            break;
                        case "Ladakh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_ladakh_districts, R.layout.spinner_layout);
                            break;
                        case "Puducherry": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_puducherry_districts, R.layout.spinner_layout);
                            break;
                        default:  break;
                    }
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     // Specify the layout to use when the list of choices appears
                    districtSpinner.setAdapter(districtAdapter);        //Populate the list of Districts in respect of the State selected

                    //To obtain the selected District from the spinner
                    districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedDistrict = districtSpinner.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}