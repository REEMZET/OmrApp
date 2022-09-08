package com.reemzet.omr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.reemzet.omr.Adapter.SliderAdapter;
import com.reemzet.omr.Adapter.TodaystestlistViewHolder;
import com.reemzet.omr.Models.SliderMOdel;
import com.reemzet.omr.Models.StudentsModel;
import com.reemzet.omr.Models.TestDetails;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class HomeStudent extends Fragment {
    public static Context context;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    DatabaseReference posterref, TestListref, studentref;
    ArrayList<SliderMOdel> posterlist;
    ViewPager2 viewPager2;
    Handler sliderhandler = new Handler();
    String batch, studentcity;
    NavController navController;
    RecyclerView recyclerView;
    ConstraintLayout contraintestlist, constraintbatch, constraintreport, constraintupdate;
    TestDetails testDetails;
    TextView tvnotests;
    DialogPlus dialog;
    ProgressDialog progressDialog;
    LinearLayout nobatchlayout;
    Button btnfindbatch;
    ScrollView homescroll;
    StudentsModel studentsModel;
    TextView tvinstruct, todaysdate;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;
    ImageView imageshare2;
    LinearLayout invite;
    FirebaseRecyclerAdapter<TestDetails, TodaystestlistViewHolder> adapter;


    NavigationView navigationView;
    TextView loginusername, loginuserphonno;
    LinearLayout profilenav,sharenav,logoutnav,updatenav,devnav,homenav;
    ImageView loginuserpic;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    TextView tvprofile;
    CircularImageView circularImageView;
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_student, container, false);
        drawerLayout = getActivity().findViewById(R.id.drawer);
        toolbar = getActivity().findViewById(R.id.toolbar);
        navigationView = view.findViewById(R.id.nav_view);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        loginusername = headerView.findViewById(R.id.loginusername);
        loginuserphonno = headerView.findViewById(R.id.loginuserphone);
        loginuserpic = headerView.findViewById(R.id.loginuserpic);
        homenav=headerView.findViewById(R.id.ll_Home);
        profilenav=headerView.findViewById(R.id.ll_Profile);
        sharenav=headerView.findViewById(R.id.ll_Share);
        logoutnav=headerView.findViewById(R.id.ll_Logout);
        updatenav=headerView.findViewById(R.id.ll_update);
        devnav=headerView.findViewById(R.id.ll_developer);


        navController = navHostFragment.getNavController();
        contraintestlist = view.findViewById(R.id.constrainttestlist);
        constraintbatch = view.findViewById(R.id.constraintbatchlist);
        constraintreport = view.findViewById(R.id.constraintreport);
        constraintupdate = view.findViewById(R.id.constraintupdate);
        recyclerView = view.findViewById(R.id.todaystudenttestrecycler);
        nobatchlayout = view.findViewById(R.id.nobtchlayout);
        invite = view.findViewById(R.id.invite);
        imageshare2 = view.findViewById(R.id.imageshare2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        tvnotests = view.findViewById(R.id.tvnotests);
        btnfindbatch = view.findViewById(R.id.btnfindbatch);
        homescroll = view.findViewById(R.id.homescroll);
        viewPager2 = view.findViewById(R.id.viewpager2);
        todaysdate = view.findViewById(R.id.todaydate);
        tvprofile = view.findViewById(R.id.tvprofile);
        circularImageView = view.findViewById(R.id.profileimage);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        studentref = database.getReference("students");
        checkbatch();
        setdatetohome();
        loadshareimg();

        posterlist = new ArrayList<>();

        progressDialog = new ProgressDialog(getActivity());
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        contraintestlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("orgcode", batch);
                navController.navigate(R.id.studentList, bundle);
            }
        });

        constraintbatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("city", studentcity);
                navController.navigate(R.id.requestBatch, bundle);
            }
        });

        btnfindbatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("city", studentcity);
                navController.navigate(R.id.requestBatch, bundle);
            }
        });
        constraintreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("orgcode", batch);
                navController.navigate(R.id.report, bundle);
            }
        });
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invite();
            }
        });
        constraintupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdateChecker appUpdateChecker = new AppUpdateChecker(getActivity());
                appUpdateChecker.checkForUpdate(true);
            }
        });
        tvprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("orgcode", batch);
                navController.navigate(R.id.profileEdit, bundle);
            }
        });
        homenav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
                navController.navigate(R.id.homeStudent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        profilenav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("orgcode", batch);
                navController.navigate(R.id.profileEdit, bundle);
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

    private void checkbatch() {
        studentref.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    studentsModel = snapshot.getValue(StudentsModel.class);

                    batch = studentsModel.getBatch();
                    FirebaseMessaging.getInstance().subscribeToTopic(batch);
                    studentcity = studentsModel.getStudentcity();
                    posterref = database.getReference("institute").child(batch).child("slider");
                    TestListref = database.getReference("institute").child(batch).child("TestList");
                    context = getContext();
                    Glide.with(context)
                            .load(studentsModel.getImageurl())
                            .centerCrop()
                            .placeholder(R.drawable.student)
                            .into(loginuserpic);
                    setPoster();
                    getdatafromserver();
                    if (studentsModel.getBatch().equals("Nobatch")) {
                        nobatchlayout.setVisibility(View.VISIBLE);
                        homescroll.setVisibility(View.GONE);
                    } else {
                        nobatchlayout.setVisibility(View.GONE);
                        homescroll.setVisibility(View.VISIBLE);
                        loginusername.setText(studentsModel.getStudenname());
                        loginuserphonno.setText("Mob +91 " + studentsModel.getStudentphone());
                        setupnavigation();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setPoster() {
        posterref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posterlist.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    SliderMOdel mOdel = snap.getValue(SliderMOdel.class);
                    posterlist.add(mOdel);
                }
                SliderAdapter sliderAdapter = new SliderAdapter(posterlist, viewPager2);
                viewPager2.setAdapter(sliderAdapter);
                viewPager2.setOffscreenPageLimit(3);
                viewPager2.setClipChildren(false);
                viewPager2.setClipToPadding(false);
                viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                CompositePageTransformer transformer = new CompositePageTransformer();
                transformer.addTransformer(new MarginPageTransformer(30));
                transformer.addTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.14f);
                    }
                });
                viewPager2.setPageTransformer(transformer);
                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        sliderhandler.removeCallbacks(sliderRunnable);
                        sliderhandler.postDelayed(sliderRunnable, 4000);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }

    public void getdatafromserver() {
        TestListref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    testDetails = snapshot.getValue(TestDetails.class);
                    setData();
                    int i = (int) snapshot.getChildrenCount();
                    tvnotests.setText(String.valueOf(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void setData() {
        String date = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(new Date());
        Query query = TestListref.orderByChild("testdate").startAt(date).endAt(date + "\uf8ff");
        FirebaseRecyclerOptions<TestDetails> options =
                new FirebaseRecyclerOptions.Builder<TestDetails>()
                        .setQuery(query, TestDetails.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<TestDetails, TodaystestlistViewHolder>(options) {

            @NonNull
            @Override
            public TodaystestlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View rview = LayoutInflater.from(parent.getContext()).inflate(R.layout.todaytestlistlayout, parent, false);
                return new TodaystestlistViewHolder(rview);
            }

            @Override
            protected void onBindViewHolder(@NonNull TodaystestlistViewHolder holder, int position, @NonNull TestDetails model) {
                holder.testname.setText(model.getTestname());
                holder.totalquestion.setText("No.of Ques-" + model.getQuestionno());
                holder.testtime.setText(model.getStarttime());
                int totalmarks = Integer.parseInt(model.getCorrectmarks()) * Integer.parseInt(model.getQuestionno());
                holder.tvtotalmarks.setText("Marks-" + totalmarks);
                totalmarks = 0;
                holder.duration.setText("  Duration\n   " + model.getTesttime() + "mins");
                holder.date.setText("Date\n" + model.getTestdate());
                holder.teststatus.setText(model.getStatus());
                holder.editnow.setText("Start now");
                holder.editnow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkattemptest(model);
                    }
                });
                if (holder.teststatus.getText().equals("Answer not Set")) {
                    holder.teststatus.setTextColor(Color.RED);
                }

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderhandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderhandler.postDelayed(sliderRunnable, 4000);
    }

    private boolean checktimings(String time, String endtime) {
        String pattern = "h:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            return date1.before(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void settestinstruction(TestDetails model) {
        tvinstruct.setText("1.Click ok on bottom to begin the test." +
                "\n2.Do not click submit on the right corner unless complete your test." +
                "\n3.For Every Correct answer you will get " + model.getCorrectmarks() + " marks and for every wrong " + model.getWrongmarks() + " marks." +
                "\n4.You can get your score after the test completed.");
    }

    public boolean checktestperoid(String testtime, int duration) {
        SimpleDateFormat df = new SimpleDateFormat("h:mm:ss a");
        Date d = null;
        try {
            d = df.parse(testtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, duration);
        String newTime = df.format(cal.getTime());
        String currentTime = new SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(new Date());
        String pattern = "h:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(newTime);
            Date date2 = sdf.parse(currentTime);

            return !date1.before(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setdatetohome() {
        Date cd = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(cd);
        todaysdate.setText(formattedDate);
    }

    public void checkattemptest(TestDetails model) {
        TestListref.child(model.getTestid()).child("StudentResponse").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(getActivity(), "you have already attempted", Toast.LENGTH_SHORT).show();
                } else {
                    String currentTime = new SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(new Date());
                    if (model.getStatus().equals("Ready")) {
                        dialog = DialogPlus.newDialog(getContext())
                                .setContentHolder(new ViewHolder(R.layout.testcodelayout))
                                .setGravity(Gravity.CENTER)
                                .create();
                        dialog.show();
                        View myview = dialog.getHolderView();
                        TextView ok = myview.findViewById(R.id.btnsubmitcode);
                        TextView cancel = myview.findViewById(R.id.cancel);
                        tvinstruct = myview.findViewById(R.id.testinstruction);
                        EditText etcode = myview.findViewById(R.id.etcode);
                        settestinstruction(model);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        ok.setOnClickListener(v1 -> {

                            if (etcode.getText().toString().equals(model.getTestcode())) {
                                if (checktimings(model.getStarttime(), currentTime)) {
                                    if (checktestperoid(model.getStarttime(), Integer.parseInt(model.getTesttime()))) {
                                        dialog.dismiss();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("orgcode", batch);
                                        bundle.putString("testref", model.getTestid());
                                        bundle.putString("studentimg", studentsModel.getImageurl());
                                        bundle.putString("studentcity", studentcity);
                                        bundle.putString("studentname", studentsModel.getStudenname());
                                        bundle.putString("testduration", model.getTesttime());
                                        bundle.putString("teststarttime", model.getStarttime());
                                        navController.navigate(R.id.omr, bundle);
                                    } else
                                        Toast.makeText(getActivity(), "Test is Over", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getActivity(), "Test Start on- " + model.getStarttime(), Toast.LENGTH_SHORT).show();
                                }
                            } else etcode.setError("WrongCode");
                        });

                    } else if (model.getStatus().equals("completed")) {
                        Toast.makeText(getActivity(), "Test is Over", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Test is not ready", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadshareimg() {
        imageshare2.setImageDrawable(getResources().getDrawable(R.drawable.applogo));
        bitmapDrawable = (BitmapDrawable) imageshare2.getDrawable();
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

    public void setupnavigation() {
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