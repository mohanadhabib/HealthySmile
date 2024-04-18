package com.buc.gradution.Adapter.User;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Model.UserAiChatMessageModel;
import com.buc.gradution.R;

import java.util.ArrayList;

public class UserAIChatsRecyclerAdapter extends RecyclerView.Adapter<UserAIChatsRecyclerAdapter.UserAIChatsViewHolder>{

    private ArrayList<UserAiChatMessageModel> messages = new ArrayList<>();
    public void setMessages(ArrayList<UserAiChatMessageModel> messages){
        this.messages = messages;
    }
    @NonNull
    @Override
    public UserAIChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_ai_chat_item,parent,false);
        return new UserAIChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAIChatsViewHolder holder, int position) {
        UserAiChatMessageModel message = messages.get(position);
        if(message.getId()==0){
            holder.receiverMessage.setVisibility(View.VISIBLE);
            holder.senderMessage.setVisibility(View.INVISIBLE);
            holder.receiverMessage.setText(message.getMessage());
        }
        else if(message.getId()==1){
            holder.senderMessage.setVisibility(View.VISIBLE);
            holder.receiverMessage.setVisibility(View.INVISIBLE);
            holder.senderMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserAIChatsViewHolder extends RecyclerView.ViewHolder{
        private TextView senderMessage;
        private TextView receiverMessage;
        public UserAIChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.sender_message);
            receiverMessage = itemView.findViewById(R.id.receiver_message);
        }
    }
}
