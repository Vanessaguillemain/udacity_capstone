package com.example.android.bookstudyplanner.uis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.bookstudyplanner.R;

import java.util.ArrayList;

/**
 * Created by vanessa on 08/07/2019.
 */

public class TabBooksFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener  {

    MyRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_books, container, false);

        // data to populate the RecyclerView with
        ArrayList<String> bookTitles = new ArrayList<>();
        bookTitles.add("Android for dummies");
        bookTitles.add("The alchemist");
        bookTitles.add("the 4 hours week");
        bookTitles.add("Hello World");
        bookTitles.add("Misery");

        // set up the RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.rvBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyRecyclerViewAdapter(getActivity(), bookTitles);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
