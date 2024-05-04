package com.buc.gradution.Adapter.User;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Model.HistoryModel;
import com.buc.gradution.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserHistoryRecyclerAdapter extends RecyclerView.Adapter<UserHistoryRecyclerAdapter.UserHistoryRecyclerViewHolder>{

    private ArrayList<HistoryModel> history = new ArrayList<>();
    public void setHistory(ArrayList<HistoryModel> history){
        this.history = history;
    }
    @NonNull
    @Override
    public UserHistoryRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user_history,parent,false);
        return new UserHistoryRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHistoryRecyclerViewHolder holder, int position) {
        Picasso.get().load(history.get(position).getImgUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    static class UserHistoryRecyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public UserHistoryRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
