package com.reemzet.omr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Models.ScoreModel;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class Score extends Fragment {

    PieChart pieChart;
    FirebaseDatabase database;
    DatabaseReference scoreref;

    String orgcode,testid,teststarttime,testduration;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    int  totalmarks,correctquestion,unattempedquestion,totalquestion,eachcorrectmarks;
    TextView tvobtainedmarks,tvtotalque,tvcorectans,tvincorrect;
    LinearLayout linearleaderboard,contentlinear, sharelinear,linearpreviewomr,linearback;
    NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_score, container, false);
        orgcode=getArguments().getString("orgcode");
        testid=getArguments().getString("testid");
        totalmarks=Integer.parseInt(getArguments().getString("totalmarks"));
        correctquestion=Integer.parseInt(getArguments().getString("correctquestion"));
        unattempedquestion=Integer.parseInt(getArguments().getString("unattempedquestion"));
       totalquestion=Integer.parseInt(getArguments().getString("totalquestion"));
        eachcorrectmarks=Integer.parseInt(getArguments().getString("eachcorrectmarks"));
        teststarttime=getArguments().getString("teststarttime");
        testduration=getArguments().getString("testduration");
        pieChart=view.findViewById(R.id.scorepiechart);
        tvobtainedmarks=view.findViewById(R.id.tvobtainedmarks);
        tvtotalque=view.findViewById(R.id.tvtotalque);
        tvcorectans=view.findViewById(R.id.tvcorectans);
        tvincorrect=view.findViewById(R.id.tvincorrect);
        linearleaderboard=view.findViewById(R.id.linearleaderboard);
        contentlinear=view.findViewById(R.id.contentlinear);
        sharelinear=view.findViewById(R.id.sharelinear);
        linearpreviewomr=view.findViewById(R.id.linearpreviewans);
        linearback=view.findViewById(R.id.linearback);

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        database=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();

        scoreref=database.getReference("institute").child(orgcode).child("TestList").child(testid).child("StudentResponse").child(mAuth.getUid()).child("Score");
        loadpiechart();

        tvobtainedmarks.setText(String.valueOf(totalmarks));
        tvtotalque.setText(String.valueOf(totalquestion));
        tvcorectans.setText(String.valueOf(correctquestion));
        tvincorrect.setText(String.valueOf(totalquestion-correctquestion-unattempedquestion));

            linearleaderboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("orgcode",orgcode);
                    bundle.putString("testid",testid);
                    navController.navigate(R.id.leaderBoard,bundle);
                }
            });
        sharelinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invite();
            }
        });

        linearpreviewomr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("orgcode",orgcode);
                bundle.putString("testid",testid);
                navController.navigate(R.id.previewAnswer,bundle);
            }
        });
        linearback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("orgcode",orgcode);
                navController.popBackStack();
                navController.navigate(R.id.homeStudent,bundle);
            }
        });



   return  view;
    }
    public void showloding() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialoprogress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    public void loadpiechart(){
        int marksobtained=totalmarks;
        int deductedmarks=totalquestion*eachcorrectmarks-(marksobtained);
        ArrayList<PieEntry> records=new ArrayList<>();
        records.add(new PieEntry(marksobtained,"Obtained Marks"));
        records.add(new PieEntry(deductedmarks,"Lost Marks"));
        PieDataSet dataSet=new PieDataSet(records,"Test Report");
        dataSet.setColor(Color.WHITE);

        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(15f);
        PieData pieData=new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("Total Marks\n"+(totalquestion)*eachcorrectmarks);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterTextSize(16f);
        pieChart.animate();


    }

    public void invite() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        Uri bmpuri;
        int happy=0x1F929;
        int hand=0x1F449;
        String text = "hey this is my score card"+getEmojiByUnicode(happy)+"you can also try this nice app"+getEmojiByUnicode(hand)+"https://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + "&hl=it";
        bmpuri = saveImage(getBitmapFromView(contentlinear), getActivity());
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_STREAM, bmpuri);
        share.putExtra(Intent.EXTRA_SUBJECT, "share App");
        share.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(share, "Share to"));
    }
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
    private static Uri saveImage(Bitmap image, Context context) {

        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_images.jpg");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),
                    "com.reemzet.omr" + ".provider", file);

        } catch (IOException e) {

        }
        return uri;
    }
    public static Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }


}
