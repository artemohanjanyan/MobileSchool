package com.artemohanjanyan.mobileschool;

import android.app.ActivityManager;
import android.app.Application;

import com.squareup.picasso.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

/**
 * Singleton class, which holds {@link Picasso} instance.
 */
public class ApplicationContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Picasso.setSingletonInstance(buildPicasso());
    }

    /**
     * Creates new instance of {@link Picasso}. <br>
     * LRU memory cache is set up to be ~50% of the available application RAM.
     */
    private Picasso buildPicasso() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        // Target ~50% of the available heap.
        Cache cache = new LruCache(1024 * 1024 * memoryClass / 2);
        return new Picasso.Builder(this).memoryCache(cache).build();
    }
}
