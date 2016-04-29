package com.artemohanjanyan.mobileschool;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ListActivity.class.getSimpleName();
    private static final String FIRST_LAUNCH_FLAG = "first launch flag";
    private static final String LAST_POSITION = "last position";
    private static final String SEARCH_QUERY = "search query";

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;
    private Adapter adapter;
    private SearchView searchView;

    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // UI components
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.list_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.dropCursor();
                Bundle bundle = new Bundle();
                bundle.putBoolean(InfoLoader.REFRESH_EXTRA, true);
                getLoaderManager().restartLoader(0, bundle, ListActivity.this);
            }
        });
        textView = (TextView) findViewById(R.id.list_no_artists_text);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this,
                getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT ? 1 : 2));
        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

        // Warn if internet connection isn't available.
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

        // Get data
        swipeRefreshLayout.setRefreshing(true);
        SharedPreferences preferences = getSharedPreferences(TAG, 0);
        Bundle bundle = new Bundle();
        if (preferences.getBoolean(FIRST_LAUNCH_FLAG, true)) {
            // Why ask to pull, if app can pull without any help.
            bundle.putBoolean(InfoLoader.REFRESH_EXTRA, true);
            preferences.edit().putBoolean(FIRST_LAUNCH_FLAG, false).apply();
        }

        getLoaderManager().initLoader(0, bundle, ListActivity.this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Avoid unnecessary animation.
        outState.putInt(LAST_POSITION, adapter.getLastPosition());
        // Save search query
        outState.putString(SEARCH_QUERY, searchQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adapter.setLastPosition(savedInstanceState.getInt(LAST_POSITION));
        searchQuery = savedInstanceState.getString(SEARCH_QUERY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        // Setup SearchView
        MenuItem item = menu.findItem(R.id.list_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                adapter.dropCursor();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchQuery = null;
                adapter.dropCursor();
                searchView.clearFocus();
                getLoaderManager().restartLoader(0, null, ListActivity.this);
                return true;
            }
        });

        if (searchQuery != null && !searchQuery.isEmpty()) {
            item.expandActionView();
            searchView.setQuery(searchQuery, true);
            searchView.clearFocus();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SearchView view = (SearchView) item.getActionView();
        if (item.getItemId() == R.id.list_search) {
            // Focus on the text input field so that virtual keyboard appears.
            view.setFocusable(true);
            view.setIconified(false);
            view.requestFocusFromTouch();
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);

            // UI
            swipeRefreshLayout.setRefreshing(true);
            searchView.clearFocus();

            // Start search
            Bundle bundle = new Bundle();
            bundle.putString(InfoLoader.SEARCH_EXTRA, searchQuery.toLowerCase());
            getLoaderManager().restartLoader(0, bundle, ListActivity.this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "loader created");
        return new InfoLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);
        if (data.getCount() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "loader reset");
        // Should remove reference to Loader's data.
        adapter.dropCursor();
    }
}
