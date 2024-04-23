package com.buc.gradution.Adapter.User;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.Model.NotesModel;
import com.buc.gradution.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserDoctorNotesRecyclerAdapter extends RecyclerView.Adapter<UserDoctorNotesRecyclerAdapter.UserDoctorNotesRecyclerHolder>{
    ArrayList<NotesModel> notes = new ArrayList<>();

    public void setNotes(ArrayList<NotesModel> notes){
        this.notes = notes;
    }
    @NonNull
    @Override
    public UserDoctorNotesRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_doctor_note_item,parent,false);
        return new UserDoctorNotesRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDoctorNotesRecyclerHolder holder, int position) {
        holder.doctorNotes.setText(notes.get(position).getDoctorNotes());
        holder.doctorSpec.setText(notes.get(position).getDoctorSpec());
        holder.doctorName.setText(notes.get(position).getDoctorName());
        Picasso.get().load(notes.get(position).getDoctorImg()).into(holder.doctorImg);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class UserDoctorNotesRecyclerHolder extends RecyclerView.ViewHolder {
        TextView doctorName,doctorSpec,doctorNotes;
        ShapeableImageView doctorImg;

        public UserDoctorNotesRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            doctorImg = itemView.findViewById(R.id.doctor_img);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpec = itemView.findViewById(R.id.doctor_spec);
            doctorNotes = itemView.findViewById(R.id.doctor_notes);
        }
    }
}
