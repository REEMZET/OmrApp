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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reemzet.omr.Models.InstuteDetails;
import com.reemzet.omr.Models.StudentsModel;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.HashMap;

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
    LinearLayout homenav;
    ImageView loginuserpic,logout;
    ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        logout=findViewById(R.id.logout);
        mAuth=FirebaseAuth.getInstance();

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;

        navController = navHostFragment.getNavController();
        database= FirebaseDatabase.getInstance();
        teacherref=database.getReference("Teacherlist");
        instituteref=database.getReference("institute");
        studentref=database.getReference("students");



        View headerView=navigationView.getHeaderView(0);
        loginusername=headerView.findViewById(R.id.loginusername);
        loginuserphonno=headerView.findViewById(R.id.loginuserphone);
        loginuserpic=headerView.findViewById(R.id.loginuserpic);
        NavigationUI.setupWithNavController(navigationView, navController);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);
        hidenav();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.gohome) {
                    Toast.makeText(MainActivity.this, "get", Toast.LENGTH_SHORT).show();
                }
              drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Alert!")
                        .setMessage("Are you Sure to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                navController.popBackStack();
                                navController.navigate(R.id.welCome);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });







    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)){
            return true;
        }
            return super.onOptionsItemSelected(item);
    }

    public void setupnavigation(){

    }

    public void hidenav(){

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
          int id=destination.getId();
            switch (id){
                case R.id.homeStudent:
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                case R.id.homeTeacher:
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                case R.id.t_testslist:
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                case R.id.studentList:
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                case R.id.setAnswer:
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                case R.id.testReport:
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                case R.id.score:
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                case R.id.report:
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                case R.id.previewAnswer:
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                case R.id.profileEdit:
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                case R.id.aboutDev:
                    toolbar.setVisibility(View.VISIBLE);
                    break;
                default:toolbar.setVisibility(View.GONE);
            }


        });


    }
    public void showloding() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.dialoprogress);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.setCanceledOnTouchOutside(false);

    }


}














