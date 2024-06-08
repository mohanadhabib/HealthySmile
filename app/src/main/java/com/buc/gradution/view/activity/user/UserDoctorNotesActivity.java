package com.buc.gradution.view.activity.user;

import android.os.Bundle;

import com.buc.gradution.adapter.user.UserDoctorNotesRecyclerAdapter;
import com.buc.gradution.model.DoctorModel;
import com.buc.gradution.model.NotesModel;
import com.buc.gradution.service.FirebaseSecurity;
import com.buc.gradution.service.FirebaseService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.buc.gradution.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserDoctorNotesActivity extends AppCompatActivity {
    private final FirebaseSecurity security = new FirebaseSecurity();
    private DoctorModel doctor;
    private TextView noNotesTxt;
    private ImageView back;
    private RecyclerView recyclerView;
    private ArrayList<NotesModel> notes = new ArrayList<>();
    private UserDoctorNotesRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_doctor_notes);
        initComponents();
        doctor = (DoctorModel) getIntent().getSerializableExtra("doctor");
        back.setOnClickListener(v -> finish());
        getNotes();
    }
    private void initComponents(){
        back = findViewById(R.id.back);
        noNotesTxt = findViewById(R.id.no_notes_text);
        recyclerView = findViewById(R.id.notes_recycler);
        adapter = new UserDoctorNotesRecyclerAdapter();
    }
    private void getNotes(){
        String userRef = FirebaseService.getFirebaseAuth().getCurrentUser().getUid();
        FirebaseService.getFirebaseDatabase().getReference("Notes")
                .child(userRef)
                .child(doctor.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            NotesModel note;
                            try {
                                note = security.decryptNote(snapshot1.getValue().toString());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            notes.add(note);
                        }
                        noNotesTxt.setVisibility(View.INVISIBLE);
                        adapter.setNotes(notes);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}