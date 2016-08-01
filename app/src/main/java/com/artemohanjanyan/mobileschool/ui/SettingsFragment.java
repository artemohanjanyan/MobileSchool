package com.artemohanjanyan.mobileschool.ui;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.artemohanjanyan.mobileschool.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.fragment_preferences);
        getActivity().setTitle(getString(R.string.settings));
    }
}
