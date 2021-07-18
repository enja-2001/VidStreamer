package com.enja.videostreamingapp.Views;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.enja.videostreamingapp.Adapters.VideoViewPagerAdapter2;
import com.enja.videostreamingapp.Models.single_msg;
import com.enja.videostreamingapp.Network.Response;

import com.enja.videostreamingapp.R;

import java.util.ArrayList;

public class VideoRecyclerView extends FragmentActivity {

    ViewPager2 viewPager;
    ArrayList<single_msg> al;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recycler_view);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initViews();
        getResponse();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(1);     //set page off screen page limit=1 on either side of the current page
    }

    private void getResponse(){
        position=0;
        Response ob=new Response();

        ob.getResponse(customOutput -> {
            al = customOutput.getMsg();
            VideoViewPagerAdapter2 videoViewPagerAdapter = new VideoViewPagerAdapter2(this,al);
            viewPager.setAdapter(videoViewPagerAdapter);
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}