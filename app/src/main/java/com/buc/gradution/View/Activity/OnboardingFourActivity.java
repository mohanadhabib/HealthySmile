package com.buc.gradution.View.Activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.os.LocaleListCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.buc.gradution.Constant.Constant;
import com.buc.gradution.R;
import com.buc.gradution.Service.NetworkService;
import com.buc.gradution.View.Activity.User.UserGuideActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class OnboardingFourActivity extends AppCompatActivity {

    private String[] languages;
    private ImageView userGuide;
    private TextInputLayout languageLayout;
    private MaterialAutoCompleteTextView autoCompleteTextView;
    private MaterialSwitch themeSwitch;
    private MaterialButton loginBtn;
    private MaterialButton signupBtn;
    private Boolean isDarkTheme;
    private SharedPreferences.Editor themeEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_four);
        initComponents();
        languages = new String[]{getResources().getString(R.string.english),getResources().getString(R.string.arabic)};
        isDarkTheme = getSharedPreferences(Constant.THEME_SHARED_PREFERENCES,0).getBoolean(Constant.CURRENT_THEME,false);
        if (isDarkTheme){
            themeSwitch.setThumbIconDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_dark_mode));
        }else{
            themeSwitch.setThumbIconDrawable(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_light_mode));
        }
        if(Locale.getDefault().getLanguage().contentEquals("en")){
            autoCompleteTextView.setText(getResources().getString(R.string.english));
        }else{
            autoCompleteTextView.setText(getResources().getString(R.string.arabic));
        }
        userGuide.setOnClickListener(v ->{
            if(NetworkService.isConnected(getApplicationContext())){
                boolean isEnglish = Locale.getDefault().getLanguage().contentEquals("en");
                Intent intent = new Intent(getApplicationContext(), UserGuideActivity.class);
                if(isEnglish){
                    intent.putExtra("url","https://drive.google.com/file/d/117O8OjdD4kAtRgl9EKE1ydzNKdQE49_b/view?usp=drivesdk");
                }
                else {
                    intent.putExtra("url","https://drive.google.com/file/d/15y9mimifNeyOhWzold7kcXYSGb533s0E/view?usp=drivesdk");
                }
                startActivity(intent);
            }
            else{
                NetworkService.connectionFailed(getApplicationContext());
            }
        });
        autoCompleteTextView.setSimpleItems(languages);
        autoCompleteTextView.dismissDropDown();
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            if(position == 0){
                LocaleListCompat locale = LocaleListCompat.forLanguageTags("en");
                AppCompatDelegate.setApplicationLocales(locale);
            }
            else if(position == 1){
                LocaleListCompat locale = LocaleListCompat.forLanguageTags("ar");
                AppCompatDelegate.setApplicationLocales(locale);
            }
        });
        themeSwitch.setOnClickListener(v -> {
            themeEditor = getSharedPreferences(Constant.THEME_SHARED_PREFERENCES,0).edit();
            isDarkTheme = !isDarkTheme;
            themeEditor.putBoolean(Constant.CURRENT_THEME,isDarkTheme);
            themeEditor.commit();
            themeSwitch.setChecked(isDarkTheme);
            if(isDarkTheme){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        themeSwitch.setChecked(isDarkTheme);
    }

    private void initComponents(){
        userGuide = findViewById(R.id.user_guide);
        themeSwitch = findViewById(R.id.theme);
        loginBtn = findViewById(R.id.login);
        signupBtn = findViewById(R.id.signUp);
        languageLayout = findViewById(R.id.language_layout);
        autoCompleteTextView = (MaterialAutoCompleteTextView) languageLayout.getEditText();
    }
}