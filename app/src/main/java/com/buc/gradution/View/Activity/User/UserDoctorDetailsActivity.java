package com.buc.gradution.View.Activity.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.AppointmentModel;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.Model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.buc.gradution.Service.NetworkService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class UserDoctorDetailsActivity extends AppCompatActivity {

    private ImageView back;
    private ShapeableImageView doctorImg;
    private TextView doctorName,doctorSpec,stars,distance,about,date,time;
    private MaterialButton datePicker,timePicker,chatButton,bookAppointment;
    private DoctorModel doctor;
    private UserModel user;
    private AppointmentModel appointment;
    private ArrayList<AppointmentModel> appointments;
    private Intent intent;
    private Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_doctor_details);
        initComponents();
        intent = getIntent();
        getView();
        back.setOnClickListener(v -> finish());
        datePicker.setOnClickListener(v -> pickDate());
        timePicker.setOnClickListener(v -> pickTime());
        bookAppointment.setOnClickListener(v -> bookAnAppointment());
    }
    private void getView(){
        if(intent.getSerializableExtra(Constant.APPOINTMENT) != null){
            appointment = (AppointmentModel) intent.getSerializableExtra(Constant.APPOINTMENT);
            Picasso.get().load(appointment.getDoctorImg()).into(doctorImg);
            doctorName.setText(appointment.getDoctorName());
            doctorSpec.setText(appointment.getDoctorSpec());
            stars.setText(appointment.getStars());
            distance.setText(appointment.getDistance());
            about.setText(appointment.getAboutDoctor());
        }else{
            String json = getSharedPreferences(Constant.CURRENT_USER,0).getString(Constant.OBJECT,"");
            user = gson.fromJson(json, UserModel.class);
            doctor = (DoctorModel) intent.getSerializableExtra("doctor");
            Picasso.get().load(doctor.getProfileImgUri()).into(doctorImg);
            doctorName.setText(doctor.getName());
            doctorSpec.setText(doctor.getSpec());
            stars.setText(doctor.getStars());
            distance.setText(doctor.getDistance());
            about.setText(doctor.getAbout());
        }
    }
    private void initComponents(){
        back = findViewById(R.id.back);
        doctorImg = findViewById(R.id.doctor_img);
        doctorName = findViewById(R.id.doctor_name);
        doctorSpec = findViewById(R.id.doctor_spec);
        stars = findViewById(R.id.star_txt);
        distance = findViewById(R.id.distance);
        about = findViewById(R.id.about_txt);
        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.time_picker);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        chatButton = findViewById(R.id.chat_btn);
        bookAppointment = findViewById(R.id.book_appointment);
    }
    private void pickTime(){
        AtomicReference<String> timeTxt = new AtomicReference<>();
        MaterialTimePicker.Builder materialTimePicker = new MaterialTimePicker.Builder();
        MaterialTimePicker timePicker = materialTimePicker.build();
        timePicker.show(getSupportFragmentManager(),"UserDoctorDetailsActivity");
        timePicker.addOnPositiveButtonClickListener(n -> {
            timeTxt.set(timePicker.getHour() + ":" + timePicker.getMinute());
            time.setText(timeTxt.get());
        });
    }
    private void pickDate(){
        AtomicReference<String> dateTxt = new AtomicReference<>();
        MaterialDatePicker.Builder materialDatePicker = MaterialDatePicker.Builder.datePicker();
        MaterialDatePicker datePicker = materialDatePicker.build();
        datePicker.show(getSupportFragmentManager(),"UserDoctorDetailsActivity");
        datePicker.addOnPositiveButtonClickListener(n -> {
            dateTxt.set(datePicker.getHeaderText());
            date.setText(dateTxt.get());
        });
    }
    private void bookAnAppointment(){
        String appointmentDate = date.getText().toString();
        String appointmentTime = time.getText().toString();
        if (appointmentDate.equals(getString(R.string.no_date_selected))&&appointmentTime.equals(getString(R.string.no_time_selected))){
            Toast.makeText(UserDoctorDetailsActivity.this, "Please select appointment date\nand time first", Toast.LENGTH_SHORT).show();
        } else if(appointmentDate.equals(getString(R.string.no_date_selected))){
            Toast.makeText(UserDoctorDetailsActivity.this, "Please select appointment date first", Toast.LENGTH_SHORT).show();
        } else if(appointmentTime.equals(getString(R.string.no_time_selected))){
            Toast.makeText(UserDoctorDetailsActivity.this, "Please select appointment time first", Toast.LENGTH_SHORT).show();
        } else{
            if(NetworkService.isConnected(getApplicationContext())){
                if(appointment != null){
                    String ref = appointment.getUserId() + appointment.getDoctorId();
                    appointment = new AppointmentModel(appointment.getUserId(),
                            appointment.getUserName(), appointment.getUserEmail(), appointment.getDoctorId(),
                            appointment.getDoctorName(),appointment.getDoctorEmail(),appointment.getDoctorImg(),
                            appointment.getDoctorSpec(), appointmentDate,appointmentTime,
                            appointment.getStars(),appointment.getDistance(),appointment.getAboutDoctor());
                    FirebaseService.getFirebaseDatabase().getReference(Constant.APPOINTMENT)
                            .child(ref)
                            .setValue(appointment)
                            .addOnSuccessListener(s -> {
                                storeAppointmentsInStorage();
                                View view = LayoutInflater.from(UserDoctorDetailsActivity.this).inflate(R.layout.alert_dialog_appointment_success,null);
                                AlertDialog alertDialog = new MaterialAlertDialogBuilder(UserDoctorDetailsActivity.this).create();
                                alertDialog.setView(view);
                                alertDialog.show();
                                MaterialButton chatDoctor = view.findViewById(R.id.chat_doctor_btn);
                                chatDoctor.setOnClickListener(v -> {

                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(UserDoctorDetailsActivity.this, "Sorry, Couldn't make your appointment\nPlease try again later", Toast.LENGTH_SHORT).show();
                            });
                }else{
                    String ref = user.getId() + doctor.getId();
                    AppointmentModel appointment = new AppointmentModel(user.getId(),
                            user.getName(), user.getEmail(), doctor.getId(),
                            doctor.getName(),doctor.getEmail(),doctor.getProfileImgUri(),
                            doctor.getSpec(), appointmentDate,appointmentTime,
                            doctor.getStars(),doctor.getDistance(),doctor.getAbout());
                    FirebaseService.getFirebaseDatabase().getReference(Constant.APPOINTMENT)
                            .child(ref)
                            .setValue(appointment)
                            .addOnSuccessListener(s -> {
                                storeAppointmentsInStorage();
                                View view = LayoutInflater.from(UserDoctorDetailsActivity.this).inflate(R.layout.alert_dialog_appointment_success,null);
                                AlertDialog alertDialog = new MaterialAlertDialogBuilder(UserDoctorDetailsActivity.this).create();
                                alertDialog.setView(view);
                                alertDialog.show();
                                MaterialButton chatDoctor = view.findViewById(R.id.chat_doctor_btn);
                                chatDoctor.setOnClickListener(v -> {
                                    Intent intent0 = new Intent(UserDoctorDetailsActivity.this,UserDoctorChatActivity.class);
                                    intent0.putExtra(Constant.OBJECT,doctor);
                                    startActivity(intent0);
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(UserDoctorDetailsActivity.this, "Sorry, Couldn't make your appointment\nPlease try again later", Toast.LENGTH_SHORT).show();
                            });
                }
            }else{
                NetworkService.connectionFailed(getApplicationContext());
            }
        }
    }
    private void storeAppointmentsInStorage(){
        FirebaseService.getFirebaseDatabase().getReference(Constant.APPOINTMENT)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appointments = new ArrayList<>();
                        for(DataSnapshot dataSnapshot0 : snapshot.getChildren()){
                            AppointmentModel appointment = dataSnapshot0.getValue(AppointmentModel.class);
                            appointments.add(appointment);
                        }
                        SharedPreferences.Editor shared = getSharedPreferences(Constant.APPOINTMENT_LIST,0).edit();
                        String appointmentsTxt = gson.toJson(appointments);
                        shared.putString(Constant.APPOINTMENTS,appointmentsTxt);
                        shared.commit();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}