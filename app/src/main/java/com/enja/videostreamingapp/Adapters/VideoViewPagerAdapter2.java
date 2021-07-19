package com.enja.videostreamingapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.enja.videostreamingapp.Fragments.VideoFragment;
import com.enja.videostreamingapp.Models.single_msg;

import java.util.ArrayList;

public class VideoViewPagerAdapter2 extends FragmentStateAdapter {

    ArrayList<single_msg> al;
    ViewPager2 viewPager;

    public VideoViewPagerAdapter2(@NonNull FragmentActivity fragmentActivity, ArrayList<single_msg> al) {
        super(fragmentActivity);
        this.al=al;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return VideoFragment.newInstance(al,position);
    }

    @Override
    public int getItemCount() {
        return al.size();
    }
}
