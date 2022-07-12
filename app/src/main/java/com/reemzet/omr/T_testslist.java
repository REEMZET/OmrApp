package com.reemzet.omr;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.nex3z.togglebuttongroup.button.CircularToggle;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.reemzet.omr.Adapter.TeacherTestListViewHolder;
import com.reemzet.omr.Adapter.TodaystestlistViewHolder;
import com.reemzet.omr.Models.OmrModel;
import com.reemzet.omr.Models.TestDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class T_testslist extends Fragment {
    RecyclerView testlistrecycler;
    NavController navController;
    FirebaseDatabase database;
    DatabaseReference TestListref;
    FirebaseAuth mAuth;
    TestDetails testDetails;
    DialogPlus dialog;
    ImageView calendar,clock;
    int d,m,y,minute,hours;
    TextView etnoofques,tvtestdate,tvtesttime;
    EditText etduration,etcorrectmarks,etincorrectmarks,ettestcode,ettestname;
    String noofques,testduration,correctmarks,incorrectmarks,testcode,testartstime,testdate,testname;
    FirebaseRecyclerAdapter<TestDetails, TeacherTestListViewHolder> adapter;
    Button btnsubmit;
    ProgressDialog progressDialog;
    boolean fieldboolean;
    ArrayList<String> selectedanswer;
    ArrayList<OmrModel> list;
    String testkey;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =inflater.inflate(R.layout.fragment_t_testslist, container, false);
        testlistrecycler=view.findViewById(R.id.testlistrecyclerview);

        testlistrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        TestListref=database.getReference("institute").child(mAuth.getUid()).child("TestList");

        getdatafromserver();
        selectedanswer = new ArrayList<>();
        list=new ArrayList<>();


        return view;
    }
    public void getdatafromserver(){
        TestListref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                testDetails=snapshot.getValue(TestDetails.class);
                testkey=snapshot.getKey();
                setData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setData(){

        FirebaseRecyclerOptions<TestDetails> options =
                new FirebaseRecyclerOptions.Builder<TestDetails>()
                        .setQuery(TestListref, TestDetails.class)
                        .build();

        adapter=new FirebaseRecyclerAdapter<TestDetails,TeacherTestListViewHolder>(options){

            @NonNull
            @Override
            public TeacherTestListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View rview = LayoutInflater.from(parent.getContext()).inflate(R.layout.teachertestlistlayout, parent, false);
                return new TeacherTestListViewHolder(rview);
            }

            @Override
            protected void onBindViewHolder(@NonNull TeacherTestListViewHolder holder, int position, @NonNull TestDetails model) {
                holder.testname.setText(model.getTestname());
                holder.totalquestion.setText("No.of Ques-"+model.getQuestionno());
                holder.testtime.setText(model.getStarttime());
                int totalmarks=Integer.parseInt(model.getCorrectmarks())*Integer.parseInt(model.getQuestionno());
                holder.tvtotalmarks.setText("Marks-"+totalmarks);
                totalmarks=0;
                holder.duration.setText("Duration:-"+model.getTesttime());
                holder.date.setText("Date:-"+model.getTestdate());
                holder.tteststatus.setText(model.getStatus());
                holder.editnow.setOnClickListener(v -> {
                    dialog = DialogPlus.newDialog(getContext())
                            .setContentHolder(new ViewHolder(R.layout.testeditlayout))
                            .setGravity(Gravity.CENTER)
                            .create();
                             dialog.show();
                             initdialogview();
                             feeddatainview(model);
                             Calendar c=Calendar.getInstance();
       calendar.setOnClickListener(v1 -> {
                        y=c.get(Calendar.YEAR);
                        m=c.get(Calendar.MONTH);
                        d=c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog dp=new DatePickerDialog(getActivity(), (view1, year, month, dayOfMonth) -> {
                            month++;
                           tvtestdate.setText(dayOfMonth+"/"+month+"/"+year);
                        },y,m,d);
                        dp.setTitle("Select Meeting Date");
                        dp.show();
                    });
        clock.setOnClickListener(v13 -> {
                        minute=c.get(Calendar.MINUTE);
                        hours=c.get(Calendar.HOUR_OF_DAY);

                        TimePickerDialog timePickerDialog=new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                c.set(Calendar.MINUTE,minute);
                                c.setTimeZone(TimeZone.getDefault());
                                SimpleDateFormat format=new SimpleDateFormat("h:mm a");
                                String time=format.format(c.getTime());
                                tvtesttime.setText(time);
                            }
                        },hours,minute,false);
                        timePickerDialog.setTitle("Select meeting time");
                        timePickerDialog.show();
                    });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showloding();
                dialogdatavalidation();
                if (fieldboolean){
                    HashMap<String, Object> testdetails = new HashMap<>();
                   testdetails.put("testname",testname);
                   testdetails.put("correctmarks",correctmarks);
                   testdetails.put("testcode",testcode);
                   testdetails.put("testdate",testdate);
                   testdetails.put("testtime",testduration);
                   testdetails.put("starttime",testartstime);
                   testdetails.put("wrongmarks",incorrectmarks);

                     TestListref.child(getRef(holder.getAdapterPosition()).getKey()).updateChildren((testdetails)).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             dialog.dismiss();
                            progressDialog.dismiss(); }});
                }else {
                    progressDialog.dismiss();
                } }}); });
                holder.tvsetanswer.setOnClickListener(v -> {
                    Bundle bundle=new Bundle();
                    bundle.putString("testkey",  String.valueOf(TestListref.child(getRef(holder.getAdapterPosition()).getKey())));
                   navController.navigate(R.id.setAnswer,bundle);
                });
                holder.tvdeletebtn.setOnClickListener(v -> {

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Alert!")
                            .setMessage("Are you Sure to Delete?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) ->
                                    TestListref.child(getRef(holder.getAdapterPosition()).getKey()).removeValue()
                                            .addOnCompleteListener(task ->{

                                            }
                                           ))
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                });

            }
        };

        testlistrecycler.setAdapter(adapter);
        adapter.startListening();
    }
    public void initdialogview(){
        View myview = dialog.getHolderView();
        etnoofques=myview.findViewById(R.id.etnofques);
        etduration=myview.findViewById(R.id.etduration);
        etcorrectmarks=myview.findViewById(R.id.etcorrectmarks);
        etincorrectmarks=myview.findViewById(R.id.etincorrectmarks);
        ettestcode=myview.findViewById(R.id.ettestcode);
        ettestname=myview.findViewById(R.id.ettestname);
        calendar=myview.findViewById(R.id.calendar);
        clock=myview.findViewById(R.id.clock);
        tvtestdate=myview.findViewById(R.id.tvtestdate);
        tvtesttime=myview.findViewById(R.id.tvtesttime);
        btnsubmit=myview.findViewById(R.id.btnsubmit);

    }
    public boolean dialogdatavalidation(){
        if (etnoofques.getText().toString().isEmpty()) {
            etnoofques.setError("field cant be empty");
            fieldboolean = false;
        }else {
            etnoofques.setError(null);
             noofques= etnoofques.getText().toString();
            fieldboolean = true;
        }
        if (etduration.getText().toString().isEmpty()) {
            etduration.setError("field cant be empty");
            fieldboolean = false;
        }else {
            etduration.setError(null);
            testduration = etduration.getText().toString();
            fieldboolean = true;
        }
        if (etcorrectmarks.getText().toString().isEmpty()) {
            etcorrectmarks.setError("field cant be empty");
            fieldboolean = false;
        }else {
            etcorrectmarks.setError(null);
             correctmarks=  etcorrectmarks.getText().toString();
            fieldboolean = true;
        }
        if (etincorrectmarks.getText().toString().isEmpty()) {
            etincorrectmarks.setError("field cant be empty");
            fieldboolean = false;
        }else {
            etincorrectmarks.setError(null);
            incorrectmarks = etincorrectmarks.getText().toString();
            fieldboolean = true;
        }
        if (ettestcode.getText().toString().isEmpty()) {
            ettestcode .setError("field cant be empty");
            fieldboolean = false;
        }else {
            ettestcode .setError(null);
            testcode =ettestcode.getText().toString();
            fieldboolean = true;
        }
        if (ettestname.getText().toString().isEmpty()) {
            ettestname .setError("field cant be empty");
            fieldboolean = false;
        }else {
            ettestname .setError(null);
            testname =ettestname.getText().toString();
            fieldboolean = true;
        }

        if (tvtesttime.getText().toString().equals("Test Time")){
            tvtesttime.setError("Can't be empty");
            fieldboolean=false;
        }else{
            tvtesttime.setError(null);
            testartstime=tvtesttime.getText().toString();
            fieldboolean=true;
        }
        if (tvtestdate.getText().toString().equals("Test Date")){
            tvtestdate.setError("Can't be empty");
            fieldboolean=false;
        }else {
            tvtestdate.setError(null);
            testdate=tvtestdate.getText().toString();
            fieldboolean=true;
        }
        return fieldboolean;
    }

    public void feeddatainview(TestDetails model){
        etnoofques.setText(model.getQuestionno());
        etduration.setText(model.getTesttime());
        etcorrectmarks.setText(model.getCorrectmarks());
        etincorrectmarks.setText(model.getWrongmarks());
        ettestcode.setText(model.getTestcode());
        ettestname.setText(model.getTestname());
        tvtestdate.setText(model.getTestdate());
        tvtesttime.setText(model.getStarttime());
    }
    public void showloding() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialoprogress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }



}