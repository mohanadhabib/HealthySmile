package com.buc.gradution.adapter.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.model.AppointmentModel;
import com.buc.gradution.R;
import com.buc.gradution.interfaceType.AppointmentInterface;
import com.buc.gradution.service.NetworkService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAppointmentsRecyclerAdapter extends RecyclerView.Adapter<UserAppointmentsRecyclerAdapter.UserAppointmentRecyclerHolder> {

    private AppointmentInterface appointmentInterface;
    private ArrayList<AppointmentModel> appointments;
    private Context context;
    private Gson gson = new Gson();
    public UserAppointmentsRecyclerAdapter(){}
    public UserAppointmentsRecyclerAdapter(AppointmentInterface appointmentInterface){
        this.appointmentInterface = appointmentInterface;
    }
    public void setAppointments(ArrayList<AppointmentModel> appointments){
        this.appointments = appointments;
    }
    @NonNull
    @Override
    public UserAppointmentRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_appointment_item,parent,false);
        return new UserAppointmentRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAppointmentRecyclerHolder holder, int position) {
        context = holder.itemView.getContext();
        holder.doctorName.setText(appointments.get(position).getDoctorName());
        holder.doctorSpec.setText(appointments.get(position).getDoctorSpec());
        holder.date.setText(appointments.get(position).getAppointmentDate());
        holder.time.setText(appointments.get(position).getAppointmentTime());
        Picasso.get().load(appointments.get(position).getDoctorImg()).into(holder.doctorImg);
        holder.reschedule.setOnClickListener(v -> {
            View view = holder.itemView;
           if(NetworkService.isConnected(context)){
               appointmentInterface.onReschedule(appointments.get(position));
           }
           else{
               NetworkService.connectionFailed(context);
           }
        });
        holder.cancel.setOnClickListener(v ->{
            View root = holder.itemView;
            if(NetworkService.isConnected(context)){
                appointmentInterface.onDelete(appointments.get(position));
            }
            else{
                NetworkService.connectionFailed(context);
            }
        });
    }
    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class UserAppointmentRecyclerHolder extends RecyclerView.ViewHolder{
        private TextView doctorName,doctorSpec,date,time;
        private ShapeableImageView doctorImg;
        private MaterialButton cancel,reschedule;

        public UserAppointmentRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpec = itemView.findViewById(R.id.doctor_spec);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            doctorImg = itemView.findViewById(R.id.doctor_img);
            cancel = itemView.findViewById(R.id.cancel_btn);
            reschedule = itemView.findViewById(R.id.reschedule_btn);
        }
    }
}
