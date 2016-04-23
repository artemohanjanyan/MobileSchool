package com.artemohanjanyan.mobileschool;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

public class ListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Artist>> {

    private static final String TAG = ListActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private Adapter adapter;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // UI components
        progressBar = (ProgressBar) findViewById(R.id.list_progress_bar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this,
                getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT ? 1 : 2));
        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

        // Download JSON
        getLoaderManager().initLoader(0, null, this);
        // restartLoader
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Loader<List<Artist>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "loader created");
        return new InfoLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Artist>> loader, List<Artist> data) {
        if (data != null && data.size() != 0) {
            progressBar.setVisibility(View.INVISIBLE);
            adapter.setArtists(data);
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Artist>> loader) {

    }
}
