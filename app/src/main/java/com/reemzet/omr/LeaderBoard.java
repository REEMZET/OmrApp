package com.reemzet.omr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.reemzet.omr.Models.RankModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class LeaderBoard extends Fragment {

    RecyclerView rankcycler;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String orgcode,testkey;
    LeaderboarAdapter adapter;
    ImageView top1,top2,top3;
    TextView tvtop1name,tvtop2name,tvtop3name;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_leader_board, container, false);
       orgcode=getArguments().getString("orgcode");
       testkey=getArguments().getString("testid");
        db=FirebaseFirestore.getInstance();
        rankcycler=view.findViewById(R.id.rankrecycler);
        mAuth=FirebaseAuth.getInstance();

        rankcycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        ArrayList<RankModel> rankModelArrayList=new ArrayList<>();
        adapter=new LeaderboarAdapter(rankModelArrayList);
        rankcycler.setAdapter(adapter);
        top1=view.findViewById(R.id.top1);
        top2=view.findViewById(R.id.top2);
        top3=view.findViewById(R.id.top3);
        tvtop1name=view.findViewById(R.id.tvtop1name);
        tvtop2name=view.findViewById(R.id.tvtop2name);
        tvtop3name=view.findViewById(R.id.tvtop3name);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();

        db.setFirestoreSettings(settings);

        db.collection("Score").document(orgcode).collection(testkey).orderBy("totalmarks", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot:queryDocumentSnapshots){
                    RankModel rankModel=snapshot.toObject(RankModel.class);
                    rankModelArrayList.add(rankModel);
                }
                adapter.notifyDataSetChanged();
            }
        });

   return  view;
    }

   public class LeaderboarAdapter extends RecyclerView.Adapter<LeaderboarAdapter.RankHolder>{

        ArrayList<RankModel>rankarralist;

       public LeaderboarAdapter(ArrayList<RankModel> rankarralist) {
           this.rankarralist = rankarralist;
       }

       @NonNull
       @Override
       public RankHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View view=LayoutInflater.from(getContext()).inflate(R.layout.leaderboardlayout,parent,false);
           return new RankHolder(view);

       }

       @Override
       public void onBindViewHolder(@NonNull RankHolder holder, int position) {
              RankModel rankModel=rankarralist.get(position);
                holder.tvstudentname.setText(rankModel.getStudentname());
                holder.tvtime.setText(rankModel.getTotaltime());
                holder.tvrank.setText(String.format("#%d",position+1));
                holder.tvmarks.setText(String.valueOf(rankModel.getTotalmarks()+"\nmarks"));
           Picasso.get()
                   .load(rankModel.getStudentimg())
                   .placeholder(R.drawable.student)
                   .error(R.drawable.student)
                   .into(holder.studentimg);
        if(!rankarralist.isEmpty() && rankarralist.size()>=3){
            RankModel topper1=rankarralist.get(0);
            RankModel topper2=rankarralist.get(1);
            RankModel topper3=rankarralist.get(2);
            Picasso.get()
                    .load(topper1.getStudentimg())
                    .placeholder(R.drawable.student)
                    .error(R.drawable.student)
                    .into(top1);
            Picasso.get()
                    .load(topper2.getStudentimg())
                    .placeholder(R.drawable.student)
                    .error(R.drawable.student)
                    .into(top2);
            Picasso.get()
                    .load(topper3.getStudentimg())
                    .placeholder(R.drawable.student)
                    .error(R.drawable.student)
                    .into(top3);

            tvtop1name.setText(topper1.getStudentname());
            tvtop2name.setText(topper2.getStudentname());
            tvtop3name.setText(topper3.getStudentname());
        }

        if (rankarralist.size()==1){
            RankModel topper1=rankarralist.get(0);
            Picasso.get()
                    .load(topper1.getStudentimg())
                    .placeholder(R.drawable.student)
                    .error(R.drawable.student)
                    .into(top1);
            tvtop1name.setText(topper1.getStudentname());
        }



       }

       @Override
       public int getItemCount() {
           return rankarralist.size();
       }

       public class RankHolder extends RecyclerView.ViewHolder {
           public TextView tvstudentname,tvrank,tvmarks,tvtime;
           public CircularImageView studentimg;
           public RankHolder(@NonNull View itemView) {
               super(itemView);
               tvstudentname=itemView.findViewById(R.id.tvstudentname);
               studentimg=itemView.findViewById(R.id.studentimg);
               tvrank=itemView.findViewById(R.id.tvrank);
               tvmarks=itemView.findViewById(R.id.tvtotalmarks);
               tvtime=itemView.findViewById(R.id.tvtime);


           }
       }
   }
}