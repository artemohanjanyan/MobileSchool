package com.artemohanjanyan.mobileschool;

import com.squareup.picasso.Picasso;

public class ApplicationContext extends android.app.Application {
    private static ApplicationContext instance;
    private Picasso picasso;

    public ApplicationContext() {
        instance = this;
    }

    public static ApplicationContext getInstance() {
        return instance;
    }

    public Picasso getPicasso() {
        if (picasso == null) {
            picasso = Picasso.with(this);
            picasso.setIndicatorsEnabled(true);
        }

        return picasso;
    }
}
