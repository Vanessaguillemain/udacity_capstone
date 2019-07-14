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
import static com.example.android.bookstudyplanner.database.DatabaseUtils.ISBN_ABSENT_VALUE;

/**
 * Created by vanessa on 09/07/2019.
 */

public class BookDetailActivity extends AppCompatActivity implements TextWatcher{

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
                    }
                }
            }
        };

        //init Fields
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
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
            pageCount = Integer.parseInt(pageCountStr);
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
            int nbPagesToRead = to-from+1;
            mValueNbPagesToRead.setText(String.valueOf(nbPagesToRead));
            Resources res = getResources();
            int avg_nb_sec_by_page = res.getInteger(R.integer.avg_nb_sec_by_page);
            int total = avg_nb_sec_by_page*nbPagesToRead;
            String time = Utils.getTime(total, getString(R.string.label_hour), getString(R.string.label_minute));

            mValueTimeEstimated.setText(time);
            mValueTimeEstimated.setVisibility(View.VISIBLE);
            mValueNbPagesToRead.setVisibility(View.VISIBLE);
            mLabelNbPagesToRead.setVisibility(View.VISIBLE);
        } else {
            mValueNbPagesToRead.setVisibility(View.INVISIBLE);
            mLabelNbPagesToRead.setVisibility(View.INVISIBLE);
        }

    }
}
