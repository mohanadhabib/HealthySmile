package com.buc.gradution.Adapter.Doctor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Model.AppointmentModel;
import com.buc.gradution.R;
import com.buc.gradution.View.Activity.Doctor.DoctorNotesActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DoctorAppointmentsRecyclerAdapter extends RecyclerView.Adapter<DoctorAppointmentsRecyclerAdapter.DoctorAppointmentRecyclerHolder> {
    private ArrayList<AppointmentModel> appointments;
    private Context context;
    private Gson gson = new Gson();
    public DoctorAppointmentsRecyclerAdapter(){}
    public void setAppointments(ArrayList<AppointmentModel> appointments){
        this.appointments = appointments;
    }
    @NonNull
    @Override
    public DoctorAppointmentsRecyclerAdapter.DoctorAppointmentRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_doctor_appointment_item,parent,false);
        return new DoctorAppointmentsRecyclerAdapter.DoctorAppointmentRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorAppointmentRecyclerHolder holder, int position) {
        context = holder.itemView.getContext();
        holder.userName.setText(appointments.get(position).getUserName());
        holder.date.setText(appointments.get(position).getAppointmentDate());
        holder.time.setText(appointments.get(position).getAppointmentTime());
        Picasso.get().load(appointments.get(position).getUserImg()).into(holder.userImg);
        holder.appointmentLayout.setOnClickListener(v ->{
            Intent intent = new Intent(context.getApplicationContext(), DoctorNotesActivity.class);
            intent.putExtra("appointment",appointments.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }
    static class DoctorAppointmentRecyclerHolder extends RecyclerView.ViewHolder{
        private ConstraintLayout appointmentLayout;
        private TextView userName,date,time;
        private ShapeableImageView userImg;

        public DoctorAppointmentRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            userImg = itemView.findViewById(R.id.user_img);
            appointmentLayout = itemView.findViewById(R.id.appointment_layout);
        }
    }
}
