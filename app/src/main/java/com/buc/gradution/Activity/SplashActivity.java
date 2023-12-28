package com.buc.gradution.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.buc.gradution.Activity.Doctor.DoctorHomeActivity;
import com.buc.gradution.Activity.User.UserHomeActivity;
import com.buc.gradution.Model.UserModel;
import com.buc.gradution.R;
import com.buc.gradution.Service.FirebaseService;
import com.google.firebase.database.DataSnapshot;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.myLooper()).post(() -> {
            if(FirebaseService.getFirebaseAuth().getCurrentUser() != null){
                FirebaseService.getFirebaseDatabase().getReference("User").get().addOnSuccessListener(
                        dataSnapshot -> {
                            for(DataSnapshot data : dataSnapshot.getChildren()){
                                    UserModel userModel = data.getValue(UserModel.class);
                                    if(userModel.getId().equals(FirebaseService.getFirebaseAuth().getCurrentUser().getUid())){
                                        if(userModel.getType().equals("Patient")){
                                            Intent intent = new Intent(SplashActivity.this, UserHomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else if(userModel.getType().equals("Doctor")){
                                            Intent intent = new Intent(SplashActivity.this, DoctorHomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                        }
                ).addOnFailureListener(
                        e -> Toast.makeText(SplashActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
                );
            }
            else {
                Intent intent = new Intent(SplashActivity.this, OnboardingOneActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}