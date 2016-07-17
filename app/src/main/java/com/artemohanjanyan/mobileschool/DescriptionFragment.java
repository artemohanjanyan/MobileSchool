package com.artemohanjanyan.mobileschool;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DescriptionFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = DescriptionFragment.class.getSimpleName();

    static final String ARTIST_EXTRA = "artist";

    @SuppressWarnings("FieldCanBeLocal")
    private ImageView cover;
    @SuppressWarnings("FieldCanBeLocal")
    private TextView genres, published, description;

    private Artist artist;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View frameLayout = inflater.inflate(R.layout.fragment_description, container, false);

        // UI components
        cover = (ImageView) frameLayout.findViewById(R.id.description_cover);
        genres = (TextView) frameLayout.findViewById(R.id.description_genres);
        published = (TextView) frameLayout.findViewById(R.id.description_published);
        description = (TextView) frameLayout.findViewById(R.id.description_description);

        // Artist info
        artist = getArguments().getParcelable(ARTIST_EXTRA);

        getActivity().setTitle(artist.name);
        genres.setText(artist.getGenres());
        published.setText(artist.getPublished(getActivity()));
        description.setText(artist.description);

        ApplicationContext.getInstance().getPicasso()
                .load(artist.bigCover)
                .into(cover);

        return frameLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.description_menu, menu);
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

                Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.wait_please), Toast.LENGTH_SHORT).show();
                getLoaderManager().initLoader(0, bundle, this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public android.support.v4.content.Loader<String> onCreateLoader(int id, Bundle args) {
        return new ShareLoader(getActivity(), args);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String data) {
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
    public void onLoaderReset(android.support.v4.content.Loader<String> loader) {
        // No data is kept.
    }
}