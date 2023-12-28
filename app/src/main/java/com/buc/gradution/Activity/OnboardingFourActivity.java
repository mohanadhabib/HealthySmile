package com.buc.gradution.Activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.buc.gradution.R;
import com.google.android.material.button.MaterialButton;

public class OnboardingFourActivity extends AppCompatActivity {

    private MaterialButton loginBtn;
    private MaterialButton signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_four);
        initComponents();
        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingFourActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingFourActivity.this, SignupActivity.class);
            startActivity(intent);
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(getApplicationContext(), "Couldn't get back", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initComponents(){
        loginBtn = findViewById(R.id.login);
        signupBtn = findViewById(R.id.signUp);
    }
}