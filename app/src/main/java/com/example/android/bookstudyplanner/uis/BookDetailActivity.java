package com.example.android.bookstudyplanner.uis;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.android.bookstudyplanner.AddBookViewModel;
import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.database.AddBookViewModelFactory;
import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.AppExecutor;
import com.example.android.bookstudyplanner.database.BookEntity;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.bookstudyplanner.Utils.dateIsBeforeToday;
import static com.example.android.bookstudyplanner.Utils.secondsToText;
import static com.example.android.bookstudyplanner.database.DatabaseUtils.ISBN_ABSENT_VALUE;

/**
 * Created by vanessa on 09/07/2019.
 */

public class BookDetailActivity extends AppCompatActivity implements TextWatcher, CompoundButton.OnCheckedChangeListener{

    // Constant for logging
    private static final String TAG = BookDetailActivity.class.getSimpleName();

    private final String BUNDLE_KEY_TEXT_TITLE = "BUNDLE_KEY_TEXT_TITLE";
    private final String BUNDLE_KEY_TEXT_FROM_DATE = "BUNDLE_KEY_TEXT_FROM_DATE";
    private final String BUNDLE_KEY_TEXT_TO_DATE = "BUNDLE_KEY_TEXT_TO_DATE";
    private final String STRING_NUMBER_PAGE_NULL = "0";

    @BindView(R.id.tvTitle) TextView mTvTitle;
    @BindView(R.id.valuePageCount) TextView mValuePageCount;
    @BindView(R.id.tvFromPage) TextView mTvFromPage;
    @BindView(R.id.tvToPage) TextView mTvToPage;
    @BindView(R.id.valueNbPagesToRead) TextView mValueNbPagesToRead;
    @BindView(R.id.labelNbPagesToRead) TextView mLabelNbPagesToRead;
    @BindView(R.id.valueTimeEstimated) TextView mValueTimeEstimated;
    @BindView(R.id.buttonSave) Button mButtonSave;
    @BindView(R.id.buttonDelete) Button mButtonDelete;
    @BindView(R.id.labelErrorFromDate) TextView mLabelErrorFromDate;
    @BindView(R.id.labelErrorToDate) TextView mLabelErrorToDate;
    @BindView(R.id.labelSelectFromDate) TextView mLabelSelectFromDate;
    @BindView(R.id.labelSelectToDate) TextView mLabelSelectToDate;
    @BindView(R.id.btnCalendarFrom) Button mButtonCalendarFrom;
    @BindView(R.id.btnCalendarTo) Button mButtonCalendarTo;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.cbx1) CheckBox mCbx1;
    @BindView(R.id.cbx2) CheckBox mCbx2;
    @BindView(R.id.cbx3) CheckBox mCbx3;
    @BindView(R.id.cbx4) CheckBox mCbx4;
    @BindView(R.id.cbx5) CheckBox mCbx5;
    @BindView(R.id.cbx6) CheckBox mCbx6;
    @BindView(R.id.cbx7) CheckBox mCbx7;
    @BindView(R.id.aboutNbPages) TextView mAboutNbPages;

    private int year;
    private int month;
    private int dayOfMonth;
    private int yearFrom;
    private int monthFrom;
    private int dayOfMonthFrom;
    private int yearTo;
    private int monthTo;
    private int dayOfMonthTo;
    private Calendar calendar;
    private Date mBeginDate;
    private Date mEndDate;
    private DatePickerDialog.OnDateSetListener mDateFromSetListener;
    private DatePickerDialog.OnDateSetListener mDateToSetListener;
    private int mTotalDays = 5;
    private String mWeekPlanning;
    private int mTabWeekPlanning[] = {1,1,1,1,1,0,0};
    private int mNbPagesToRead;
    private int mAvgNbSecByPage;

    // Constant for default book id to be used when not in update mode
    private static final int DEFAULT_BOOK_ID = -1;

    // Member variable for the Database
    private AppDatabase mDb;
    private int mBookId = DEFAULT_BOOK_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);

        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        // recovering the instance state
        if (savedInstanceState != null) {
            mTvTitle.setText(savedInstanceState.getString(BUNDLE_KEY_TEXT_TITLE));
            String sBegin = savedInstanceState.getString(BUNDLE_KEY_TEXT_FROM_DATE);
            String sEnd = savedInstanceState.getString(BUNDLE_KEY_TEXT_TO_DATE);
            //Save locally the dates to avoid them to be wipe off by data base values
            mBeginDate = Utils.getDateFromFormatedDate(sBegin, BookDetailActivity.this);
            yearFrom = Utils.getYearFromDate(mBeginDate);
            monthFrom = Utils.getMonthFromDate(mBeginDate);
            dayOfMonthFrom = Utils.getDayFromDate(mBeginDate);

            mEndDate = Utils.getDateFromFormatedDate(sEnd, BookDetailActivity.this);
            yearTo = Utils.getYearFromDate(mEndDate);
            monthTo = Utils.getMonthFromDate(mEndDate);
            dayOfMonthTo = Utils.getDayFromDate(mEndDate);

            mLabelSelectFromDate.setText(sBegin);
            mLabelSelectToDate.setText(sEnd);
        }

        Intent intent = getIntent();
        if(intent != null) {
            String action = intent.getStringExtra(Utils.INTENT_KEY_BOOK_DETAIL_ACTION);

            if (Utils.INTENT_VAL_BOOK_DETAIL_ACTION_MODIF.equals(action)) {
                //TODO test if null
                BookEntity book = intent.getParcelableExtra(Utils.INTENT_KEY_BOOK);
                mBookId = book.getId();

                AddBookViewModelFactory factory = new AddBookViewModelFactory(mDb, mBookId);
                final AddBookViewModel viewModel = ViewModelProviders.of(this, factory).get(AddBookViewModel.class);

                viewModel.getBook().observe(this, new Observer<BookEntity>() {
                    @Override
                    public void onChanged(@Nullable BookEntity book) {
                        viewModel.getBook().removeObserver(this);
                        Log.d(TAG, "Receiving database update from LiveData");
                        fillLayoutFields(book);
                        setNbPagesAverage();
                    }
                });
            } else {
                mButtonDelete.setVisibility(View.GONE);
                mToolbar.setTitle(getString(R.string.title_create));
                mButtonSave.setEnabled(false);
            }
        }
    }

    private void fillLayoutFields(BookEntity item) {
        mTvTitle.setText(item.getTitle());
        mValuePageCount.setText(String.valueOf(item.getPageCount()));
        if(item.getNbPagesToRead() != null) {
            mValueNbPagesToRead.setText(String.valueOf(item.getNbPagesToRead()));
        }
        if(item.getFromPageNb() == null) {
            mTvFromPage.setText(STRING_NUMBER_PAGE_NULL);
        } else {
            mTvFromPage.setText(String.valueOf(item.getFromPageNb()));
        }
        if(item.getToPageNb() == null) {
            mTvToPage.setText(STRING_NUMBER_PAGE_NULL);
        } else {
            mTvToPage.setText(String.valueOf(item.getToPageNb()));
        }
        //If mBeginDate is not null, it has been initialized in savednstanceSTate
        if(mBeginDate == null) {
            mBeginDate = item.getBeginDate();
            if (mBeginDate != null) {
                String sBegin = Utils.getFormatedDateFromDate(mBeginDate, BookDetailActivity.this);
                mLabelSelectFromDate.setText(sBegin);
                yearFrom = Utils.getYearFromDate(mBeginDate);
                monthFrom = Utils.getMonthFromDate(mBeginDate);
                dayOfMonthFrom = Utils.getDayFromDate(mBeginDate);
            }
        }
        //If mEndDate is not null, it has been initialized in savednstanceSTate
        if(mEndDate == null) {
            mEndDate = item.getEndDate();
            if (mEndDate != null) {
                String sEnd = Utils.getFormatedDateFromDate(mEndDate, BookDetailActivity.this);
                mLabelSelectToDate.setText(sEnd);
                yearTo = Utils.getYearFromDate(mEndDate);
                monthTo = Utils.getMonthFromDate(mEndDate);
                dayOfMonthTo = Utils.getDayFromDate(mEndDate);
            }
        }
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {

        ButterKnife.bind(this);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteButtonClicked();
            }
        });
        mButtonCalendarFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDateFromButtonClicked();
            }
        });
        mButtonCalendarTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDateToButtonClicked();
            }
        });

        mTvTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if( s.length() == 0 ) {
                    mTvTitle.setError(getString(R.string.err_title_required));
                    mButtonSave.setEnabled(false);
                    return;
                }
            }
        });

        mTvFromPage.addTextChangedListener(this);
        mTvToPage.addTextChangedListener(this);
        mValuePageCount.addTextChangedListener(this);

        mDateFromSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date chosenDate = Utils.getDateFromDatePicker(view);
                mBeginDate = chosenDate;
                String formatedDate = Utils.getFormatedDateFromDatePicker(view, BookDetailActivity.this);
                mLabelSelectFromDate.setText(formatedDate);
                yearFrom = year;
                monthFrom = month;
                dayOfMonthFrom = dayOfMonth;
                if(dateIsBeforeToday(chosenDate)) {
                    mLabelSelectFromDate.setError("");
                    mLabelErrorFromDate.setVisibility(View.VISIBLE);
                    mLabelErrorFromDate.setText(getString(R.string.err_date_before_today));
                    if(!dateIsBeforeToday(mEndDate)) {
                        mLabelErrorToDate.setVisibility(View.INVISIBLE);
                        mLabelSelectToDate.setError(null);
                    }
                    mButtonSave.setEnabled(false);
                    return;
                } else {
                    if(!Utils.dateOneIsBeforeDateTwo(chosenDate, mEndDate)) {
                        mLabelSelectFromDate.setError("");
                        mLabelErrorFromDate.setVisibility(View.VISIBLE);
                        mLabelErrorFromDate.setText(getString(R.string.err_date_from_after_to));
                        mButtonSave.setEnabled(false);
                        return;
                    } else {
                        mLabelSelectFromDate.setError(null);
                        mLabelErrorFromDate.setVisibility(View.INVISIBLE);
                        mButtonSave.setEnabled(true);
                        setNbPagesAverage();
                    }
                }
            }
        };

        mDateToSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date chosenDate = Utils.getDateFromDatePicker(view);
                mEndDate = chosenDate;
                String formatedDate = Utils.getFormatedDateFromDatePicker(view, BookDetailActivity.this);
                mLabelSelectToDate.setText(formatedDate);
                yearTo = year;
                monthTo = month;
                dayOfMonthTo = dayOfMonth;

                if(dateIsBeforeToday(chosenDate)) {
                    mLabelSelectToDate.setError("");
                    mLabelErrorToDate.setVisibility(View.VISIBLE);
                    mLabelErrorToDate.setText(getString(R.string.err_date_before_today));
                    if(!dateIsBeforeToday(mBeginDate)) {
                        mLabelErrorFromDate.setVisibility(View.INVISIBLE);
                        mLabelSelectFromDate.setError(null);
                    }

                    mButtonSave.setEnabled(false);
                    return;
                } else {
                    if(!Utils.dateOneIsBeforeDateTwo(mBeginDate, chosenDate)) {
                        mLabelSelectToDate.setError("");
                        mLabelErrorToDate.setVisibility(View.VISIBLE);
                        mLabelErrorToDate.setText(getString(R.string.err_date_to_before_from));
                        mButtonSave.setEnabled(false);
                        return;
                    } else {
                        mLabelSelectToDate.setError(null);
                        if(dateIsBeforeToday(mBeginDate)) {
                            mButtonSave.setEnabled(false);
                        } else {
                            mLabelErrorFromDate.setVisibility(View.INVISIBLE);
                            mLabelSelectFromDate.setError(null);
                        }
                        mLabelErrorToDate.setVisibility(View.INVISIBLE);
                        mButtonSave.setEnabled(true);
                        setNbPagesAverage();
                    }
                }
            }
        };

        mCbx1.setOnCheckedChangeListener(this);
        mCbx2.setOnCheckedChangeListener(this);
        mCbx3.setOnCheckedChangeListener(this);
        mCbx4.setOnCheckedChangeListener(this);
        mCbx5.setOnCheckedChangeListener(this);
        mCbx6.setOnCheckedChangeListener(this);
        mCbx7.setOnCheckedChangeListener(this);

        //init Fields
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void setNbPagesAverage() {
        int result = Utils.calculateNbPagesAverage(mNbPagesToRead, mBeginDate, mEndDate, mTabWeekPlanning, mTotalDays);
        if (result == Utils.ERROR_NB_PAGES_AVERAGE) {
            mAboutNbPages.setText("ERROR_NB_PAGES_AVERAGE");
        } else {
            int seconds = mAvgNbSecByPage*result;
            String text = secondsToText(seconds);
            if(text == Utils.ERROR_NB_SECONDS_A_DAY) {
                mAboutNbPages.setText(getString(R.string.err_read_time_greater_than_day));
                mButtonSave.setEnabled(false);
            } else {
                mAboutNbPages.setText(String.valueOf(result) + " " +getString (R.string.label_pages)+ " (" + text + ")" +getString (R.string.label_per_day));
                mButtonSave.setEnabled(true);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_KEY_TEXT_TITLE, mTvTitle.getText().toString());
        outState.putString(BUNDLE_KEY_TEXT_FROM_DATE, mLabelSelectFromDate.getText().toString());
        outState.putString(BUNDLE_KEY_TEXT_TO_DATE, mLabelSelectToDate.getText().toString());
        super.onSaveInstanceState(outState);
    }


    /**
     * Save Data
     */
    public void onSaveButtonClicked() {

        Date beginDate = Utils.getDateFromFormatedDate(mLabelSelectFromDate.getText().toString(), BookDetailActivity.this);
        Date endDate = Utils.getDateFromFormatedDate(mLabelSelectToDate.getText().toString(), BookDetailActivity.this);
        boolean intervaleOk = Utils.dateOneIsBeforeDateTwo(beginDate, endDate);
        if(!intervaleOk) {
            mLabelErrorToDate.setVisibility(View.VISIBLE);
            mLabelErrorToDate.setText(getString(R.string.err_date_to_before_from));
            mLabelSelectToDate.setError("");
            mButtonSave.setEnabled(false);
            return;
        }
        String title = mTvTitle.getText().toString();
        String toPage = mTvToPage.getText().toString();
        String fromPage = mTvFromPage.getText().toString();
        Integer pageCount = Integer.parseInt(mValuePageCount.getText().toString());

        Integer fromPageNb = Integer.parseInt(fromPage);
        Integer toPageNb = Integer.parseInt(toPage);
        Integer nbPagesToRead = toPageNb - fromPageNb +1;

        String weekPlanning = null;
        Integer nbPagesRead = null;
        Integer readTimeInSeconds = null;
        Integer nbSecondsByPage = null;

        //Book Entity
        final BookEntity book = new BookEntity(ISBN_ABSENT_VALUE,  title,  pageCount,  fromPageNb,  toPageNb, nbPagesToRead,
                beginDate, endDate, weekPlanning, nbPagesRead, readTimeInSeconds, nbSecondsByPage);

            AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // insert the book only if mBookId matches DEFAULT_BOOK_ID
                // Otherwise update it. Call finish in any case
                if (mBookId == DEFAULT_BOOK_ID) {
                    // insert new book
                    mDb.bookDao().insertBook(book);
                } else {
                    //update book
                    book.setId(mBookId);
                    mDb.bookDao().updateBook(book);
                }
                finish();
            }
        });
    }

    public void onDateFromButtonClicked() {
        int y = (yearFrom != 0) ? yearFrom:year;
        int m = (monthFrom != 0) ? monthFrom:month;
        int d = (dayOfMonthFrom != 0) ? dayOfMonthFrom:dayOfMonth;

        DatePickerDialog datePickerDialog = new DatePickerDialog(BookDetailActivity.this, android.R.style.Theme_Holo_Dialog,
                mDateFromSetListener, y, m, d);

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    public void onDateToButtonClicked() {
        int y = (yearTo != 0) ? yearTo:year;
        int m = (monthTo != 0) ? monthTo:month;
        int d = (dayOfMonthTo != 0) ? dayOfMonthTo:dayOfMonth;

        DatePickerDialog datePickerDialog = new DatePickerDialog(BookDetailActivity.this, android.R.style.Theme_Holo_Dialog,
                mDateToSetListener, y, m, d);

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    public void onDeleteButtonClicked() {
        //TODO demand validation
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.bookDao().deleteBookById(mBookId);
                finish();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String fromPage = mTvFromPage.getText().toString();
        String toPage = mTvToPage.getText().toString();
        String pageCountStr = mValuePageCount.getText().toString();
        int pageCount;
        int from;
        int to;

        if(fromPage.length() > 0 && toPage.length() > 0 && pageCountStr.length() > 0) {
            pageCount = Integer.parseInt(pageCountStr);//TODO TESTER pas de string
            from = Integer.parseInt(fromPage);
            to = Integer.parseInt(toPage);
            if (from > pageCount) {
                mTvFromPage.setError(getString(R.string.err_from_page_too_big));
                mButtonSave.setEnabled(false);
                return;
            }
            if (from == 0) {
                mTvFromPage.setError(getString(R.string.err_from_page_greater_than_zero));
                mButtonSave.setEnabled(false);
                return;
            }
            if (to > pageCount) {
                mTvToPage.setError(getString(R.string.err_to_page_too_big));
                mButtonSave.setEnabled(false);
                return;
            }
            if (to == 0) {
                mTvToPage.setError(getString(R.string.err_to_page_greater_than_zero));
                mButtonSave.setEnabled(false);
                return;
            }
            if (to - from +1 <=0) {
                mTvFromPage.setError(getString(R.string.err_pages_to_read_positive));
                mTvToPage.setError(getString(R.string.err_pages_to_read_positive));
                mButtonSave.setEnabled(false);
                return;
            } else {
                mTvFromPage.setError(null);
                mTvToPage.setError(null);
            }
            mButtonSave.setEnabled(true);
            mNbPagesToRead = to-from+1;
            mValueNbPagesToRead.setText(String.valueOf(mNbPagesToRead));
            Resources res = getResources();
            mAvgNbSecByPage = res.getInteger(R.integer.avg_nb_sec_by_page);
            int total = mAvgNbSecByPage*mNbPagesToRead;
            String time = Utils.getTime(total, getString(R.string.label_hour), getString(R.string.label_minute));

            mValueTimeEstimated.setText(time);
            mValueTimeEstimated.setVisibility(View.VISIBLE);
            mValueNbPagesToRead.setVisibility(View.VISIBLE);
            mLabelNbPagesToRead.setVisibility(View.VISIBLE);

            setNbPagesAverage();

        } else {
            mValueNbPagesToRead.setVisibility(View.INVISIBLE);
            mLabelNbPagesToRead.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mTotalDays = 0;
        mTotalDays += (mCbx1.isChecked())? 1:0;
        mTotalDays += (mCbx2.isChecked())? 1:0;
        mTotalDays += (mCbx3.isChecked())? 1:0;
        mTotalDays += (mCbx4.isChecked())? 1:0;
        mTotalDays += (mCbx5.isChecked())? 1:0;
        mTotalDays += (mCbx6.isChecked())? 1:0;
        mTotalDays += (mCbx7.isChecked())? 1:0;

        mTabWeekPlanning[0] = (mCbx1.isChecked())? 1:0;
        mTabWeekPlanning[1] = (mCbx2.isChecked())? 1:0;
        mTabWeekPlanning[2] = (mCbx3.isChecked())? 1:0;
        mTabWeekPlanning[3] = (mCbx4.isChecked())? 1:0;
        mTabWeekPlanning[4] = (mCbx5.isChecked())? 1:0;
        mTabWeekPlanning[5] = (mCbx6.isChecked())? 1:0;
        mTabWeekPlanning[6] = (mCbx7.isChecked())? 1:0;

        mWeekPlanning = "";
        mWeekPlanning += (mCbx1.isChecked())? "1":"0";
        mWeekPlanning += (mCbx2.isChecked())? "1":"0";
        mWeekPlanning += (mCbx3.isChecked())? "1":"0";
        mWeekPlanning += (mCbx4.isChecked())? "1":"0";
        mWeekPlanning += (mCbx5.isChecked())? "1":"0";
        mWeekPlanning += (mCbx6.isChecked())? "1":"0";
        mWeekPlanning += (mCbx7.isChecked())? "1":"0";

        if(mTotalDays > 0) {
            setNbPagesAverage();
        }

    }
}
