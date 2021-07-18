package com.enja.videostreamingapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.enja.videostreamingapp.Fragments.VideoFragment;
import com.enja.videostreamingapp.Models.single_msg;

import java.util.ArrayList;

public class VideoViewPagerAdapter2 extends FragmentStateAdapter {

    ArrayList<single_msg> al;

    public VideoViewPagerAdapter2(@NonNull FragmentActivity fragmentActivity, ArrayList<single_msg> al) {
        super(fragmentActivity);
        this.al=al;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        VideoFragment videoFragment = VideoFragment.newInstance(al,position);
        return videoFragment;
    }

    @Override
    public int getItemCount() {
        return al.size();
    }
}
