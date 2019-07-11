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

public class TabBooksFragment extends Fragment implements BooksRecyclerViewAdapter.ItemClickListener  {

    BooksRecyclerViewAdapter booksRecyclerViewAdapter;
    ArrayList<String> bookTitles = new ArrayList<>();

    public void setBookTitles(ArrayList<String> bookTitles) {
        this.bookTitles = bookTitles;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_books, container, false);

        // set up the RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.rvBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        booksRecyclerViewAdapter = new BooksRecyclerViewAdapter(getActivity(), bookTitles);
        booksRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(booksRecyclerViewAdapter);

        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "You clicked " + booksRecyclerViewAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
