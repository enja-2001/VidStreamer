package com.enja.videostreamingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.enja.videostreamingapp.Callbacks.ResponseCallback;
import com.enja.videostreamingapp.Listeners.OnSwipeListener;
import com.enja.videostreamingapp.Models.CustomOutput;
import com.enja.videostreamingapp.Models.single_msg;
import com.enja.videostreamingapp.Network.Response;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    PlayerView playerView;
    ProgressBar progressBar;
    SimpleExoPlayer simpleExoPlayer;
    String userAgent;
    ProgressiveMediaSource.Factory factory;
    GestureDetector gestureDetector;
    ArrayList<single_msg> al;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initViews();
        initPlayer();
        initSwipeListener();
    }

    private void initViews(){
        playerView=findViewById(R.id.playerView);
        progressBar=findViewById(R.id.progressBar);
    }

    private void initPlayer() {
        simpleExoPlayer = new SimpleExoPlayer.Builder(MainActivity.this).build();
        simpleExoPlayer.getPlayWhenReady();

        simpleExoPlayer.addListener(new Player.EventListener(){
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == Player.STATE_BUFFERING)
                    progressBar.setVisibility(View.VISIBLE);

                else if(playbackState == Player.STATE_READY)
                    progressBar.setVisibility(View.GONE);

                else if(playbackState == Player.STATE_ENDED){
                    simpleExoPlayer.seekTo(0);
                    simpleExoPlayer.getPlayWhenReady();
                }
            }
        });

        userAgent = Util.getUserAgent(playerView.getContext(),playerView.getContext().getString(R.string.app_name));
        factory = new ProgressiveMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent),
                new DefaultExtractorsFactory());

        playerView.setPlayer(simpleExoPlayer);
        playerView.setOnTouchListener(MainActivity.this);
        playerView.setKeepScreenOn(true);
        playerView.hideController();


        getResponse();
    }

    private void preparePlayer(int pos){

        if(pos<0 || pos>=al.size())
            pos=0;

        Uri videoUri = Uri.parse(al.get(pos).getVideo());
        MediaSource mediaSource = factory.createMediaSource(videoUri);
        simpleExoPlayer.prepare(mediaSource);
    }

    private void getResponse(){
        position=0;
        Response ob=new Response();

        ob.getResponse(customOutput -> {
            al = customOutput.getMsg();
            preparePlayer(position);
            simpleExoPlayer.getPlayWhenReady();
        });
    }

    private void initSwipeListener() {
        gestureDetector = new GestureDetector(this,new OnSwipeListener(){
            @Override
            public boolean onSwipe(Direction direction) {
                if(direction == Direction.up){
                    Log.d("swipe","up");
                    position++;
                }
                else if(direction == Direction.down){
                    Log.d("swipe","down");
                    position--;
                }
                preparePlayer(position);
                return true;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.getPlaybackState();
    }
}