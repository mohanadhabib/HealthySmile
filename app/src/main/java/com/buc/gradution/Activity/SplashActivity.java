package com.buc.gradution.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.buc.gradution.Activity.Doctor.DoctorHomeActivity;
import com.buc.gradution.Activity.User.UserHomeActivity;
import com.buc.gradution.Constant.Constant;
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
                String type = getSharedPreferences(Constant.PREF_NAME,0).getString(Constant.TYPE,"");
                if(type.equals(Constant.PATIENT_TYPE)){
                    FirebaseService.getFirebaseDatabase().getReference(Constant.PATIENT_TYPE).get().addOnSuccessListener(
                            dataSnapshot -> {
                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                    UserModel userModel = data.getValue(UserModel.class);
                                    if(userModel.getId().equals(FirebaseService.getFirebaseAuth().getCurrentUser().getUid())){
                                            Intent intent = new Intent(SplashActivity.this, UserHomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                    }
                                }
                            }
                    ).addOnFailureListener(
                            e -> Toast.makeText(SplashActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
                else if(type.equals(Constant.DOCTOR_TYPE)){
                    FirebaseService.getFirebaseDatabase().getReference(Constant.DOCTOR_TYPE).get().addOnSuccessListener(
                            dataSnapshot -> {
                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                    UserModel userModel = data.getValue(UserModel.class);
                                    if(userModel.getId().equals(FirebaseService.getFirebaseAuth().getCurrentUser().getUid())){
                                        Intent intent = new Intent(SplashActivity.this, DoctorHomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                    ).addOnFailureListener(
                            e -> Toast.makeText(SplashActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
            else {
                Intent intent = new Intent(SplashActivity.this, OnboardingOneActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}