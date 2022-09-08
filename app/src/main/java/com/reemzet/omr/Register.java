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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.reemzet.omr.Models.InstuteDetails;
import com.reemzet.omr.Models.SliderMOdel;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class Register extends Fragment {
        TextView tvsignin;
        ProgressDialog progressDialog;
        Button registerbtn;
        EditText etteachername,etinstitutename,etorgcode,etemail,etphone,etotp;
        String teachername,institutename,orgcode,teacheremail,teacherphone,otpid;
        boolean fieldboolean;
        FirebaseAuth mAuth;
        NavController navController;
        FirebaseDatabase database;
        DatabaseReference teacherref,instituteref,instituteorgcoderef,studentref,posterref;
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
     private String selectedDistrict, selectedState;                 //vars to hold the values of selected State and District
    private TextView tvStateSpinner, tvDistrictSpinner;             //declaring TextView to show the errors
    private Spinner stateSpinner, districtSpinner;                  //Spinners
    private ArrayAdapter<CharSequence> stateAdapter, districtAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_register, container, false);
        registerbtn=view.findViewById(R.id.registerbtn);
        etteachername=view.findViewById(R.id.etteachername);
        tvsignin=view.findViewById(R.id.signintv);
        etinstitutename=view.findViewById(R.id.etinstitutename);
        etorgcode=view.findViewById(R.id.etuniquecode);
        etemail=view.findViewById(R.id.etteacheremail);
        etphone=view.findViewById(R.id.etteacherphone);
        etotp=view.findViewById(R.id.ettotp);
        stateSpinner = view.findViewById(R.id.spinner_indian_states);
        districtSpinner = view.findViewById(R.id.spinner_indian_districts);
        tvDistrictSpinner=view.findViewById(R.id.textView_indian_districts);
        tvStateSpinner=view.findViewById(R.id.textView_indian_states);
        stateSpinner = view.findViewById(R.id.spinner_indian_states);
        //Set the adapter to the spinner to populate the State Spinner


        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        teacherref=database.getReference("Teacherlist");
        instituteref=database.getReference("institute");
        instituteorgcoderef=database.getReference("Organisation");
        studentref=database.getReference("students");

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        setstatecity();
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerbtn.getText().toString().equals("Get Otp")){
                   if (fieldcheck()){
                       showloding();
                       checkorgexit();
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

        tvsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("phone","null");
                navController.popBackStack();
                navController.navigate(R.id.login,bundle);
            }
        });





    return view;
    }
    private void disableEdittext(){
        etteachername.setEnabled(false);
        etinstitutename.setEnabled(false);
        etorgcode.setEnabled(false);

        etemail.setEnabled(false);
        etphone.setEnabled(false);
    }
    private boolean fieldcheck() {

        if (etteachername.getText().toString().isEmpty()){
            etteachername.setError("field cant be empty");
            etteachername.requestFocus();
            fieldboolean = false;
        }else if (etinstitutename.getText().toString().isEmpty()){
            etinstitutename.setError("can't be empty");
            etinstitutename.requestFocus();
            fieldboolean = false;
        }else if (etorgcode.getText().toString().length()<5){
            etorgcode.setError("5 letter unique code");
            etorgcode.requestFocus();
            fieldboolean = false;
        }else if (etemail.getText().toString().isEmpty()){
            etemail.setError("can't be empty");
            etemail.requestFocus();
            fieldboolean = false;
        }else if (etphone.getText().toString().length()<10){
            etphone.setError("invalid phone");
            etphone.requestFocus();
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
        else {
            Toast.makeText(getActivity(),"Please Wait",Toast.LENGTH_LONG).show();
            teachername = etteachername.getText().toString();
            teacheremail = etemail.getText().toString();
            institutename = etinstitutename.getText().toString();
            orgcode = etorgcode.getText().toString();
            teacherphone = etphone.getText().toString();
            fieldboolean=true;
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
                        checkstudentuid();
                    } else {
                        // Sign in failed, display a message and update the UI
                        Toast.makeText(getActivity(), "failed to verify", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void showloding() {
        if(getActivity() != null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.show();
            progressDialog.setContentView(R.layout.dialoprogress);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }
    public void sentdatatodatabase(){
        InstuteDetails instuteDetails=new InstuteDetails(teachername,institutename,orgcode,selectedDistrict,selectedState,teacheremail,teacherphone,"TEACHER",mAuth.getUid(),"noimage");
        teacherref.child(mAuth.getUid()).setValue(instuteDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                instituteref.child(orgcode).child("InstituteDetails").setValue(instuteDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        posterref=database.getReference("institute").child(orgcode).child("slider");
                        SliderMOdel slidermodel=new SliderMOdel();
                        slidermodel.setTitle(institutename);
                        slidermodel.setMessage("Thank You For Joining Our Class");
                        slidermodel.setBgcard("#F9A02F");
                        slidermodel.setTitlecolor("#FFFFFF");
                        slidermodel.setMessagecolor("#000000");

                        SliderMOdel slider2=new SliderMOdel();
                       slider2.setTitle("Powered By Reemzet");
                        slider2.setMessage("For Any Technical help please Contact us Mob:- 9525581574");
                        slider2.setBgcard("#C104FD");
                        slider2.setTitlecolor("#FFFFFF");
                        slider2.setMessagecolor("#F9FD01");
                        posterref.push().setValue(slidermodel);
                        posterref.push().setValue(slider2);
                        instituteorgcoderef.push().setValue(instuteDetails);
                        progressDialog.dismiss();
                        navController.popBackStack();
                        Bundle bundle=new Bundle();
                        bundle.putString("orgcode",orgcode);
                        FirebaseMessaging.getInstance().subscribeToTopic(orgcode);
                        FirebaseMessaging.getInstance().subscribeToTopic("admin"+orgcode);
                        FirebaseMessaging.getInstance().subscribeToTopic("institute");
                        FirebaseMessaging.getInstance().subscribeToTopic("all");
                        navController.navigate(R.id.homeTeacher,bundle);
                    }
                });
            }
        });


    }
    public void checkorgexit(){
        instituteorgcoderef.orderByChild("teacherphone").startAt(teacherphone).endAt(teacherphone+ "\uf8ff").addListenerForSingleValueEvent
                (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    progressDialog.dismiss();
                    Bundle bundle=new Bundle();
                    bundle.putString("phone",teacherphone);
                    Toast.makeText(getActivity(), "You already have account please login", Toast.LENGTH_SHORT).show();
                    navController.popBackStack();
                    navController.navigate(R.id.login,bundle);
                }else {
                    instituteorgcoderef.orderByChild("orgcode").startAt(orgcode).endAt(orgcode+ "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                progressDialog.dismiss();
                                etorgcode.setError("Already taken please try another");
                                etorgcode.requestFocus();
                            }else{
                                otpsent(teacherphone);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

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
          //Finds a view that was identified by the android:id attribute in xml
        //Populate ArrayAdapter using string array and a spinner layout that we will define
        stateAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.array_indian_states, R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appear
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);            //Set the adapter to the spinner to populate the State Spinner
        //When any item of the stateSpinner uis selected
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
    public void checkstudentuid(){
        studentref.orderByChild("studentuid").startAt(mAuth.getUid()).endAt(mAuth.getUid()+ "\uf8ff").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            navController.popBackStack();
                            navController.navigate(R.id.welCome);
                        }else {
                            sentdatatodatabase();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }
}