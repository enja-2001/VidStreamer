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

import com.enja.videostreamingapp.Listeners.OnSwipeListener;
import com.enja.videostreamingapp.Models.CacheSingleton;
import com.enja.videostreamingapp.Models.single_msg;
import com.enja.videostreamingapp.Network.Response;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheWriter;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    PlayerView playerView;
    ProgressBar progressBar;

    String userAgent;
    SimpleExoPlayer simpleExoPlayer;
    ProgressiveMediaSource.Factory factory;
    DefaultDataSourceFactory defaultDataSourceFactory;
    SimpleCache simpleCache;

    GestureDetector gestureDetector;

    int position;
    private final long cacheSize = 1 * 1024 * 1024;     // cacheSize = 1 MB

    ArrayList<single_msg> al;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initViews();
        initPlayer();
        initMediaSources();
        initSwipeListener();
        getResponse();
    }

    private void initViews() {
        playerView = findViewById(R.id.playerView);
        playerView.setOnTouchListener(MainActivity.this);
        playerView.setKeepScreenOn(true);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void initPlayer() {
        simpleExoPlayer = new SimpleExoPlayer.Builder(MainActivity.this).build();
        simpleExoPlayer.getPlayWhenReady();
        Player.Listener playerListener = getPlayerListener();
        simpleExoPlayer.addListener(playerListener);

        playerView.setPlayer(simpleExoPlayer);
    }

    private void initMediaSources(){
        //initialize userAgent
        userAgent = Util.getUserAgent(playerView.getContext(),playerView.getContext().getString(R.string.app_name));

        //initialize defaultHttpDataSourceFactory
        DefaultHttpDataSource.Factory defaultHttpDataSourceFactory = new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true);
        defaultHttpDataSourceFactory.setUserAgent(userAgent);

        //initialize LRU Cache Evictor
        LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(cacheSize);

        //get SimpleCache instance
        simpleCache = CacheSingleton.getInstance(this,leastRecentlyUsedCacheEvictor);

        //initialize CacheDataSource.Factory
        CacheDataSource.Factory cacheDataSourceFactory = new CacheDataSource.Factory();
        cacheDataSourceFactory.setCache(simpleCache);

        // wrap the defaultHttpDataSourceFactory within cacheDataSourceFactory
        cacheDataSourceFactory.setUpstreamDataSourceFactory(defaultHttpDataSourceFactory);

        //wrap the cacheDataSourceFactory within defaultDataSourceFactory
        defaultDataSourceFactory = new DefaultDataSourceFactory(this,cacheDataSourceFactory);

        //initialize ProgressiveMediaSource.Factory
        factory = new ProgressiveMediaSource.Factory(defaultDataSourceFactory, new DefaultExtractorsFactory());
    }

    private void getResponse(){
        position=0;
        Response ob=new Response();

        ob.getResponse(customOutput -> {
            al = customOutput.getMsg();
            preparePlayer(position);
        });
    }

    private void preparePlayer(int pos){
        //for playing the current video
        pos = pos<0 || pos>=al.size() ? 0 : pos;

        Uri videoUri = Uri.parse(al.get(pos).getVideo());
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        MediaSource mediaSource = factory.createMediaSource(mediaItem);
        simpleExoPlayer.setMediaSource(mediaSource);

        //for caching the next video
        int nextPos = pos+1>=al.size() ? 0 : pos+1;
        createCache(nextPos);
    }

    private void createCache(int pos){
        Uri videoUri = Uri.parse(al.get(pos).getVideo());

        DataSpec dataSpec = new DataSpec(videoUri,0,cacheSize);
        CacheDataSink cacheDataSink = new CacheDataSink(simpleCache,cacheSize);

        CacheDataSource cacheDataSource = new CacheDataSource(simpleCache,
                defaultDataSourceFactory.createDataSource(),
                new FileDataSource(),
                cacheDataSink,
                CacheDataSource.FLAG_BLOCK_ON_CACHE |  CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                null);

        CacheWriter cacheWriter = new CacheWriter(cacheDataSource,dataSpec,null,null);

        startBackgroundCache(cacheWriter);
    }

    private void startBackgroundCache(CacheWriter cacheWriter) {
        Runnable runnable = () -> {
            //background thread logic here
            try {
                cacheWriter.cache();
                Log.d("cacheStatus", "DONE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }

    private Player.Listener getPlayerListener(){
        return new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if(state == Player.STATE_BUFFERING)
                    progressBar.setVisibility(View.VISIBLE);

                else if(state == Player.STATE_READY)
                    progressBar.setVisibility(View.GONE);

                else if(state == Player.STATE_ENDED) {
                    simpleExoPlayer.seekTo(0);
                    simpleExoPlayer.getPlayWhenReady();
                    playerView.hideController();
                }
            }
        };
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