package com.artemohanjanyan.mobileschool;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class DescriptionActivity extends AppCompatActivity {

    private static final String TAG = DescriptionActivity.class.getSimpleName();

    public static final String ARTIST_EXTRA = "artist";

    @SuppressWarnings("FieldCanBeLocal")
    private ImageView cover;
    @SuppressWarnings("FieldCanBeLocal")
    private TextView genres, published, description;

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

        ApplicationContext.getInstance().getPicasso()
                .load(artist.bigCover)
                .into(cover);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.description_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.description_website) {
            // Open artist's web page.
            Log.d(TAG, "Open web page");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(artist.link));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
