package com.enja.videostreamingapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.enja.videostreamingapp.Models.single_msg;
import com.enja.videostreamingapp.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheWriter;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.util.ArrayList;

public class VideoViewPagerAdapter extends RecyclerView.Adapter<VideoViewPagerAdapter.MyViewHolder>{

    ArrayList<single_msg> al;
    Context context;
    SimpleExoPlayer simpleExoPlayer;
    ProgressiveMediaSource.Factory factory;
    DefaultDataSourceFactory defaultDataSourceFactory;
    SimpleCache simpleCache;
    private final long cacheSize = 1 * 1024 * 1024;     // cacheSize = 1 MB

    public static class MyViewHolder extends RecyclerView.ViewHolder {  //view holder class

        public PlayerView playerView;
        public ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView=itemView.findViewById(R.id.playerView);
            playerView.setKeepScreenOn(true);

            progressBar=itemView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
    }


    public VideoViewPagerAdapter(ArrayList<single_msg> al, Context context, ProgressiveMediaSource.Factory factory, DefaultDataSourceFactory defaultDataSourceFactory, SimpleCache simpleCache){
        this.al=al;
        this.context=context;
        this.factory = factory;
        this.defaultDataSourceFactory=defaultDataSourceFactory;
        this.simpleCache=simpleCache;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_video,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        initPlayer(holder);
        holder.playerView.setPlayer(simpleExoPlayer);
        preparePlayer(position);
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    private void initPlayer(MyViewHolder holder) {
        simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();
        simpleExoPlayer.getPlayWhenReady();
        Player.Listener playerListener = getPlayerListener(holder);
        simpleExoPlayer.addListener(playerListener);
    }

    private void preparePlayer(int pos){
        //for playing the current video
        pos = pos<0 || pos>=al.size() ? 0 : pos;

        Uri videoUri = Uri.parse(al.get(pos).getVideo());
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        MediaSource mediaSource = factory.createMediaSource(mediaItem);
        simpleExoPlayer.setMediaSource(mediaSource);

//        for caching the next video
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

    private Player.Listener getPlayerListener(MyViewHolder holder){
        return new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if(state == Player.STATE_BUFFERING)
                    holder.progressBar.setVisibility(View.VISIBLE);

                else if(state == Player.STATE_READY)
                    holder.progressBar.setVisibility(View.GONE);

                else if(state == Player.STATE_ENDED) {
                    simpleExoPlayer.seekTo(0);
                    simpleExoPlayer.getPlayWhenReady();
                    holder.playerView.hideController();
                }
            }
        };
    }
}