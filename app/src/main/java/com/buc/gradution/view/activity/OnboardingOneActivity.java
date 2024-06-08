package com.buc.gradution.view.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.buc.gradution.R;
import com.google.android.material.button.MaterialButton;

public class OnboardingOneActivity extends AppCompatActivity {

    private TextView skipBtn;
    private MaterialButton nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_one);
        initComponents();
        skipBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingOneActivity.this, OnboardingFourActivity.class);
            startActivity(intent);
            finish();
        });
        nextBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingOneActivity.this, OnboardingTwoActivity.class);
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
        skipBtn = findViewById(R.id.skip_button);
        nextBtn = findViewById(R.id.next);
    }
}