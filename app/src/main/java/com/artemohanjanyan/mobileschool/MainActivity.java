package com.artemohanjanyan.mobileschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements Adapter.OnArtistSelectListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.activity_main_layout) != null && savedInstanceState == null) {
            Fragment fragment = new ListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main_layout, fragment).commit();
        }
    }

    // Вот тут не очень клёво
    @Override
    protected void onNewIntent(Intent intent) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_layout);
        if (fragment instanceof ListFragment) {
            ((ListFragment) fragment).onNewIntent(intent);
        }
    }

    @Override
    public void onArtistSelected(Artist artist) {
        DescriptionFragment descriptionFragment = new DescriptionFragment();

        Bundle args = new Bundle();
        args.putParcelable(DescriptionFragment.ARTIST_EXTRA, artist);
        descriptionFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_main_layout, descriptionFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
