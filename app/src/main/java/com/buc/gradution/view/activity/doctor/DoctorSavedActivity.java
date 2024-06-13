package com.buc.gradution.view.activity.doctor;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buc.gradution.adapter.doctor.DoctorSavedRecyclerAdapter;
import com.buc.gradution.constant.Constant;
import com.buc.gradution.model.AppointmentModel;
import com.buc.gradution.R;
import com.buc.gradution.service.FirebaseSecurity;
import com.buc.gradution.service.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorSavedActivity extends AppCompatActivity {
    private FirebaseSecurity security = new FirebaseSecurity();
    private ImageView back;
    private TextView noData;
    private RecyclerView recyclerView;
    private DoctorSavedRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_saved);
        initComponents();
        back.setOnClickListener(v -> finish());
        getData();
    }
    private void initComponents(){
        back = findViewById(R.id.back);
        noData = findViewById(R.id.no_data);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        adapter = new DoctorSavedRecyclerAdapter();
    }
    private void getData(){
        FirebaseService.getFirebaseDatabase().getReference(Constant.APPOINTMENT)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AppointmentModel appointment;
                        ArrayList<AppointmentModel> appointments = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            try {
                                appointment = security.decryptAppointment(dataSnapshot.getValue().toString());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            if(appointment.getDoctorId().equals(FirebaseService.getFirebaseAuth().getCurrentUser().getUid())){
                                noData.setVisibility(View.INVISIBLE);
                                appointments.add(appointment);
                            }
                        }
                        adapter.setUsers(appointments);
                        recyclerView.setAdapter(adapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}