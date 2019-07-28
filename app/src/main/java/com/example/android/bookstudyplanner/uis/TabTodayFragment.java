package com.example.android.bookstudyplanner.uis;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bookstudyplanner.AddBookViewModel;
import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.UpdatePlanningViewModel;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.database.AddBookViewModelFactory;
import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.AppExecutor;
import com.example.android.bookstudyplanner.database.BookEntity;
import com.example.android.bookstudyplanner.database.PlanningEntity;
import com.example.android.bookstudyplanner.database.UpdatePlanningViewModelFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vanessa on 08/07/2019.
 */

public class TabTodayFragment extends Fragment implements TodayRecyclerViewAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = TabTodayFragment.class.getSimpleName();

    TodayRecyclerViewAdapter todayRecyclerViewAdapter;
    ArrayList<PlanningEntity> planningEntities = new ArrayList<>();

    private AppDatabase mDb;

    public void setPlanningEntities(ArrayList<PlanningEntity> planningEntities) {
        this.planningEntities = planningEntities;
    }

    public void setPlanningsToAdapter(List<PlanningEntity> plannings) {
        if (todayRecyclerViewAdapter != null) {
            todayRecyclerViewAdapter.setPlannings(plannings);
        } else {
            planningEntities.addAll(plannings);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());

        // set up the RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.rvToday);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3));
        todayRecyclerViewAdapter = new TodayRecyclerViewAdapter(getActivity(), planningEntities);
        todayRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(todayRecyclerViewAdapter);

        return rootView;
    }

    @Override
    public void onItemClick(View view, TextView tvPageCount, TextView tvMinutes,  int position) {
        PlanningEntity planning = todayRecyclerViewAdapter.getItem(position);

        if(view.getId() == R.id.btnDone) {

            final int mBookId = planning.getBookId();
            final Date mDate = planning.getDate();

            UpdatePlanningViewModelFactory factory = new UpdatePlanningViewModelFactory(mDb, mBookId, mDate);
            final UpdatePlanningViewModel viewModel = ViewModelProviders.of(this, factory).get(UpdatePlanningViewModel.class);

            viewModel.getPlanning().observe(this, new Observer<PlanningEntity>() {
                @Override
                public void onChanged(@Nullable PlanningEntity planning) {
                    viewModel.getPlanning().removeObserver(this);
                    Log.d(TAG, "From viewModel Planning");
                }
            });

            AddBookViewModelFactory factoryBook = new AddBookViewModelFactory(mDb, mBookId);
            final AddBookViewModel viewModelBook = ViewModelProviders.of(this, factoryBook).get(AddBookViewModel.class);
            viewModelBook.getBook().observe(this, new Observer<BookEntity>() {
                @Override
                public void onChanged(@Nullable BookEntity book) {
                viewModelBook.getBook().removeObserver(this);
                Log.d(TAG, "From viewModel Book");
                }
            });

            final int pagesCount = Integer.parseInt(tvPageCount.getText().toString());
            int minutes = planning.getNbMinutesReading();
            if(tvMinutes.getText() != null && tvMinutes.getText().length() > 0) {
                minutes = Integer.parseInt(tvMinutes.getText().toString());
            }

            //PlanningEntity
            final PlanningEntity planning2 = new PlanningEntity(mDate, mBookId, true, pagesCount, minutes);
            AppExecutor.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    //update
                    mDb.planningDao().updatePlanning(planning2);
                    Integer nbPagesToRead = mDb.bookDao().loadNbPagesToReadBookById(mBookId);
                    Integer nbPagesReadBefore = mDb.bookDao().loadNbPagesReadBookById(mBookId);
                    Integer totalPagesRead = pagesCount + nbPagesReadBefore;
                    Double percentRead = Utils.getPercentRead(totalPagesRead, nbPagesToRead);
                    mDb.bookDao().updateBookReadingForBookId(mBookId, totalPagesRead, percentRead);
                }
            });

            Button b = view.getRootView().findViewById(R.id.btnDone);
            b.setEnabled(false);

        }

    }
    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
