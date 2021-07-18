package com.enja.videostreamingapp.Models;

import android.content.Context;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public class CacheSingleton {

    private static SimpleCache simpleCache;

    public static SimpleCache getInstance(Context context, LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor) {
        if (simpleCache == null) {

            simpleCache = new SimpleCache(new File(context.getCacheDir(), context.getPackageName()),
                    leastRecentlyUsedCacheEvictor,
                    new ExoDatabaseProvider(context));
        }

        return simpleCache;
    }
}
