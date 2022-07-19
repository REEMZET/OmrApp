package com.reemzet.omr.Adapter;



import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.reemzet.omr.R;

public class BatchlistViewHolder extends RecyclerView.ViewHolder {
  public   TextView tvinsitutename,tvorgcode,tvteachername,tvinstitutecity,tvinstitutestate,tvsendrequest;
    public CircularImageView instituteimage;
    public BatchlistViewHolder(@NonNull View itemView) {
        super(itemView);
        tvinsitutename=itemView.findViewById(R.id.institutename);
        tvinstitutecity=itemView.findViewById(R.id.city);
        tvinstitutestate=itemView.findViewById(R.id.state);
        tvorgcode=itemView.findViewById(R.id.orgcode);
        tvteachername=itemView.findViewById(R.id.directorname);
        instituteimage=itemView.findViewById(R.id.instituteimage);
        tvsendrequest=itemView.findViewById(R.id.tvrequest);
    }
}
