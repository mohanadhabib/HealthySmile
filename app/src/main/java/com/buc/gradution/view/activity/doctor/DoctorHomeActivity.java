package com.buc.gradution.view.activity.doctor;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.buc.gradution.adapter.doctor.DoctorHomeViewPagerAdapter;
import com.buc.gradution.R;
import com.buc.gradution.view.fragment.doctor.DoctorAppointmentFragment;
import com.buc.gradution.view.fragment.doctor.DoctorChatFragment;
import com.buc.gradution.view.fragment.doctor.DoctorProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class DoctorHomeActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private DoctorAppointmentFragment doctorAppointmentFragment;
    private DoctorChatFragment doctorChatFragment;
    private DoctorProfileFragment doctorProfileFragment;
    private ArrayList<Fragment> fragments;
    private DoctorHomeViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        initComponents();
        fragments = new ArrayList<>();
        doctorAppointmentFragment = new DoctorAppointmentFragment();
        doctorChatFragment = new DoctorChatFragment();
        doctorProfileFragment = new DoctorProfileFragment(viewPager,bottomNavigationView);
        fragments.add(doctorAppointmentFragment);
        fragments.add(doctorChatFragment);
        fragments.add(doctorProfileFragment);
        adapter = new DoctorHomeViewPagerAdapter(getSupportFragmentManager(),getLifecycle(),fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0,false);
        viewPager.setUserInputEnabled(false);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
             if (item.getItemId() == R.id.appointment) {
                 viewPager.setCurrentItem(0, false);
             } else if (item.getItemId() == R.id.chat) {
                viewPager.setCurrentItem(1,false);
             } else if (item.getItemId() == R.id.profile) {
                viewPager.setCurrentItem(2,false);
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
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.nav_bar);
    }
}