package com.artemohanjanyan.mobileschool;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = "ListActivity";

    private DownloadInfoTask downloadInfoTask;
    private static final String jsonURL = "http://cache-spb03.cdn.yandex.net/" +
            "download.cdn.yandex.net/mobilization-2016/artists.json";

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // UI components
        progressBar = (ProgressBar) findViewById(R.id.list_progress_bar);

        recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this,
                getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT ? 1 : 2));
        adapter = new Adapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

        // Download JSON
        downloadInfoTask = new DownloadInfoTask(this);
        URL url = null;
        try {
            url = new URL(jsonURL);
        } catch (MalformedURLException e) {
            // URL is well-formed
            Log.d(TAG, "Malformed URL", e);
        }
        downloadInfoTask.execute(url);
    }

    @Override
    protected void onDestroy() {
        if (downloadInfoTask != null) {
            downloadInfoTask.cancel(true);
            downloadInfoTask = null;
        }

        super.onDestroy();
    }

    public void onJsonDownloaded(List<Artist> artists) {
        if (artists != null) {
            downloadInfoTask = null;
            progressBar.setVisibility(View.INVISIBLE);
            adapter.addArtists(artists);
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.error_toast), Toast.LENGTH_SHORT).show();
        }
    }
}
