package com.example.android.bookstudyplanner.uis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.database.BookEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vanessa on 08/07/2019.
 */

public class TabBooksFragment extends Fragment implements BooksRecyclerViewAdapter.ItemClickListener  {

    private BooksRecyclerViewAdapter booksRecyclerViewAdapter;
    private ArrayList<BookEntity> bookEntities = new ArrayList<>();

    public void setBookEntities(ArrayList<BookEntity> bookEntities) {
        this.bookEntities = bookEntities;
    }

    public void setBooksToAdapter(List<BookEntity> books) {
        if (booksRecyclerViewAdapter != null) {
            booksRecyclerViewAdapter.setBooks(books);
        } else {
            bookEntities.addAll(books);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_books, container, false);
        // set up the RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.rvBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        booksRecyclerViewAdapter = new BooksRecyclerViewAdapter(getActivity(), bookEntities);
        booksRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(booksRecyclerViewAdapter);

        // Test if list empty
        TextView tvError = rootView.findViewById(R.id.tv_error_no_books);
        if(bookEntities.isEmpty()) {
            tvError.setVisibility(View.VISIBLE);
        } else {
            tvError.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {
        BookEntity item = booksRecyclerViewAdapter.getItem(position);

        Intent myIntent = new Intent(getActivity(), BookDetailActivity.class);
        myIntent.putExtra(Utils.INTENT_KEY_BOOK_DETAIL_ACTION, Utils.INTENT_VAL_BOOK_DETAIL_ACTION_MODIF);
        myIntent.putExtra(Utils.INTENT_KEY_BOOK, item);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity());
        getActivity().startActivity(myIntent, options.toBundle());

    }
}
