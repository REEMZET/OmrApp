package com.reemzet.omr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reemzet.omr.Adapter.OmrAdapter;
import com.reemzet.omr.Models.OmrModel;

import java.util.ArrayList;


public class Omr extends Fragment {
  RecyclerView omrrecycler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view= inflater.inflate(R.layout.fragment_omr, container, false);
            omrrecycler=view.findViewById(R.id.omrrecycyler);
        ArrayList<OmrModel> list = new ArrayList<>();
        int i,questionno=50;
        for (i=0;i<=questionno; i++){
            if (i!=0){
                list.add(new OmrModel("a","b","c","d",String.valueOf(i)));
            }

        }




        OmrAdapter omrAdapter=new OmrAdapter(getContext(),list);
        omrrecycler.setAdapter(omrAdapter);
        omrrecycler.setLayoutManager(new GridLayoutManager(getContext(), 1));



   return view;
    }
}