package com.artemohanjanyan.mobileschool;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DescriptionActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

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

        // UI components
        cover = (ImageView) findViewById(R.id.description_cover);
        genres = (TextView) findViewById(R.id.description_genres);
        published = (TextView) findViewById(R.id.description_published);
        description = (TextView) findViewById(R.id.description_description);

        // Artist info
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
        switch (item.getItemId()) {
            case R.id.description_website: {
                // Open artist's web page.
                Log.d(TAG, "Open web page");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(artist.link));
                startActivity(intent);
                return true;
            }

            case R.id.description_share: {
                Log.d(TAG, "description_share");
                Bundle bundle = new Bundle();
                bundle.putParcelable(ShareLoader.SHARE_ARTIST_EXTRA, artist);

                Toast.makeText(getApplicationContext(),
                        getString(R.string.wait_please), Toast.LENGTH_SHORT).show();
                getLoaderManager().initLoader(0, bundle, this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new ShareLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(TAG, "onLoadFinished");

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.share_text, artist.name, artist.link));

        if (data != null) {
            Uri uri = Uri.parse(data);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("image/*");
        } else {
            intent.setType("text/plain");
        }

        startActivity(intent);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // No data is kept.
    }
}
