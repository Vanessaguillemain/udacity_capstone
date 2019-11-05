package com.example.android.bookstudyplanner.uis;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.database.PlanningEntity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vanessa on 10/07/2019.
 */

public class TodayRecyclerViewAdapter extends RecyclerView.Adapter<TodayRecyclerViewAdapter.TodayViewHolder> {

    private List<PlanningEntity> mDataPlanningEntities;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private TextView mTvError;

    // data is passed into the constructor
    TodayRecyclerViewAdapter(Context context, List<PlanningEntity> dataBookEntities, TextView tvError) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataPlanningEntities = dataBookEntities;
        this.mTvError = tvError;
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
        PlanningEntity planning = mDataPlanningEntities.get(position);

        Context context = holder.ivImageBook.getContext();
        String title = Utils.getTruncatedStringFor(planning.getTitle(), Utils.SCREEN_TODAY, context);

        holder.tvBookTitle.setText(title);
        String imageLink = planning.getImageLink();
        if(imageLink != null && !imageLink.equals("")) {
            Picasso.with(context).load(imageLink).into((ImageView) holder.ivImageBook);
        } else {
            Picasso.with(context).load(R.drawable.photobook).into((ImageView) holder.ivImageBook);
        }

        holder.tvBookPageCount.setText(String.valueOf(planning.getNbPagesToRead()));
        holder.tvBookMinuteCount.setText(String.valueOf(planning.getNbMinutesReading()));
        if(planning.isDone()) {
            holder.btnDone.setEnabled(false);
            holder.tvBookPageCount.setEnabled(false);
            holder.tvBookMinuteCount.setEnabled(false);
        } else {
            holder.btnDone.setEnabled(true);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if (mDataPlanningEntities == null) {
            return 0;
        }
        return mDataPlanningEntities.size();
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setPlannings(List<PlanningEntity> plannings) {
        mDataPlanningEntities = plannings;
        if (plannings.isEmpty()) {
            mTvError.setVisibility(View.VISIBLE);
        } else {
            mTvError.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }


    // stores and recycles views as they are scrolled off screen
    public class TodayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivImageBook;
        TextView tvBookTitle;
        TextView tvBookPageCount;
        TextView tvBookMinuteCount;
        Button btnDone;

        TodayViewHolder(View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle_today);
            ivImageBook = itemView.findViewById(R.id.ivImageBook_today);
            tvBookPageCount = itemView.findViewById(R.id.tvBookPagesCount_today);

            tvBookPageCount.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if( s.length() == 0 ) {
                        tvBookPageCount.setError(mInflater.getContext().getResources().getString(R.string.err_page_count_required));
                        btnDone.setEnabled(false);
                    } else {
                        btnDone.setEnabled(true);
                    }
                }
            });

            tvBookMinuteCount = itemView.findViewById(R.id.tvBookMinutesCount_today);
            tvBookMinuteCount.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if( s.length() == 0 ) {
                        PlanningEntity p = mDataPlanningEntities.get(getAdapterPosition());
                        tvBookMinuteCount.setHint(p.getNbMinutesReading().toString());
                    }
                }
            });

            btnDone = itemView.findViewById(R.id.btnDone);
            btnDone.setOnClickListener(this);
            ivImageBook.setOnClickListener(this);//click opens Detail screen
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, tvBookPageCount, tvBookMinuteCount, getAdapterPosition() );
        }
    }

    // convenience method for getting data at click position
    PlanningEntity getItem(int id) {
        return mDataPlanningEntities.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, TextView pageCount, TextView minutes, int position);
    }

}
