package com.artemohanjanyan.mobileschool;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.List;

public class ListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Artist>> {

    private static final String TAG = ListActivity.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // UI components
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.list_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(getOnRefreshListener());

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
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.list_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            swipeRefreshLayout.setRefreshing(true);

            Bundle bundle = new Bundle();
            bundle.putString(InfoLoader.SEARCH_EXTRA, query);
            getLoaderManager().restartLoader(0, bundle, ListActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Loader<List<Artist>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "loader created");
        return new InfoLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Artist>> loader, List<Artist> data) {
        swipeRefreshLayout.setRefreshing(false);
        if (data != null && data.size() != 0) {
            adapter.setArtists(data);
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_results_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Artist>> loader) {
        Log.d(TAG, "loader reset");
        adapter.dropArtists();
    }

    private SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.dropArtists();
                Bundle bundle = new Bundle();
                bundle.putBoolean(InfoLoader.REFRESH_EXTRA, true);
                getLoaderManager().restartLoader(0, bundle, ListActivity.this);
            }
        };
    }
}
