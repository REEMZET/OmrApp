package com.reemzet.omr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WelCome extends Fragment {
    Button btnstudent,teacherbtn;
    NavController navController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_wel_come, container, false);

       btnstudent=view.findViewById(R.id.studentbtn);
       teacherbtn=view.findViewById(R.id.teacherbtn);



        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();


        teacherbtn.setOnClickListener(v -> navController.navigate(R.id.action_welCome_to_register));
        btnstudent.setOnClickListener(v -> { navController.navigate(R.id.action_welCome_to_registerStudent); });


       return view;
    }
}