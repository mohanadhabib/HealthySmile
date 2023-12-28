package com.buc.gradution.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.buc.gradution.R;
import com.google.android.material.button.MaterialButton;

public class OnboardingTwoActivity extends AppCompatActivity {

    private TextView skipBtn;
    private MaterialButton nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_two);
        initComponents();
        skipBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingTwoActivity.this, OnboardingFourActivity.class);
            startActivity(intent);
            finish();
        });
        nextBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingTwoActivity.this, OnboardingThreeActivity.class);
            startActivity(intent);
        });
    }
    private void initComponents(){
        skipBtn = findViewById(R.id.skip_button);
        nextBtn = findViewById(R.id.next);
    }
}