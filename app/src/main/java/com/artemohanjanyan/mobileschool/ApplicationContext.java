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
    private static ApplicationContext instance;
    private Picasso picasso;

    public ApplicationContext() {
        instance = this;
    }

    public static ApplicationContext getInstance() {
        return instance;
    }

    /**
     * Returns application-global {@link Picasso} instance. <br>
     * LRU memory cache is set up to be ~50% of the available application RAM.
     */
    public Picasso getPicasso() {
        if (picasso == null) {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            int memoryClass = am.getMemoryClass();
            // Target ~50% of the available heap.
            Cache cache = new LruCache(1024 * 1024 * memoryClass / 2);
            picasso = new Picasso.Builder(this).memoryCache(cache).build();
            if (BuildConfig.DEBUG) {
                picasso.setIndicatorsEnabled(true);
            }
        }

        return picasso;
    }
}
