package com.reemzet.omr;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.reemzet.omr.Adapter.TeacherTestListViewHolder;
import com.reemzet.omr.Adapter.TodaystestlistViewHolder;
import com.reemzet.omr.Models.InstuteDetails;
import com.reemzet.omr.Models.TestDetails;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class HomeTeacher extends Fragment {


    RecyclerView todaystestrecycler;
    NavController navController;
    FirebaseDatabase database;
    DatabaseReference TestListref,requestlistref;
    TestDetails testDetails;
    TextView todaysdate,tvcreatetest,tvnoofrequest;
    ConstraintLayout consttestlist,constraintupdate,constraintrequestlist,constraintjoinedstudent;
    DialogPlus dialog;
    ImageView calendar,clock;
    int d,m,y,minute,hours;
    TextView tvtestdate,tvtesttime,tvnooftest;
    EditText etnoofques,etduration,etcorrectmarks,etincorrectmarks,ettestcode,ettestname;
    String noofques,testduration,correctmarks,incorrectmarks,testcode,testartstime,testdate,testname,orgcode;
    Button btnsubmit;
    ProgressDialog progressDialog;
    boolean fieldboolean;


    FirebaseRecyclerAdapter<TestDetails, TodaystestlistViewHolder> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home_teacher, container, false);
        todaystestrecycler=view.findViewById(R.id.todaytestrecycler);
        todaysdate=view.findViewById(R.id.todaydate);
        consttestlist=view.findViewById(R.id.consttestlist);
        tvcreatetest=view.findViewById(R.id.createtest);
        tvnooftest=view.findViewById(R.id.tvnotests);
        tvnoofrequest=view.findViewById(R.id.tvnoofrequest);
        constraintrequestlist=view.findViewById(R.id.constraintrequestlist);
        constraintupdate=view.findViewById(R.id.constraintupdate);
        constraintjoinedstudent=view.findViewById(R.id.constraintjoinedstudent);

        todaystestrecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        orgcode=getArguments().getString("orgcode");
        database=FirebaseDatabase.getInstance();
        TestListref=database.getReference("institute").child(orgcode).child("TestList");
        requestlistref=database.getReference("institute").child(orgcode).child("BatchrequestList");
        setdatetohome();
        getdatafromserver();
        setnoofrequestlist();
        consttestlist.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putString("orgcode",orgcode);
            navController.navigate(R.id.t_testslist,bundle);
        });
        tvcreatetest.setOnClickListener(v -> {
            dialog = DialogPlus.newDialog(getContext())
                    .setContentHolder(new ViewHolder(R.layout.createtestlayout))
                    .setGravity(Gravity.CENTER)
                    .create();
            dialog.show();
            initdialogview();

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
                        c.set(Calendar.SECOND,00);
                        c.setTimeZone(TimeZone.getDefault());
                        SimpleDateFormat format=new SimpleDateFormat("h:mm:ss a");
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
                        String testid=TestListref.push().getKey();
                        TestDetails testDetails =new TestDetails(testname,noofques,testduration,correctmarks,incorrectmarks,testartstime,testcode,testdate,"Answer not Set",testid,"notpub");
                        TestListref.child(testid).setValue(testDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                dialog.dismiss();
                                progressDialog.dismiss();
                            }
                        });
                    }else {
                        progressDialog.dismiss();
                    }
                }
            });
        });


        constraintrequestlist.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putString("orgcode",orgcode);
            navController.navigate(R.id.requestList,bundle);
        });
        constraintupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdateChecker appUpdateChecker = new AppUpdateChecker(getActivity());
                appUpdateChecker.checkForUpdate(true);
            }
        });
        constraintjoinedstudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("orgcode",orgcode);
                navController.navigate(R.id.joinedStudent,bundle);
            }
        });

        return view;

    }
    public void getdatafromserver(){
        TestListref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    testDetails=snapshot.getValue(TestDetails.class);
                    setData();
                    int i= (int) snapshot.getChildrenCount();
                    tvnooftest.setText(String.valueOf(i));
                }else tvnooftest.setText("0");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void setData(){
        String date = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(new Date());
        Query query =TestListref.orderByChild("testdate").startAt(date).endAt(date+ "\uf8ff");
        FirebaseRecyclerOptions<TestDetails> options =
                new FirebaseRecyclerOptions.Builder<TestDetails>()
                        .setQuery(query, TestDetails.class)
                        .build();

        adapter=new FirebaseRecyclerAdapter<TestDetails,TodaystestlistViewHolder>(options){

            @NonNull
            @Override
            public TodaystestlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View rview = LayoutInflater.from(parent.getContext()).inflate(R.layout.todaytestlistlayout, parent, false);
                return new TodaystestlistViewHolder(rview);
            }

            @Override
            protected void onBindViewHolder(@NonNull TodaystestlistViewHolder holder, int position, @NonNull TestDetails model) {
                    holder.testname.setText(model.getTestname());
                    holder.totalquestion.setText("No.of Ques-"+model.getQuestionno());
                    holder.testtime.setText(model.getStarttime());
                    int totalmarks=Integer.parseInt(model.getCorrectmarks())*Integer.parseInt(model.getQuestionno());
                    holder.tvtotalmarks.setText("Marks-"+totalmarks);
                    totalmarks=0;
                    holder.duration.setText("  Duration\n   "+model.getTesttime()+"mins");
                    holder.date.setText("Date\n"+model.getTestdate());
                    holder.teststatus.setText(model.getStatus());

                    holder.editnow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle=new Bundle();
                            bundle.putString("testkey",String.valueOf(TestListref.child(getRef(holder.getAdapterPosition()).getKey())));
                            navController.navigate(R.id.setAnswer,bundle);
                        }
                    });
                    if (holder.teststatus.getText().equals("Answer not Set")){
                        holder.teststatus.setTextColor(Color.RED);
                        holder.editnow.setEnabled(true);
                        holder.editnow.setText("Set answer");
                    }else if(holder.teststatus.getText().equals("completed")){
                        holder.editnow.setEnabled(false);
                    }else {
                        holder.editnow.setEnabled(true);
                        holder.editnow.setText("edit answer");
                    }

            }
        };
        todaystestrecycler.setAdapter(adapter);
        adapter.startListening();
    }
    public void initdialogview(){
        View myview = dialog.getHolderView();
        etnoofques=myview.findViewById(R.id.etnofquestion);
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

    public void showloding() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialoprogress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    public void setdatetohome(){
        Date cd = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(cd);
        todaysdate.setText(formattedDate);
    }

    public void setnoofrequestlist(){
        requestlistref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int i= (int) snapshot.getChildrenCount();
                    tvnoofrequest.setText(String.valueOf(i));
                }else tvnoofrequest.setText("0");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}