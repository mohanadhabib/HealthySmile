package com.buc.gradution.View.Activity.User;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.buc.gradution.Constant.Constant;
import com.buc.gradution.Model.DoctorModel;
import com.buc.gradution.R;

public class UserDoctorChatActivity extends AppCompatActivity {
    private DoctorModel doctor;
    private ImageView back,phone,video;
    private TextView doctorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_doctor_chat);
        initComponents();
        doctor = (DoctorModel) getIntent().getSerializableExtra(Constant.OBJECT);
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(UserDoctorChatActivity.this, UserHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        back.setOnClickListener(v ->{
            Intent intent = new Intent(UserDoctorChatActivity.this, UserHomeActivity.class);
            startActivity(intent);
            finish();
        });
        doctorName.setText(doctor.getName());
    }
    private void initComponents(){
        back = findViewById(R.id.back);
        doctorName = findViewById(R.id.doctor_name);
        video = findViewById(R.id.video_call);
        phone = findViewById(R.id.phone_call);
    }
}