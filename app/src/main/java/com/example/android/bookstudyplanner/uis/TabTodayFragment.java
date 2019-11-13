package com.example.android.bookstudyplanner.uis;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import com.example.android.bookstudyplanner.bookservice.WidgetService;
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
    private RecyclerView myRecyclerView;

    private TodayRecyclerViewAdapter todayRecyclerViewAdapter;
    private ArrayList<PlanningEntity> planningEntities = new ArrayList<>();

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

        // Test if list empty
        TextView tvError = rootView.findViewById(R.id.tv_error_no_plannings);

        // set up the RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.rvToday);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3));
        todayRecyclerViewAdapter = new TodayRecyclerViewAdapter(getActivity(), planningEntities, tvError);
        todayRecyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(todayRecyclerViewAdapter);
        myRecyclerView = recyclerView;

        if(planningEntities.isEmpty()) {
            tvError.setVisibility(View.VISIBLE);
        } else {
            tvError.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onItemClick(View view,TextView tvPageCount,TextView tvPageFrom, TextView tvPageTo, TextView tvMinutes,  int position) {
        final PlanningEntity planning = todayRecyclerViewAdapter.getItem(position);

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
            final int pageFrom = Integer.parseInt(tvPageFrom.getText().toString());
            final int pageTo = Integer.parseInt(tvPageTo.getText().toString());

            //PlanningEntity
            final PlanningEntity planningToday = new PlanningEntity(mDate, mBookId, true, pagesCount, pageFrom, pageTo, minutes);
            AppExecutor.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    //update book
                    Integer nbPagesToRead = mDb.bookDao().loadNbPagesToReadBookById(mBookId);
                    Integer nbPagesReadBefore = mDb.bookDao().loadNbPagesReadBookById(mBookId);
                    Integer totalPagesRead = pagesCount + nbPagesReadBefore;
                    Double percentRead = Utils.getPercentRead(totalPagesRead, nbPagesToRead);
                    mDb.bookDao().updateBookReadingForBookId(mBookId, totalPagesRead, percentRead, pageTo);

                    //delete all plannings from D day
                    mDb.planningDao().deletePlanningByBookIdAfterIncludeDate(mBookId, mDate);

                    //insert planning for today
                    mDb.planningDao().insertPlanning(planningToday);

                    //get data for new plannings after today
                    int nbPagesLastToReadAfterToday = nbPagesToRead - totalPagesRead;
                    String mSTabWeekPlanning = mDb.bookDao().loadWeekPlanningById(mBookId);
                    int[] mITabWeekPlanning = Utils.getTabWeekPlanningFromString(mSTabWeekPlanning);
                    int mTotalDaysByWeek = Utils.calculateNbDaysAWeek(mITabWeekPlanning);

                    //mBeginDate = tomorrow for next plannings
                    Date mBeginDate = Utils.dateAfter(Utils.getToday());
                    Date mEndDate = mDb.bookDao().loadEndDateById(mBookId);
                    int lastBookPage = mDb.bookDao().loadToPageNbById(mBookId);

                    int mAvgNbSecByPage = getResources().getInteger(R.integer.avg_nb_sec_by_page);
                    List<Date> planningDates = Utils.getPlanning(mBeginDate, mEndDate, mITabWeekPlanning);
                    int mNbPagesToReadByDay = Utils.calculateNbPagesAverage(nbPagesLastToReadAfterToday, mBeginDate, mEndDate, mITabWeekPlanning, mTotalDaysByWeek);

                    //insert all other plannings after today
                    int firstPage = pageTo +1;
                    int lastPage = firstPage + mNbPagesToReadByDay - 1;
                    //inserting new plannings for tomorrow and after
                    int nbMinutesReading = mNbPagesToReadByDay*mAvgNbSecByPage/60;
                    for (Date d : planningDates){
                        PlanningEntity planning = new PlanningEntity(d, mBookId, false, mNbPagesToReadByDay, firstPage, lastPage, nbMinutesReading);
                        mDb.planningDao().insertPlanning(planning);
                        firstPage = lastPage + 1;
                        lastPage = firstPage + mNbPagesToReadByDay - 1;
                        if(lastPage > lastBookPage) {
                            lastPage = lastBookPage;
                            mNbPagesToReadByDay = lastPage - firstPage + 1;
                            nbMinutesReading = mNbPagesToReadByDay*mAvgNbSecByPage/60;
                        }
                    }

                    //Update widget
                    WidgetService.startActionUpdateWidgets(getContext());
                }
            });

            Button b = view.getRootView().findViewById(R.id.btnDone);
            b.setEnabled(false);

        } else {
            //open detail screen
            AppExecutor.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    BookEntity book = mDb.bookDao().loadBookEntityById(planning.getBookId());
                    Intent myIntent = new Intent(getActivity(), BookDetailActivity.class);
                    myIntent.putExtra(Utils.INTENT_KEY_BOOK_DETAIL_ACTION, Utils.INTENT_VAL_BOOK_DETAIL_ACTION_MODIF);
                    myIntent.putExtra(Utils.INTENT_KEY_BOOK, book);
                    //TODO : try an animation
                    getActivity().startActivity(myIntent);
                }
            });
        }

    }

    public void selectBookPositionWithIdFromWidget(int bookIdFromWidget) {
        if(todayRecyclerViewAdapter != null && bookIdFromWidget != Utils.INTENT_VAL_BOOK_ID_EMPTY) {
            int position = 0;
            for(PlanningEntity planningEntity : planningEntities) {
                if(bookIdFromWidget == planningEntity.getBookId()) {
                    myRecyclerView.getLayoutManager().scrollToPosition(position);
                    //TODO reload data in card planning today ?
                    // ...
                    return;
                } else {
                    position++;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
