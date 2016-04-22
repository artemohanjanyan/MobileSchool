package com.artemohanjanyan.mobileschool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DescriptionActivity extends AppCompatActivity {
    public static final String ARTIST_EXTRA = "artist";

    private ImageView cover;
    @SuppressWarnings("FieldCanBeLocal")
    private TextView genres, published, description;

    @SuppressWarnings("FieldCanBeLocal")
    private Artist artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        cover = (ImageView) findViewById(R.id.description_cover);
        genres = (TextView) findViewById(R.id.description_genres);
        published = (TextView) findViewById(R.id.description_published);
        description = (TextView) findViewById(R.id.description_description);

        Intent intent = getIntent();
        artist = intent.getParcelableExtra(ARTIST_EXTRA);

        setTitle(artist.name);
        genres.setText(artist.getGenres());
        published.setText(artist.getPublished(this));
        description.setText(artist.description);

        Picasso.with(this)
                .setIndicatorsEnabled(true);
        Picasso.with(cover.getContext())
                .load(artist.bigCover)
                .into(cover);
    }
}
