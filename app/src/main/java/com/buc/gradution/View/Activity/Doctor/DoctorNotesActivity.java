package com.buc.gradution.View.Activity.Doctor;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.buc.gradution.Model.AppointmentModel;
import com.buc.gradution.Model.NotesModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseSecurity;
import com.buc.gradution.Service.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class DoctorNotesActivity extends AppCompatActivity {
    private final FirebaseSecurity security = new FirebaseSecurity();
    private ImageView back;
    private TextInputLayout notesLayout;
    private MaterialButton sendBtn;
    private NotesModel notesModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_notes);
        initComponents();
        AppointmentModel appointmentModel = (AppointmentModel) getIntent().getSerializableExtra("appointment");
        back.setOnClickListener(v -> finish());
        sendBtn.setOnClickListener(v ->{
            if(!validateInputLayout()){
                try {
                    sendNotesToDB(appointmentModel);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void initComponents(){
        back = findViewById(R.id.back);
        notesLayout = findViewById(R.id.notes_layout);
        sendBtn = findViewById(R.id.send);
    }
    private boolean validateInputLayout(){
        return notesLayout.getEditText().getText().toString().isEmpty();
    }
    private void sendNotesToDB(AppointmentModel appointment) throws Exception {
        String notes = notesLayout.getEditText().getText().toString();
        notesModel = new NotesModel(appointment.getUserId(),appointment.getUserName(),appointment.getUserEmail(),appointment.getUserImg(),appointment.getDoctorId(),appointment.getDoctorName(),appointment.getDoctorEmail(),appointment.getDoctorImg(),appointment.getDoctorSpec(),appointment.getAppointmentDate(),appointment.getAppointmentTime(),appointment.getStars(),appointment.getDistance(),appointment.getAboutDoctor(),notes);
        String patientRef = notesModel.getUserId();
        String doctorRef = notesModel.getDoctorId();
        String data = security.encrypt(notesModel);
        FirebaseService.getFirebaseDatabase().getReference("Notes")
                .child(patientRef)
                .child(doctorRef)
                .push()
                .setValue(data)
                .addOnSuccessListener(v -> {
                    notesLayout.getEditText().setText("");
                    Toast.makeText(getApplicationContext(), "Notes have been successfully added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}