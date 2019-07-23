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
import com.google.api.services.books.model.Volume;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vanessa on 23/07/2019.
 */

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchBookViewHolder>{
    // Constant for logging
    private static final String TAG = SearchRecyclerViewAdapter.class.getSimpleName();

    private final int spanCount;
    private List<Volume> mVolumes;
    private LayoutInflater mInflater;
   //private ItemClickListener mClickListener;

    public SearchRecyclerViewAdapter(Context context, List<Volume> volumes, int spanCount) {
        this.mVolumes = volumes;
        this.spanCount = spanCount;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setVolumes(List<Volume> volumes) {
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
        holder.setSpanCount(spanCount);
        holder.setVolumeInLayout(mVolumes.get(position));
    }

    @Override
    public long getItemId(int position) {
        return mVolumes.get(position).getId().hashCode();
    }

    // convenience method for getting data at click position
    Volume getItem(int id) {
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
        private Volume volume;
        private int spanCount;
        ImageView ivBookImage;
        TextView tvBookTitle;
        TextView tvBookSubTitle;
        TextView tvBookPageCount;

        public SearchBookViewHolder(ViewGroup viewGroup) {
        //public SearchBookViewHolder(View viewGroup) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_search_item, viewGroup, false));
            ivBookImage = (ImageView) itemView.findViewById(R.id.imageBook_search);
            tvBookTitle = (TextView) itemView.findViewById(R.id.tvBookTitle_search);
            tvBookSubTitle = (TextView) itemView.findViewById(R.id.tvBookSubTitle_search);
            tvBookPageCount = (TextView) itemView.findViewById(R.id.tvBookPageCount_search);
            itemView.setOnClickListener(this);
        }

        public void setVolumeInLayout(Volume volume) {
            this.volume = volume;

            System.out.println(volume.getVolumeInfo().getInfoLink());

            Volume.VolumeInfo.ImageLinks imageLinks = volume.getVolumeInfo().getImageLinks();

            if (imageLinks != null) {
                String medium = imageLinks.getMedium();
                String large = imageLinks.getLarge();
                String small = imageLinks.getSmall();
                String thumbnail = imageLinks.getThumbnail();
                String smallThumbnail = imageLinks.getSmallThumbnail();

                String imageLink = "";
                if (large != null) {
                    imageLink = large;
                } else if (medium != null) {
                    imageLink = medium;
                } else if (small != null) {
                    imageLink = small;
                } else if (thumbnail != null) {
                    imageLink = thumbnail;
                } else if (smallThumbnail != null) {
                    imageLink = smallThumbnail;
                }

                imageLink = imageLink.replace("edge=curl", "");
                System.out.println(imageLink);

                Picasso.with(itemView.getContext()).load(imageLink).into((ImageView) ivBookImage);

            } else {
                Log.d(TAG, "no image");//TODO
                Picasso.with(itemView.getContext()).load(R.drawable.photobook).into((ImageView) ivBookImage);
            }

            Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();

            if (volumeInfo != null) {
                if (volumeInfo.getTitle() != null) {
                    tvBookTitle.setVisibility(View.VISIBLE);
                    tvBookTitle.setText(volumeInfo.getTitle());
                } else {
                    tvBookTitle.setVisibility(View.GONE);
                }
                if (volumeInfo.getSubtitle() != null) {
                    tvBookSubTitle.setVisibility(View.VISIBLE);
                    tvBookSubTitle.setText(volumeInfo.getSubtitle());
                } else {
                    tvBookSubTitle.setVisibility(View.GONE);
                }
                if (volumeInfo.getPageCount() != null) {
                    tvBookPageCount.setVisibility(View.VISIBLE);
                    tvBookPageCount.setText(String.valueOf(volumeInfo.getPageCount())+ " pages");//TODO
                } else {
                    tvBookPageCount.setVisibility(View.GONE);
                }
            }

        }

        public void setSpanCount(int spanCount) {
            this.spanCount = spanCount;
        }

        @Override
        public void onClick(View v) {

            Bundle metadata = new Bundle();

            int i = getAdapterPosition();

            Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();

            if (volumeInfo != null) {
                if (volumeInfo.getTitle() != null) {
                    metadata.putString(GoogleBookMetaData.TITLE, volumeInfo.getTitle());
                }
                if (volumeInfo.getPageCount() != null) {
                    metadata.putInt(GoogleBookMetaData.PAGE_COUNT, volumeInfo.getPageCount());
                }


                Volume.VolumeInfo.ImageLinks imageLinks = volumeInfo.getImageLinks();
                if (imageLinks != null) {
                    String image = null;
                    if (imageLinks.getExtraLarge() != null) {
                        image = imageLinks.getExtraLarge();
                    } else if (imageLinks.getLarge() != null) {
                        image = imageLinks.getLarge();
                    } else if (imageLinks.getMedium() != null) {
                        image = imageLinks.getMedium();
                    } else if (imageLinks.getSmall() != null) {
                        image = imageLinks.getSmall();
                    } else if (imageLinks.getThumbnail() != null) {
                        image = imageLinks.getThumbnail();
                    } else if (imageLinks.getSmallThumbnail() != null) {
                        image = imageLinks.getSmallThumbnail();
                    }
                    if (image != null) {
                        metadata.putString(GoogleBookMetaData.IMAGE, image);
                    }
                }
            }

            Context context = itemView.getContext();
            Intent myIntent = new Intent(context, BookDetailActivity.class);
            myIntent.putExtra(Utils.INTENT_KEY_BOOK_DETAIL_ACTION, Utils.INTENT_VAL_BOOK_DETAIL_ACTION_FROM_SEARCH);
            myIntent.putExtra("metadata", metadata);//TODO
            context.startActivity(myIntent);
        }

    }


}
