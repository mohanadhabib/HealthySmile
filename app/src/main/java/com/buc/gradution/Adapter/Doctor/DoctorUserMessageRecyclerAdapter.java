package com.buc.gradution.Adapter.Doctor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.Model.MessageModel;
import com.buc.gradution.R;

import java.util.ArrayList;

public class DoctorUserMessageRecyclerAdapter extends RecyclerView.Adapter<DoctorUserMessageRecyclerAdapter.DoctorUserMessageViewHolder> {
    private ArrayList<MessageModel> messages;
    private final DoctorModel doctor;
    public DoctorUserMessageRecyclerAdapter(DoctorModel doctor){
        this.doctor = doctor;
    }
    public void setMessages(ArrayList<MessageModel> messages){
        this.messages = messages;
    }
    @NonNull
    @Override
    public DoctorUserMessageRecyclerAdapter.DoctorUserMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_doctor_chat_item,parent,false);
        return new DoctorUserMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorUserMessageRecyclerAdapter.DoctorUserMessageViewHolder holder, int position) {
        if(doctor.getId().equals(messages.get(position).getSenderId())){
            holder.senderMessage.setVisibility(View.VISIBLE);
            holder.receiverMessage.setVisibility(View.INVISIBLE);
            holder.senderMessage.setText(messages.get(position).getMessage());
        }
        else{
            holder.senderMessage.setVisibility(View.INVISIBLE);
            holder.receiverMessage.setVisibility(View.VISIBLE);
            holder.receiverMessage.setText(messages.get(position).getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class DoctorUserMessageViewHolder extends RecyclerView.ViewHolder{
        TextView senderMessage,receiverMessage;

        public DoctorUserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.sender_message);
            receiverMessage = itemView.findViewById(R.id.receiver_message);
        }
    }
}
