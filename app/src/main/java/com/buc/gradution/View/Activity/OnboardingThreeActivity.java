package com.buc.gradution.View.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.buc.gradution.R;
import com.google.android.material.button.MaterialButton;

public class OnboardingThreeActivity extends AppCompatActivity {

    private TextView skipBtn;
    private MaterialButton nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_three);
        initComponents();
        skipBtn.setOnClickListener(v -> navigateToStart());
        nextBtn.setOnClickListener(v -> navigateToStart());
    }
    private void initComponents(){
        skipBtn = findViewById(R.id.skip_button);
        nextBtn = findViewById(R.id.next);
    }
    private void navigateToStart(){
        Intent intent = new Intent(OnboardingThreeActivity.this , OnboardingFourActivity.class);
        startActivity(intent);
        finish();
    }
}