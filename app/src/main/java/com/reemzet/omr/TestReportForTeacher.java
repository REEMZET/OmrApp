package com.reemzet.omr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.reemzet.omr.Models.RankModel;
import com.reemzet.omr.Models.TestDetails;

import java.util.ArrayList;

import me.bastanfar.semicirclearcprogressbar.SemiCircleArcProgressBar;

public class TestReportForTeacher extends Fragment {


    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String orgcode,testkey;
    ImageView top1,top2,top3;
    TextView tvtop1name,tvtop2name,tvtop3name,tvhighscore,tvattempedstudent,tvtop10score,tvhighscoremrks;
    ArrayList<RankModel> rankModelArrayList;
    double totalmarksobtained=0,totaltestmarks;
    TextView btnscoreboard;
    NavController navController;
    TestDetails testDetails;
    FirebaseDatabase database;
    DatabaseReference testdetailsref,studentattemptedref,totalstudentref;
    SemiCircleArcProgressBar prhighscore,prattempt,prclasstop10;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view= inflater.inflate(R.layout.fragment_test_report_for_teacher, container, false);
        orgcode=getArguments().getString("orgcode");
        testkey=getArguments().getString("testid");
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        rankModelArrayList =new ArrayList<>();
        top1=view.findViewById(R.id.top1);
        top2=view.findViewById(R.id.top2);
        top3=view.findViewById(R.id.top3);
        tvtop1name=view.findViewById(R.id.tvtop1name);
        tvtop2name=view.findViewById(R.id.tvtop2name);
        tvtop3name=view.findViewById(R.id.tvtop3name);
        tvhighscore=view.findViewById(R.id.tvhighscore);
        tvattempedstudent = view.findViewById(R.id.tvattemptstudent);
        tvtop10score=view.findViewById(R.id.tvtop10score);
        btnscoreboard=view.findViewById(R.id.btnscoreboard);
        prhighscore=view.findViewById(R.id.prhighscore);
        prattempt=view.findViewById(R.id.prattempt);
        prclasstop10=view.findViewById(R.id.prclasstop10score);
        tvhighscoremrks=view.findViewById(R.id.tvhighscoremarks);


        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        database= FirebaseDatabase.getInstance();
        testdetailsref=database.getReference("institute").child(orgcode).child("TestList").child(testkey);
        studentattemptedref=testdetailsref.child("StudentResponse");
        totalstudentref=database.getReference("institute").child(orgcode).child("StduentList");
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);
        setfirebaasedetails();




        btnscoreboard.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putString("orgcode",orgcode);
            bundle.putString("testid",testkey);
            navController.navigate(R.id.leaderBoard,bundle);
        });


         return view;
    }
    public void setfirestoredetails(){
        db.collection("Score").document(orgcode).collection(testkey).orderBy("totalmarks", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot:queryDocumentSnapshots){
                    RankModel rankModel=snapshot.toObject(RankModel.class);
                    rankModelArrayList.add(rankModel);
                }
                if(rankModelArrayList.size()>=3){
                    RankModel topper1=rankModelArrayList.get(0);
                    RankModel topper2=rankModelArrayList.get(1);
                    RankModel topper3=rankModelArrayList.get(2);
                    Glide.with(getActivity())
                            .load(topper1.getStudentimg())
                            .centerCrop()
                            .placeholder(R.drawable.student)
                            .into(top1);
                    Glide.with(getActivity())
                            .load(topper2.getStudentimg())
                            .centerCrop()
                            .placeholder(R.drawable.student)
                            .into(top2);
                    Glide.with(getActivity())
                            .load(topper3.getStudentimg())
                            .centerCrop()
                            .placeholder(R.drawable.student)
                            .into(top3);
                    tvtop1name.setText(topper1.getStudentname());
                    tvtop2name.setText(topper2.getStudentname());
                    tvtop3name.setText(topper3.getStudentname());
                }
                if (rankModelArrayList.size()==1){
                    RankModel topper1=rankModelArrayList.get(0);
                    Glide.with(getActivity())
                            .load(topper1.getStudentimg())
                            .centerCrop()
                            .placeholder(R.drawable.student)
                            .into(top1);
                    tvtop1name.setText(topper1.getStudentname());
                }
                if (rankModelArrayList.size()>=10){
                    for (int i=0;i<=10;i++){
                        RankModel top=rankModelArrayList.get(i);
                        totalmarksobtained=totalmarksobtained+top.getTotalmarks();
                    }
                    tvtop10score.setText(String.valueOf(totalmarksobtained/10));
                    prclasstop10.setPercentWithAnimation((int) calculatePercentage(totalmarksobtained,10));
                }else {
                    tvtop10score.setText("N/A");
                }

                if (!rankModelArrayList.isEmpty()){
                    RankModel top1=rankModelArrayList.get(0);
                  double highscore= top1.getTotalmarks();
                  tvhighscoremrks.setText("High Score-"+String.valueOf(top1.getTotalmarks()));
                    tvhighscore.setText(String.valueOf(calculatePercentage(highscore,totaltestmarks)+"%"));
                    prhighscore.setPercent((int) calculatePercentage(highscore,totaltestmarks));
                       prhighscore.setPercentWithAnimation((int) calculatePercentage(highscore,totaltestmarks));

                }
            }
        });


    }

    public void setfirebaasedetails(){
        testdetailsref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                testDetails=snapshot.getValue(TestDetails.class);
               totaltestmarks=Integer.parseInt(testDetails.getQuestionno())*Integer.parseInt(testDetails.getCorrectmarks());
                setfirestoredetails();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        studentattemptedref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int studentattempt=(int) snapshot.getChildrenCount();
                    totalstudentref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                int totalstudent=(int) snapshot.getChildrenCount();
                                    tvattempedstudent.setText(studentattempt + "/" + totalstudent);
                                    prattempt.setPercentWithAnimation((int) calculatePercentage(studentattempt,totalstudent));
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
    public double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }
}