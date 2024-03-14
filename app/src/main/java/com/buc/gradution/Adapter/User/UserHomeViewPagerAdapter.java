package com.buc.gradution.Adapter.User;

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
    public void removeFragment(int pos){
        fragments.remove(pos);
        notifyItemChanged(pos);
    }
    public void addFragment(Fragment fragment , int pos){
        fragments.add(pos,fragment);
        notifyItemChanged(pos);
    }

    @Override
    public long getItemId(int position) {
        return fragments.get(position).hashCode();
    }

    @Override
    public boolean containsItem(long itemId) {
        boolean res = false;
        for (Fragment f :fragments){
            if (f.hashCode() == itemId) {
                res = true;
                break;
            }
        }
        return res;
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