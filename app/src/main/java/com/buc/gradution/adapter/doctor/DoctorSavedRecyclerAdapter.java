package com.buc.gradution.adapter.doctor;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.model.AppointmentModel;
import com.buc.gradution.model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.view.activity.user.UserHistoryActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DoctorSavedRecyclerAdapter extends RecyclerView.Adapter<DoctorSavedRecyclerAdapter.DoctorSavedRecyclerViewHolder>{
    ArrayList<AppointmentModel> users = new ArrayList<>();
    public void setUsers(ArrayList<AppointmentModel> users){
        this.users = users;
    }
    @NonNull
    @Override
    public DoctorSavedRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_doctor_saved_item,parent,false);
        return new DoctorSavedRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorSavedRecyclerViewHolder holder, int position) {
        Picasso.get().load(users.get(position).getUserImg()).into(holder.image);
        holder.name.setText(users.get(position).getUserName());
        holder.userLayout.setOnClickListener(v -> {
            AppointmentModel appointment = users.get(position);
            UserModel user = new UserModel(appointment.getUserId(),appointment.getUserName(),appointment.getUserEmail(),"User",appointment.getUserImg(),"");
            Intent intent = new Intent(holder.itemView.getContext().getApplicationContext(), UserHistoryActivity.class);
            intent.putExtra("user",user);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class DoctorSavedRecyclerViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout userLayout;
        ImageView image;
        TextView name;
        public DoctorSavedRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            userLayout = itemView.findViewById(R.id.user_layout);
            image = itemView.findViewById(R.id.user_img);
            name = itemView.findViewById(R.id.user_name);
        }
    }
}
