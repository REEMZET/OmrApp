package com.reemzet.omr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Models.InstuteDetails;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    NavController navController;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference teacherref,instituteref,studentref;
    TextView loginusername,loginuserphonno;
    LinearLayout chatnav,videonav,notesnav,homenav;
    ImageView loginuserpic,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        logout=findViewById(R.id.logout);



        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        hidenav();

        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        teacherref=database.getReference("Teacherlist");
        instituteref=database.getReference("institute");
        studentref=database.getReference("students");


        View headerView=navigationView.getHeaderView(0);
        loginusername=headerView.findViewById(R.id.loginusername);
        loginuserphonno=headerView.findViewById(R.id.loginuserphone);
        loginuserpic=headerView.findViewById(R.id.loginuserpic);
        chatnav=headerView.findViewById(R.id.chatnav);
        notesnav=headerView.findViewById(R.id.notesnav);
        videonav=headerView.findViewById(R.id.videonav);
        homenav=headerView.findViewById(R.id.homenav);


        if (mAuth.getCurrentUser()!=null&& mAuth.getUid()!=null){
            teacherref.orderByChild(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        teacherref.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                InstuteDetails instuteDetails=snapshot.getValue(InstuteDetails.class);
                                navController.popBackStack();
                                navController.navigate(R.id.homeTeacher);
                                loginusername.setText(instuteDetails.getTeachername());
                                loginuserphonno.setText(instuteDetails.getTeacherphone());
                                setupnavigation();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }else {
                        studentref.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                navController.popBackStack();
                                navController.navigate(R.id.homeStudent);
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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                navController.popBackStack();
                navController.navigate(R.id.welCome);
            }
        });

    }
    public void setupnavigation(){
        NavigationUI.setupWithNavController(navigationView, navController);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);
    }

    public void hidenav(){
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId()==R.id.welCome ){
                toolbar.setVisibility(View.GONE);
            }
            else if (destination.getId()==R.id.omr){
                toolbar.setVisibility(View.GONE);
            }
            else if (destination.getId()==R.id.register){
                toolbar.setVisibility(View.GONE);
            }
            else {
                toolbar.setVisibility(View.VISIBLE);

            }
        });
    }
}
