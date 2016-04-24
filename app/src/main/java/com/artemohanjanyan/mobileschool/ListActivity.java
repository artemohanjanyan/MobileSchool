package com.artemohanjanyan.mobileschool;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.List;

public class ListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Artist>> {

    private static final String TAG = ListActivity.class.getSimpleName();
    private static final String FIRST_LAUNCH_FLAG = "firts launch flag";

    private SwipeRefreshLayout swipeRefreshLayout;
    private Adapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // UI components
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.list_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.dropArtists();
                Bundle bundle = new Bundle();
                bundle.putBoolean(InfoLoader.REFRESH_EXTRA, true);
                getLoaderManager().restartLoader(0, bundle, ListActivity.this);
            }
        });

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
        SharedPreferences preferences = getSharedPreferences(TAG, 0);
        if (preferences.getBoolean(FIRST_LAUNCH_FLAG, true)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.pull_to_download), Toast.LENGTH_SHORT).show();
            preferences.edit().putBoolean(FIRST_LAUNCH_FLAG, false).apply();
        } else {
            getLoaderManager().initLoader(0, null, this);
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        MenuItem item = menu.findItem(R.id.list_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                adapter.dropArtists();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter.dropArtists();
                getLoaderManager().restartLoader(0, null, ListActivity.this);
                searchView.clearFocus();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SearchView view = (SearchView) item.getActionView();
        if (item.getItemId() == R.id.list_search) {
            view.setFocusable(true);
            view.setIconified(false);
            view.requestFocusFromTouch();
        }

        return super.onOptionsItemSelected(item);
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
    public Loader<List<Artist>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "loader created");
        return new InfoLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<Artist>> loader, List<Artist> data) {
        swipeRefreshLayout.setRefreshing(false);
        if (data.size() == 0) {
            Toast.makeText(getApplicationContext(),
                        getString(R.string.no_results_toast), Toast.LENGTH_SHORT).show();
        }
        adapter.setArtists(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Artist>> loader) {
        Log.d(TAG, "loader reset");
        adapter.dropArtists();
    }
}
