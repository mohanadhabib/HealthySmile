package com.buc.gradution.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buc.gradution.model.UserAiChatMessageModel;
import com.buc.gradution.R;

import java.util.ArrayList;

public class UserAiChatListViewAdapter extends BaseAdapter {
    private ArrayList<UserAiChatMessageModel> messages = new ArrayList<>();
    public void setMessages(ArrayList<UserAiChatMessageModel> messages){
        this.messages = messages;
    }
    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_ai_chat_item,parent,false);
        TextView sender = convertView.findViewById(R.id.sender_message);
        TextView receiver = convertView.findViewById(R.id.receiver_message);
        UserAiChatMessageModel message = messages.get(position);
        if(message.getId()==0){
            receiver.setVisibility(View.VISIBLE);
            sender.setVisibility(View.INVISIBLE);
            receiver.setText(message.getMessage());
        }
        else if(message.getId()==1){
            sender.setVisibility(View.VISIBLE);
            receiver.setVisibility(View.INVISIBLE);
            sender.setText(message.getMessage());
        }
        return convertView;
    }
}
