package com.reemzet.omr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
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
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.reemzet.omr.Models.StudentsModel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class ProfileEdit extends Fragment {
    public static Context context;
        EditText etname,etmob,etemail,etbatch,etprepare,etstate,etcity;
        EditText edtname,edtemail,edtprepare,edtstate,edtcity;
        ImageView btncamera;
        CircularImageView profileimage,cprofileimage;
        TextView tvedit,ctvedit;
        String orgcode;
        FirebaseDatabase database;
        DatabaseReference studentprofileref,studentref;
        FirebaseAuth mAuth;
        StudentsModel studentsModel;
        String imageurl,editedemail,editedname,editedcity,editedstate,editedprepare;
        Uri selectedimage;
        FirebaseStorage storage;
        boolean fieldboolean;
        DialogPlus dialog;
        ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile_edit, container, false);
            etname=view.findViewById(R.id.etname);
            etmob=view.findViewById(R.id.etmob);
            etemail=view.findViewById(R.id.etemail);
            etbatch=view.findViewById(R.id.etbatch);
            etprepare=view.findViewById(R.id.etprepare);
            etstate=view.findViewById(R.id.etstate);
            etcity=view.findViewById(R.id.etcity);
            btncamera=view.findViewById(R.id.btncam);
            profileimage=view.findViewById(R.id.profileimage);
            tvedit=view.findViewById(R.id.tvedit);
            orgcode=getArguments().getString("orgcode");
            context = getActivity().getApplicationContext();
            database=FirebaseDatabase.getInstance();
            mAuth=FirebaseAuth.getInstance();
             storage = FirebaseStorage.getInstance();

            studentprofileref=database.getReference("institute").child(orgcode).child("StduentList").child(mAuth.getUid());
             studentref=database.getReference("students").child(mAuth.getUid());
            studentprofileref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        studentsModel=snapshot.getValue(StudentsModel.class);
                        setData();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tvedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        dialog = DialogPlus.newDialog(getContext())
                                .setContentHolder(new ViewHolder(R.layout.editprofile))
                                .setGravity(Gravity.CENTER)
                                .setCancelable(true)
                                .create();
                        dialog.show();
                    View myview = dialog.getHolderView();
                    edtname=myview.findViewById(R.id.edtname);
                    edtemail=myview.findViewById(R.id.edtemail);
                    edtprepare=myview.findViewById(R.id.edtprepare);
                    edtstate=myview.findViewById(R.id.edtstate);
                    edtcity=myview.findViewById(R.id.edtcity);
                    cprofileimage=myview.findViewById(R.id.cprofileimage);
                    ctvedit=myview.findViewById(R.id.ctvedit);
                    TextView cancelbtn=myview.findViewById(R.id.cancelbtn);
                    ImageView cbtn=myview.findViewById(R.id.cbtn);
                    edtname.setText(studentsModel.getStudenname());
                    edtemail.setText(studentsModel.getStudentemail());
                    edtstate.setText(studentsModel.getStudentstates());
                    edtcity.setText(studentsModel.getStudentcity());
                    edtprepare.setText(studentsModel.getPreparation());
                    if (getActivity()!=null){

                        Picasso.get()
                                .load(studentsModel.getImageurl())
                                .placeholder(R.drawable.student)
                                .error(R.drawable.student)
                                .into(cprofileimage);
                    }


                    ctvedit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (checkvalid()){
                                showloding();
                                savedata();
                            }
                        }
                    });
                    cancelbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                   cbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, 45);
                        }
                    });
                }
            });






    return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() != null) {
                profileimage.setImageURI(data.getData());
                cprofileimage.setImageURI(data.getData());
                selectedimage = data.getData();
                showloding();
                upload();
            }
        }

    }
    public void upload(){

        if (selectedimage != null) {
            StorageReference profileimageref = storage.getReference(orgcode).child("ProfileImage").child(mAuth.getUid());
            profileimageref.putFile(selectedimage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        profileimageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageurl = uri.toString();
                                HashMap<String, Object> profiledetails = new HashMap<>();
                                profiledetails.put("imageurl",imageurl);
                                studentref.updateChildren(profiledetails);
                                studentprofileref.updateChildren(profiledetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                }
            });

        }else {
            imageurl = "No image";
        }

    }
     public boolean checkvalid(){
         if (edtname.getText().toString().isEmpty()){
             edtname.setError("field can't be empty");
             edtname.requestFocus();
             fieldboolean = false;
         }else if (edtemail.getText().toString().isEmpty()){
             edtemail.setError("can't be empty");
             edtemail.requestFocus();
             fieldboolean = false;
         }else if (edtprepare.getText().toString().isEmpty()){
             edtprepare.setError("field can't empty");
             fieldboolean = false;
         }else if (edtcity.getText().toString().isEmpty()){
             edtprepare.setError("field can't empty");
             fieldboolean = false;
         }else if (edtstate.getText().toString().isEmpty()) {
             edtstate.setError("field can't empty");
             fieldboolean = false;
         }else {
             editedcity=edtcity.getText().toString();
             editedemail=edtemail.getText().toString();
             editedname=edtname.getText().toString();
             editedprepare=edtprepare.getText().toString();
             editedstate=edtstate.getText().toString();
             fieldboolean=true;
         }
         return fieldboolean;
     }
    public void savedata(){
        HashMap<String, Object> profiledetails = new HashMap<>();
        profiledetails.put("preparation",editedprepare);
        profiledetails.put("studenname",editedname);
        profiledetails.put("studentcity",editedcity);
        profiledetails.put("studentstates",editedstate);
        profiledetails.put("studentemail",editedemail);
        studentref.updateChildren(profiledetails);
        studentprofileref.updateChildren(profiledetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                dialog.dismiss();
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setData() {
            etname.setText(studentsModel.getStudenname());
            etemail.setText(studentsModel.getStudentemail());
            etmob.setText(studentsModel.getStudentphone());
            etstate.setText(studentsModel.getStudentstates());
            etcity.setText(studentsModel.getStudentcity());
            etprepare.setText(studentsModel.getPreparation());
            etbatch.setText(studentsModel.getBatch());

        Picasso.get()
                .load(studentsModel.getImageurl())
                .placeholder(R.drawable.student)
                .error(R.drawable.student)
                .into(profileimage);

    }
    public void showloding() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialoprogress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }
}