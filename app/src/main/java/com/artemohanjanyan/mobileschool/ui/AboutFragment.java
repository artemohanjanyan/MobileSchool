package com.artemohanjanyan.mobileschool.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artemohanjanyan.mobileschool.R;

public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.about));
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}
