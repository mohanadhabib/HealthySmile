package com.buc.gradution.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class UserHomeViewPagerAdapter extends FragmentStateAdapter{
    private final ArrayList<Fragment> fragments;

    public UserHomeViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<Fragment> fragments) {
        super(fragmentManager, lifecycle);
        this.fragments = fragments;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }
    @Override
    public int getItemCount() {
        return fragments.size();
    }
}