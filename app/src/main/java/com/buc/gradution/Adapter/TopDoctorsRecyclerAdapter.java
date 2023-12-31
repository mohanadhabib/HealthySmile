package com.buc.gradution.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.buc.gradution.R;
import com.google.android.material.imageview.ShapeableImageView;

public class TopDoctorsRecyclerAdapter extends RecyclerView.Adapter<TopDoctorsRecyclerAdapter.TopDoctorsRecyclerHolder> {

    @NonNull
    @Override
    public TopDoctorsRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_top_doctors_item,parent,false);
        return new TopDoctorsRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopDoctorsRecyclerHolder holder, int position) {
        holder.doctorImg.setImageResource(R.drawable.ic_user);
        holder.doctorName.setText(holder.itemView.getResources().getString(R.string.dummy_doctor_name));
        holder.doctorSpec.setText(holder.itemView.getResources().getString(R.string.dummy_doctor_spec));
        holder.starTxt.setText(holder.itemView.getResources().getString(R.string.dummy_rating));
        holder.distance.setText(holder.itemView.getResources().getString(R.string.dummy_distance));
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    static class TopDoctorsRecyclerHolder extends RecyclerView.ViewHolder{
        protected ShapeableImageView doctorImg;
        protected TextView doctorName,doctorSpec,starTxt,distance;
        public TopDoctorsRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            doctorImg = itemView.findViewById(R.id.doctor_img);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpec = itemView.findViewById(R.id.doctor_spec);
            starTxt = itemView.findViewById(R.id.star_txt);
            distance = itemView.findViewById(R.id.distance);
        }
    }
}
