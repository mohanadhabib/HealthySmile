package com.buc.gradution.View.Activity.User;

import android.os.Bundle;

import com.buc.gradution.Adapter.User.UserDoctorNotesRecyclerAdapter;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.Model.MessageModel;
import com.buc.gradution.Model.NotesModel;
import com.buc.gradution.Service.FirebaseService;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.databinding.ActivityUserDoctorNotesBinding;

import com.buc.gradution.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserDoctorNotesActivity extends AppCompatActivity {
    private DoctorModel doctor;
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
        back.setOnClickListener(v ->{
            finish();
        });
        getNotes();
    }
    private void initComponents(){
        back = findViewById(R.id.back);
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
                            NotesModel note = snapshot1.getValue(NotesModel.class);
                            notes.add(note);
                        }
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