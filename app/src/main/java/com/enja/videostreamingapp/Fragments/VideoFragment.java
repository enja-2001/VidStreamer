package com.enja.videostreamingapp.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.enja.videostreamingapp.Adapters.VideoViewPagerAdapter;
import com.enja.videostreamingapp.Listeners.OnSwipeListener;
import com.enja.videostreamingapp.MainActivity;
import com.enja.videostreamingapp.Models.CacheSingleton;
import com.enja.videostreamingapp.Models.single_msg;
import com.enja.videostreamingapp.Network.Response;
import com.enja.videostreamingapp.R;
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

public class VideoFragment extends Fragment {

    PlayerView playerView;
    ProgressBar progressBar;

    String userAgent;
    SimpleExoPlayer simpleExoPlayer;
    ProgressiveMediaSource.Factory factory;
    DefaultDataSourceFactory defaultDataSourceFactory;
    SimpleCache simpleCache;

    ArrayList<single_msg> al;

    int position;
    private final long cacheSize = 1 * 1024 * 1024;     // cacheSize = 1 MB

    //create instance of fragment
    public static VideoFragment newInstance( ArrayList<single_msg> al,int position) {
        VideoFragment fragment = new VideoFragment();

        Bundle args = new Bundle();
        args.putSerializable("ArrayList_single_msg", al);
        args.putInt("Position", position);
        fragment.setArguments(args);

        return fragment;
    }

    // initialize instance variables from the bundle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        al=(ArrayList<single_msg>)getArguments().getSerializable("ArrayList_single_msg");
        position=getArguments().getInt("Position");
    }

    //inflate the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        initViews(view);
        initPlayer();
        initMediaSources();
        preparePlayer(position);

        return view;
    }

    private void initViews(View view) {
        playerView = view.findViewById(R.id.playerView);
        playerView.setKeepScreenOn(true);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void initPlayer() {
        simpleExoPlayer = new SimpleExoPlayer.Builder(getContext()).build();
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
        simpleCache = CacheSingleton.getInstance(getContext(),leastRecentlyUsedCacheEvictor);

        //initialize CacheDataSource.Factory
        CacheDataSource.Factory cacheDataSourceFactory = new CacheDataSource.Factory();
        cacheDataSourceFactory.setCache(simpleCache);

        // wrap the defaultHttpDataSourceFactory within cacheDataSourceFactory
        cacheDataSourceFactory.setUpstreamDataSourceFactory(defaultHttpDataSourceFactory);

        //wrap the cacheDataSourceFactory within defaultDataSourceFactory
        defaultDataSourceFactory = new DefaultDataSourceFactory(getContext(),cacheDataSourceFactory);

        //initialize ProgressiveMediaSource.Factory
        factory = new ProgressiveMediaSource.Factory(defaultDataSourceFactory, new DefaultExtractorsFactory());
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
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.getPlaybackState();
    }

    @Override
    public void onResume() {
        super.onResume();
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.getPlaybackState();
    }
}