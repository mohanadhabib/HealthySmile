package com.buc.gradution.adapter.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.view.activity.user.UserDoctorDetailsActivity;
import com.buc.gradution.model.DoctorModel;
import com.buc.gradution.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AllDoctorsRecyclerAdapter extends RecyclerView.Adapter<AllDoctorsRecyclerAdapter.AllDoctorsRecyclerHolder> {

    private ArrayList<DoctorModel> doctors = new ArrayList<>();
    public void setDoctors(ArrayList<DoctorModel> doctors){
        this.doctors = doctors;
    }
    @NonNull
    @Override
    public AllDoctorsRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_all_doctors_item,parent,false);
        return new AllDoctorsRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllDoctorsRecyclerHolder holder, int position) {
        View root = holder.itemView;
        Picasso.get().load(doctors.get(position).getProfileImgUri()).into(holder.doctorImg);
        holder.doctorName.setText(doctors.get(position).getName());
        holder.doctorSpec.setText(doctors.get(position).getSpec());
        holder.starTxt.setText(doctors.get(position).getStars());
        holder.distance.setText(doctors.get(position).getDistance());
        holder.itemView.setOnClickListener(v ->{
            Intent intent = new Intent(root.getContext(), UserDoctorDetailsActivity.class);
            intent.putExtra("doctor",doctors.get(position));
            root.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    static class AllDoctorsRecyclerHolder extends RecyclerView.ViewHolder {
        protected ShapeableImageView doctorImg;
        protected TextView doctorName,doctorSpec,starTxt,distance;

        public AllDoctorsRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            doctorImg = itemView.findViewById(R.id.doctor_img);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpec = itemView.findViewById(R.id.doctor_spec);
            starTxt = itemView.findViewById(R.id.star_txt);
            distance = itemView.findViewById(R.id.distance);
        }
    }
}
