package com.reemzet.omr;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.reemzet.omr.Adapter.TodaystestlistViewHolder;
import com.reemzet.omr.Models.InstuteDetails;
import com.reemzet.omr.Models.TestDetails;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;


public class HomeTeacher extends Fragment {


    RecyclerView todaystestrecycler;
    NavController navController;
    FirebaseDatabase database;
    DatabaseReference TestListref,requestlistref,joinedstudentlistref,instituteref;
    TestDetails testDetails;
    TextView todaysdate,tvcreatetest,tvnoofrequest,tvnoofjoinedstudent;
    ConstraintLayout consttestlist,constraintupdate,constraintrequestlist,constraintjoinedstudent,constraintnotification;
    DialogPlus dialog;
    ImageView calendar,clock,imageshare;
    int d,m,y,minute,hours;
    TextView tvtestdate,tvtesttime,tvnooftest;
    EditText etnoofques,etduration,etcorrectmarks,etincorrectmarks,ettestcode,ettestname;
    String noofques,testduration,correctmarks,incorrectmarks,testcode,testartstime,testdate,testname,orgcode;
    Button btnsubmit;
    ProgressDialog progressDialog;
    boolean fieldboolean;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;
    LinearLayout invite;
    FirebaseRecyclerAdapter<TestDetails, TodaystestlistViewHolder> adapter;


    NavigationView navigationView;
    TextView loginusername,loginuserphonno;
    LinearLayout profilenav,sharenav,logoutnav,updatenav,devnav,homenav;
    ImageView loginuserpic;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    TextView tveditprofile;
    CircularImageView circularImageView;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home_teacher, container, false);

        drawerLayout = getActivity().findViewById(R.id.drawer);
        toolbar = getActivity().findViewById(R.id.toolbar);
        navigationView = view.findViewById(R.id.nav_view);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        loginusername=headerView.findViewById(R.id.loginusername);
        loginuserphonno=headerView.findViewById(R.id.loginuserphone);
        loginuserpic = headerView.findViewById(R.id.loginuserpic);
        homenav=headerView.findViewById(R.id.ll_Home);
        profilenav=headerView.findViewById(R.id.ll_Profile);
        sharenav=headerView.findViewById(R.id.ll_Share);
        logoutnav=headerView.findViewById(R.id.ll_Logout);
        updatenav=headerView.findViewById(R.id.ll_update);
        devnav=headerView.findViewById(R.id.ll_developer);


        todaystestrecycler=view.findViewById(R.id.todaytestrecycler);
        todaysdate=view.findViewById(R.id.todaydate);
        consttestlist=view.findViewById(R.id.consttestlist);
        tvcreatetest=view.findViewById(R.id.createtest);
        tvnooftest=view.findViewById(R.id.tvnotests);
        tvnoofrequest=view.findViewById(R.id.tvnoofrequest);
        constraintrequestlist=view.findViewById(R.id.constraintrequestlist);
        constraintupdate=view.findViewById(R.id.constraintupdate);
        constraintjoinedstudent=view.findViewById(R.id.constraintjoinedstudent);
        constraintnotification=view.findViewById(R.id.constraintnotification);
        imageshare=view.findViewById(R.id.imageshare);
        invite=view.findViewById(R.id.linearinvite);
        tvnoofjoinedstudent=view.findViewById(R.id.tvnoofjoinedstudent);
        tveditprofile=view.findViewById(R.id.tveditprofile);
       circularImageView=view.findViewById(R.id.instituteprofileimg);
        todaystestrecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        navController = navHostFragment.getNavController();
        orgcode=getArguments().getString("orgcode");
        database=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        TestListref=database.getReference("institute").child(orgcode).child("TestList");
        requestlistref=database.getReference("institute").child(orgcode).child("BatchrequestList");
        joinedstudentlistref = database.getReference("institute").child(orgcode).child("StduentList");
        instituteref=database.getReference("institute").child(orgcode).child("InstituteDetails");
        setdatetohome();
        getdatafromserver();
        setnoofrequestlist();
        setnoofjoinedstudent();
        loadshareimg();
        loginusername.setText(getArguments().getString("teachername"));
        loginuserphonno.setText("Mob +91 "+getArguments().getString("phone"));
        setupnavigation();
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
        tveditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("orgcode",orgcode);
                navController.navigate(R.id.editInstituteDetails,bundle);
            }
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
        constraintnotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialog = DialogPlus.newDialog(getActivity())
                        .setContentHolder(new ViewHolder(R.layout.notificationview))
                        .setGravity(Gravity.CENTER)
                        .create();
                dialog.show();
                View myview = dialog.getHolderView();
                Button sendnotificationbtn=myview.findViewById(R.id.sendnotificationbtn);
                EditText nottitle=myview.findViewById(R.id.nottitle);
                EditText notmsg=myview.findViewById(R.id.notmsg);
                sendnotificationbtn.setOnClickListener(v1 -> {

                    if (nottitle.getText().toString().isEmpty()){
                        nottitle.setError("Empty");

                    }else if( notmsg.getText().toString().isEmpty()){
                        notmsg.setError("Empty");
                    }
                    else {
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/"+orgcode, nottitle.getText().toString(), notmsg.getText().toString(), getContext(), getActivity());
                        notificationsSender.SendNotifications();
                        Toast.makeText(getActivity(), "Notification sent", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
        invite.setOnClickListener(v -> {
            invite();
        });

        instituteref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                InstuteDetails instuteDetails=snapshot.getValue(InstuteDetails.class);
                Glide.with(getActivity())
                        .load(instuteDetails.getInstituteimage())
                        .centerCrop()
                        .placeholder(R.drawable.student)
                        .into(circularImageView);
                Glide.with(getActivity())
                        .load(instuteDetails.getInstituteimage())
                        .centerCrop()
                        .placeholder(R.drawable.student)
                        .into(loginuserpic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        homenav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        profilenav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("orgcode",orgcode);
                navController.navigate(R.id.editInstituteDetails,bundle);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        sharenav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invite();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        updatenav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdateChecker appUpdateChecker = new AppUpdateChecker(getActivity());
                appUpdateChecker.checkForUpdate(true);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        logoutnav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                navController.popBackStack();
                navController.navigate(R.id.welCome);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        devnav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.aboutDev);
                drawerLayout.closeDrawer(GravityCompat.START);
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

    public void setnoofjoinedstudent(){
        joinedstudentlistref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int i= (int) snapshot.getChildrenCount();
                    tvnoofjoinedstudent.setText(String.valueOf(i));
                }
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
        }else  if (etduration.getText().toString().isEmpty()) {
            etduration.setError("field cant be empty");
            fieldboolean = false;
        }else  if (etcorrectmarks.getText().toString().isEmpty()) {
            etcorrectmarks.setError("field cant be empty");
            fieldboolean = false;
        }else if (etincorrectmarks.getText().toString().isEmpty()) {
            etincorrectmarks.setError("field cant be empty");
            fieldboolean = false;
        }else if (ettestcode.getText().toString().isEmpty()) {
            ettestcode .setError("field cant be empty");
            fieldboolean = false;
        }else if (ettestname.getText().toString().isEmpty()) {
            ettestname .setError("field cant be empty");
            fieldboolean = false;
        }else if (tvtesttime.getText().toString().equals("Test Time")){
            tvtesttime.setError("Can't be empty");
            fieldboolean=false;
        }else if (tvtestdate.getText().toString().equals("Test Date")){
            tvtestdate.setError("Can't be empty");
            fieldboolean=false;
        }else
        {
            etnoofques.setError(null);
            noofques= etnoofques.getText().toString();
            etduration.setError(null);
            testduration = etduration.getText().toString();
            etcorrectmarks.setError(null);
            correctmarks=  etcorrectmarks.getText().toString();
            etincorrectmarks.setError(null);
            incorrectmarks = etincorrectmarks.getText().toString();
            ettestcode .setError(null);
            testcode =ettestcode.getText().toString();
            ettestname .setError(null);
            testname =ettestname.getText().toString();
            tvtesttime.setError(null);
            testartstime=tvtesttime.getText().toString();
            tvtestdate.setError(null);
            testdate=tvtestdate.getText().toString();
            fieldboolean = true;
        }
     return fieldboolean;
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
    public void loadshareimg() {
        imageshare.setImageDrawable(getResources().getDrawable(R.drawable.applogo));
        bitmapDrawable = (BitmapDrawable) imageshare.getDrawable();
        bitmap = bitmapDrawable.getBitmap();

    }


    public void invite() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        Uri bmpuri;
        int happy=0x1F929;
        int hand=0x1F449;
        String text = "Hi download this app for online digital omr test"+getEmojiByUnicode(hand)+"https://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + "&hl=it";
        bmpuri = saveImage(bitmap, getActivity());
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_STREAM, bmpuri);
        share.putExtra(Intent.EXTRA_SUBJECT, "share App");
        share.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(share, "Share to"));
    }

    private static Uri saveImage(Bitmap image, Context context) {

        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_images.jpg");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),
                    "com.reemzet.omr" + ".provider", file);

        } catch (IOException e) {

        }
        return uri;
    }
    public void setupnavigation(){
        NavigationUI.setupWithNavController(navigationView, navController);
        toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);
    }
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}