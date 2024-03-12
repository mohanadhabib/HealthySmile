package com.buc.gradution.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.Model.MessageModel;
import com.buc.gradution.R;
import com.buc.gradution.View.Activity.User.UserChatActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserChatsRecyclerAdapter extends RecyclerView.Adapter<UserChatsRecyclerAdapter.UserChatsViewHolder>{
    ArrayList<MessageModel> messages;
    public void setMessages(ArrayList<MessageModel> messages){
        this.messages = messages;
    }
    @NonNull
    @Override
    public UserChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_chats,parent,false);
        return new UserChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatsViewHolder holder, int position) {
        Picasso.get().load(messages.get(position).getDoctorImg()).into(holder.doctorImg);
        holder.doctorName.setText(messages.get(position).getDoctorName());
        holder.lastMessage.setText(messages.get(position).getMessage());
        holder.layout.setOnClickListener(v ->{
            DoctorModel doctor = new DoctorModel();
            doctor.setId(messages.get(position).getDoctorId());
            doctor.setName(messages.get(position).getDoctorName());
            doctor.setEmail(messages.get(position).getDoctorEmail());
            doctor.setType(Constant.DOCTOR_TYPE);
            doctor.setProfileImgUri(messages.get(position).getDoctorImg());
            doctor.setSpec("Dentist");
            doctor.setStars(null);
            doctor.setDistance(null);
            doctor.setAbout(null);
            Intent intent = new Intent(holder.itemView.getContext(), UserChatActivity.class);
            intent.putExtra(Constant.OBJECT,doctor);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class UserChatsViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout layout;
        ShapeableImageView doctorImg;
        TextView doctorName,lastMessage;
        public UserChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            doctorImg = itemView.findViewById(R.id.doctor_img);
            doctorName = itemView.findViewById(R.id.doctor_name);
            lastMessage = itemView.findViewById(R.id.last_message);
        }
    }
}
