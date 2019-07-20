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

public class TodayRecyclerViewAdapter extends RecyclerView.Adapter<TodayRecyclerViewAdapter.TodayViewHolder> {

    private List<BookEntity> mDataBookEntities;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    TodayRecyclerViewAdapter(Context context, List<BookEntity> dataBookEntities) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataBookEntities = dataBookEntities;
    }

    // inflates the row layout from xml when needed
    @Override
    public TodayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_today_item, parent, false);
        return new TodayViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(TodayViewHolder holder, int position) {
        BookEntity book = mDataBookEntities.get(position);
        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookPageCount.setText(String.valueOf(book.getNbPagesToRead()));
        holder.tvBookMinuteCount.setText("TODO");//TODO
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if (mDataBookEntities == null) {
            return 0;
        }
        return mDataBookEntities.size();
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setBooks(List<BookEntity> books) {
        mDataBookEntities = books;
        notifyDataSetChanged();
    }


    // stores and recycles views as they are scrolled off screen
    public class TodayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvBookTitle;
        TextView tvBookPageCount;
        TextView tvBookMinuteCount;

        TodayViewHolder(View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle_today);
            tvBookPageCount = itemView.findViewById(R.id.tvBookPagesCount_today);
            tvBookMinuteCount = itemView.findViewById(R.id.tvBookMinutesCount_today);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    BookEntity getItem(int id) {
        return mDataBookEntities.get(id);
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
