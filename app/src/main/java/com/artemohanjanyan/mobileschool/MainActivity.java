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

        if (findViewById(R.id.activity_main_layout) != null) {
            if (savedInstanceState != null) {
                listFragment = (ListFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.activity_main_layout);
                return;
            }

            listFragment = new ListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main_layout, listFragment).commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        listFragment.onNewIntent(intent);
    }
}
