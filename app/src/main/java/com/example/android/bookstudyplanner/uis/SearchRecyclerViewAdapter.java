package com.example.android.bookstudyplanner.uis;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.database.GoogleBookMetaData;
import com.example.android.bookstudyplanner.entities.MyVolume;
import com.google.api.services.books.model.Volume;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vanessa on 23/07/2019.
 */

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchBookViewHolder>{
    // Constant for logging
    private static final String TAG = SearchRecyclerViewAdapter.class.getSimpleName();

    private List<MyVolume> mVolumes;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public SearchRecyclerViewAdapter(Context context, List<MyVolume> volumes) {
        this.mVolumes = volumes;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setVolumes(List<MyVolume> volumes) {
        mVolumes = volumes;
        notifyDataSetChanged();
    }

    @Override
    public SearchBookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_search_item, parent, false);
        return new SearchBookViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(SearchBookViewHolder holder, int position) {
        holder.setVolumeInLayout(mVolumes.get(position));
    }

    @Override
    public long getItemId(int position) {
        return mVolumes.get(position).getId().hashCode();
    }

    // convenience method for getting data at click position
    MyVolume getItem(int id) {
        return mVolumes.get(id);
    }

    @Override
    public int getItemCount() {
        if (mVolumes == null) {
            return 0;
        }
        return mVolumes.size();
    }


    //****************************************************
    // View Holder
    //****************************************************

    public class SearchBookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MyVolume volume;
        private int spanCount;
        ImageView ivBookImage;
        TextView tvBookTitle;
        //TextView tvBookSubTitle;
        TextView tvBookPageCount;

        public SearchBookViewHolder(ViewGroup viewGroup) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_search_item, viewGroup, false));
            ivBookImage = (ImageView) itemView.findViewById(R.id.imageBook_search);
            tvBookTitle = (TextView) itemView.findViewById(R.id.tvBookTitle_search);
            //tvBookSubTitle = (TextView) itemView.findViewById(R.id.tvBookSubTitle_search);
            tvBookPageCount = (TextView) itemView.findViewById(R.id.tvBookPageCount_search);
            itemView.setOnClickListener(this);
        }

        public void setVolumeInLayout(MyVolume volume) {
            this.volume = volume;

            String imageLink = volume.getVolumeInfoImageLink();
            if(imageLink != null && !imageLink.equals("")) {
                Picasso.with(itemView.getContext()).load(imageLink).into((ImageView) ivBookImage);
            } else {
                Picasso.with(itemView.getContext()).load(R.drawable.photobook).into((ImageView) ivBookImage);
            }
            if (volume.getVolumeInfoTitle() != null) {
                tvBookTitle.setText(volume.getVolumeInfoTitle());
            } else {
                tvBookTitle.setText(R.string.err_search_title_not_found);
            }
            if (volume.getVolumeInfoPageCount() != null && !volume.getVolumeInfoPageCount().equals(MyVolume.NO_PAGE_COUNT)) {
                String pageCount = String.valueOf(volume.getVolumeInfoPageCount())+ R.string.label_pages;
                tvBookPageCount.setText(pageCount);
            } else {
                tvBookPageCount.setText(R.string.label_search_no_pages);
            }

        }

        public void setSpanCount(int spanCount) {
            this.spanCount = spanCount;
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    void setClickListener(SearchRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
