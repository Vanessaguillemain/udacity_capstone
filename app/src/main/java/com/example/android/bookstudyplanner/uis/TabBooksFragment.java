package com.example.android.bookstudyplanner.uis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bookstudyplanner.R;

/**
 * Created by vanessa on 08/07/2019.
 */

public class TabBooksFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books, container, false);
    }
}
