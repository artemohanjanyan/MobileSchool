package com.artemohanjanyan.mobileschool;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class ListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    interface MenuListener {
        void onAboutSelected();
        void onFeedbackSelected();
        void onSettingsSelected();
    }

    private static final String TAG = ListFragment.class.getSimpleName();
    private static final String FIRST_LAUNCH_FLAG = "first launch flag";
    private static final String LAST_POSITION = "last position";
    private static final String SEARCH_QUERY = "search query";

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;
    private Adapter adapter;
    private SearchView searchView;

    private String searchQuery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout =
                (FrameLayout) inflater.inflate(R.layout.fragment_list, container, false);

        // UI components
        swipeRefreshLayout = (SwipeRefreshLayout) frameLayout.findViewById(R.id.list_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.dropCursor();
                Bundle bundle = new Bundle();
                bundle.putBoolean(InfoLoader.REFRESH_EXTRA, true);
                getLoaderManager().restartLoader(0, bundle, ListFragment.this);
            }
        });
        textView = (TextView) frameLayout.findViewById(R.id.list_no_artists_text);

        RecyclerView recyclerView = (RecyclerView) frameLayout.findViewById(R.id.list_recycler_view);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),
                getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT ? 1 : 2));
        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

        getActivity().setTitle(getString(R.string.app_name));

        // Warn if internet connection isn't available.
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            Toast.makeText(getContext().getApplicationContext(),
                    getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

        // Get data
        swipeRefreshLayout.setRefreshing(true);
        SharedPreferences preferences = getContext().getSharedPreferences(TAG, 0);
        Bundle bundle = new Bundle();
        if (preferences.getBoolean(FIRST_LAUNCH_FLAG, true)) {
            // Why ask to pull, if app can pull without any help.
            bundle.putBoolean(InfoLoader.REFRESH_EXTRA, true);
            preferences.edit().putBoolean(FIRST_LAUNCH_FLAG, false).apply();
        }

        getLoaderManager().initLoader(0, bundle, ListFragment.this);

        return frameLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Avoid unnecessary animation.
        outState.putInt(LAST_POSITION, adapter.getLastPosition());
        // Save search query
        outState.putString(SEARCH_QUERY, searchQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            adapter.setLastPosition(savedInstanceState.getInt(LAST_POSITION));
            searchQuery = savedInstanceState.getString(SEARCH_QUERY);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);

        // Setup SearchView
        MenuItem item = menu.findItem(R.id.list_search);

        SearchManager searchManager =
                (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
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
                getLoaderManager().restartLoader(0, null, ListFragment.this);
                return true;
            }
        });

        if (searchQuery != null && !searchQuery.isEmpty()) {
            item.expandActionView();
            searchView.setQuery(searchQuery, true);
            searchView.clearFocus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SearchView view = (SearchView) item.getActionView();
        MenuListener listener = (MenuListener) getActivity();
        switch (item.getItemId()) {
            case R.id.list_search:
                // Focus on the text input field so that virtual keyboard appears.
                view.setFocusable(true);
                view.setIconified(false);
                view.requestFocusFromTouch();
                break;
            case R.id.list_about:
                listener.onAboutSelected();
                break;
            case R.id.list_feedback:
                listener.onFeedbackSelected();
                break;
            case R.id.list_settings:
                listener.onSettingsSelected();
                break;
        }
        return true;
    }

    void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);

            // UI
            swipeRefreshLayout.setRefreshing(true);
            searchView.clearFocus();

            // Start search
            Bundle bundle = new Bundle();
            bundle.putString(InfoLoader.SEARCH_EXTRA, searchQuery.toLowerCase());
            getLoaderManager().restartLoader(0, bundle, ListFragment.this);
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "loader created");
        return new InfoLoader(getContext(), args);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);
        if (data.getCount() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        Log.d(TAG, "loader reset");
        // Should remove reference to Loader's data.
        adapter.dropCursor();
    }
}
