package com.buc.gradution.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.buc.gradution.view.activity.doctor.DoctorHomeActivity;
import com.buc.gradution.view.activity.user.UserHomeActivity;
import com.buc.gradution.constant.Constant;
import com.buc.gradution.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.myLooper()).post(() -> {
            String type = getSharedPreferences(Constant.USER_TYPE,0).getString(Constant.TYPE,"");
            if(type.equals(Constant.PATIENT_TYPE)){
                Intent intent = new Intent(SplashActivity.this, UserHomeActivity.class);
                startActivity(intent);
                finish();
            }else if (type.equals(Constant.DOCTOR_TYPE)){
                Intent intent = new Intent(SplashActivity.this, DoctorHomeActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(SplashActivity.this, OnboardingOneActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}