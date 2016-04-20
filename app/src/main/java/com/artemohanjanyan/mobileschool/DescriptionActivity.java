package com.artemohanjanyan.mobileschool;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;

public class DescriptionActivity extends AppCompatActivity implements DownloadCallback<Bitmap> {
    public static final String NAME_EXTRA = "name";
    public static final String COVER_EXTRA = "cover";
    public static final String GENRES_EXTRA = "genres";
    public static final String PUBLISHED_EXTRA = "published";
    public static final String DESCRIPTION_EXTRA = "description";

    private DownloadImageTask downloadCoverTask;

    private ImageView cover;
    @SuppressWarnings("FieldCanBeLocal")
    private TextView genres, published, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        cover = (ImageView) findViewById(R.id.description_cover);
        genres = (TextView) findViewById(R.id.description_genres);
        published = (TextView) findViewById(R.id.description_published);
        description = (TextView) findViewById(R.id.description_description);

        Intent intent = getIntent();

        setTitle(intent.getStringExtra(NAME_EXTRA));
        genres.setText(intent.getStringExtra(GENRES_EXTRA));
        published.setText(intent.getStringExtra(PUBLISHED_EXTRA));
        description.setText(intent.getStringExtra(DESCRIPTION_EXTRA));

        URL url = (URL) intent.getSerializableExtra(COVER_EXTRA);
        downloadCoverTask = new DownloadImageTask(this);
        downloadCoverTask.execute(url);
    }

    @Override
    public void onDownloaded(Bitmap bitmap) {
        if (bitmap != null) {
            cover.setImageBitmap(bitmap);
            AnimatorSet set = (AnimatorSet) AnimatorInflater
                    .loadAnimator(this, R.animator.cover_animation);
            set.setTarget(cover);
            set.start();
        }
    }
}
