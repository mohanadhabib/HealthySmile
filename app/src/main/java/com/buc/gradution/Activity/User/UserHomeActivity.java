package com.buc.gradution.Activity.User;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import com.buc.gradution.Fragment.User.UserAppointmentFragment;
import com.buc.gradution.Fragment.User.UserChatFragment;
import com.buc.gradution.Fragment.User.UserHomeFragment;
import com.buc.gradution.Fragment.User.UserProfileFragment;
import com.buc.gradution.Fragment.User.UserScanFragment;
import com.buc.gradution.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserHomeActivity extends AppCompatActivity {
    private FrameLayout frame;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        initComponents();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new UserHomeFragment()).commit();
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new UserHomeFragment()).commit();
            } else if (item.getItemId() == R.id.chat) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new UserChatFragment()).commit();
            } else if (item.getItemId() == R.id.scan) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new UserScanFragment()).commit();
            } else if (item.getItemId() == R.id.appointment) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new UserAppointmentFragment()).commit();
            } else if (item.getItemId() == R.id.profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new UserProfileFragment()).commit();
            }
            return true;
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
    private void initComponents(){
        frame = findViewById(R.id.frame);
        bottomNavigationView = findViewById(R.id.nav_bar);
    }
}