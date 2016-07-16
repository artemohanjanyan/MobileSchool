package com.artemohanjanyan.mobileschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_list);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        listFragment.onNewIntent(intent);
    }
}
