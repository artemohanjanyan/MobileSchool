package com.artemohanjanyan.mobileschool;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
        implements Adapter.OnArtistSelectListener, ListFragment.MenuListener {

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

    // Workaround
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

    @Override
    public void onAboutSelected() {
        AboutFragment aboutFragment = new AboutFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_main_layout, aboutFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFeedbackSelected() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"artemohanjanyan@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
        startActivity(Intent.createChooser(intent, getString(R.string.feedback)));
    }
}
