package com.buc.gradution.View.Activity.User;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.buc.gradution.Adapter.User.UserHomeViewPagerAdapter;
import com.buc.gradution.View.Fragment.User.UserAppointmentFragment;
import com.buc.gradution.View.Fragment.User.UserChatFragment;
import com.buc.gradution.View.Fragment.User.UserHomeFragment;
import com.buc.gradution.View.Fragment.User.UserProfileFragment;
import com.buc.gradution.View.Fragment.User.UserScanFragment;
import com.buc.gradution.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;

public class UserHomeActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private UserHomeFragment userHomeFragment;
    private UserChatFragment userChatFragment;
    private UserScanFragment userScanFragment;
    private UserAppointmentFragment userAppointmentFragment;
    private UserProfileFragment userProfileFragment;
    private ArrayList<Fragment> fragments;
    private UserHomeViewPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        initComponents();
        fragments = new ArrayList<>();
        userHomeFragment = new UserHomeFragment();
        userChatFragment = new UserChatFragment();
        userScanFragment = new UserScanFragment();
        userAppointmentFragment = new UserAppointmentFragment();
        userProfileFragment =  new UserProfileFragment(viewPager,bottomNavigationView);
        fragments.add(userHomeFragment);
        fragments.add(userChatFragment);
        fragments.add(userScanFragment);
        fragments.add(userAppointmentFragment);
        fragments.add(userProfileFragment);
        adapter = new UserHomeViewPagerAdapter(getSupportFragmentManager(),getLifecycle(),fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0,false);
        viewPager.setUserInputEnabled(false);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
               viewPager.setCurrentItem(0,false);
            } else if (item.getItemId() == R.id.chat) {
               viewPager.setCurrentItem(1,false);
            } else if (item.getItemId() == R.id.scan) {
                viewPager.setCurrentItem(2,false);
            } else if (item.getItemId() == R.id.appointment) {
                viewPager.setCurrentItem(3,false);
            } else if (item.getItemId() == R.id.profile) {
                viewPager.setCurrentItem(4,false);
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
