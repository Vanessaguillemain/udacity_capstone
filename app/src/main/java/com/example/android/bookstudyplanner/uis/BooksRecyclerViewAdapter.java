package com.example.android.bookstudyplanner.uis;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.database.BookEntity;

import java.util.List;

/**
 * Created by vanessa on 10/07/2019.
 */

public class BooksRecyclerViewAdapter extends RecyclerView.Adapter<BooksRecyclerViewAdapter.BooksViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    BooksRecyclerViewAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public BooksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_books_item, parent, false);
        return new BooksViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(BooksViewHolder holder, int position) {
        String animal = mData.get(position);
        holder.myTextView.setText(animal);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setBooks(List<String> books) {
        //TODO see if useful
        mData = books;
        notifyDataSetChanged();
    }


    // stores and recycles views as they are scrolled off screen
    public class BooksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        BooksViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvBookTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
