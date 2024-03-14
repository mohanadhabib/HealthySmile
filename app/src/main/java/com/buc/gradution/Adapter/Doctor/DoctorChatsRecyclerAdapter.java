package com.buc.gradution.Adapter.Doctor;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.MessageModel;
import com.buc.gradution.Model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.View.Activity.Doctor.DoctorChatActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DoctorChatsRecyclerAdapter extends RecyclerView.Adapter<DoctorChatsRecyclerAdapter.DoctorChatsViewHolder>{
    ArrayList<MessageModel> messages;
    public void setMessages(ArrayList<MessageModel> messages){
        this.messages = messages;
    }
    @NonNull
    @Override
    public DoctorChatsRecyclerAdapter.DoctorChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_chats,parent,false);
        return new DoctorChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorChatsRecyclerAdapter.DoctorChatsViewHolder holder, int position) {
        Picasso.get().load(messages.get(position).getUserImg()).into(holder.doctorImg);
        holder.doctorName.setText(messages.get(position).getUserName());
        holder.lastMessage.setText(messages.get(position).getMessage());
        holder.layout.setOnClickListener(v ->{
            UserModel user = new UserModel();
            user.setId(messages.get(position).getUserId());
            user.setName(messages.get(position).getUserName());
            user.setEmail(messages.get(position).getUserEmail());
            user.setType(Constant.PATIENT_TYPE);
            user.setProfileImgUri(messages.get(position).getUserImg());
            Intent intent = new Intent(holder.itemView.getContext(), DoctorChatActivity.class);
            intent.putExtra(Constant.OBJECT,user);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class DoctorChatsViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout layout;
        ShapeableImageView doctorImg;
        TextView doctorName,lastMessage;
        public DoctorChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            doctorImg = itemView.findViewById(R.id.doctor_img);
            doctorName = itemView.findViewById(R.id.doctor_name);
            lastMessage = itemView.findViewById(R.id.last_message);
        }
    }
}
