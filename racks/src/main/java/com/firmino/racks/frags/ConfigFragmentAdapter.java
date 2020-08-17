package com.firmino.racks.frags;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConfigFragmentAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragments = new ArrayList<>();

    public ConfigFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
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

    public void add(Fragment fragment) {
        this.fragments.add(fragment);
    }


    /*
    public ConfigFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {

    }
         */
}
