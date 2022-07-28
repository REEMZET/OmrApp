package com.reemzet.omr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.reemzet.omr.Models.InstuteDetails;

import java.util.HashMap;


public class EditInstituteDetails extends Fragment {

    EditText etinstitutename,etbatch,etcity,etstate,etphone,etmail;
    String imgeurl;
    Uri selectedimage;
    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference instituteref;
    CircularImageView instituteimg;
    ImageView btncamera;
    TextView tvupload;
    InstuteDetails instuteDetails;
    String orgcode;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_edit_institute_details, container, false);

        etinstitutename=view.findViewById(R.id.etname);
        etphone=view.findViewById(R.id.etmob);
        etmail=view.findViewById(R.id.etemail);
        etbatch=view.findViewById(R.id.etbatch);
        etstate=view.findViewById(R.id.etstate);
        etcity=view.findViewById(R.id.etcity);
        btncamera=view.findViewById(R.id.btncam);
        instituteimg=view.findViewById(R.id.profileimage);
        tvupload=view.findViewById(R.id.tvupload);
        orgcode=getArguments().getString("orgcode");

        database=FirebaseDatabase.getInstance();
        mAuth= FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        instituteref=database.getReference("institute").child(orgcode).child("InstituteDetails");
        instituteref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                instuteDetails=snapshot.getValue(InstuteDetails.class);
                setData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        tvupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showloding();
                upload();
            }
        });
        btncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });
            return view;
    }

    public void setData(){
        etinstitutename.setText(instuteDetails.getInstitutename());
        etmail.setText(instuteDetails.getTeacheremail());
        etbatch.setText(instuteDetails.getOrgcode());
        etphone.setText(instuteDetails.getTeacherphone());
        etcity.setText(instuteDetails.getCity());
        etstate.setText(instuteDetails.getState());
        Glide.with(getActivity())
                .load(instuteDetails.getInstituteimage())
                .centerCrop()
                .placeholder(R.drawable.student)
                .into(instituteimg);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() != null) {
                instituteimg.setImageURI(data.getData());
                selectedimage = data.getData();
            }
        }

    }
    public void upload(){
        if (selectedimage != null) {
            StorageReference profileimageref = storage.getReference(orgcode).child("instituteimg").child(mAuth.getUid());
            profileimageref.putFile(selectedimage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        profileimageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imgeurl = uri.toString();
                                HashMap<String, Object> profiledetails = new HashMap<>();
                                profiledetails.put("instituteimage",imgeurl);
                               instituteref.updateChildren(profiledetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                }
            });

        }else {
            imgeurl= "No image";
        }

    }
    public void showloding() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialoprogress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }

}