package com.example.android.bookstudyplanner.uis;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bookstudyplanner.AddBookViewModel;
import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.database.AddBookViewModelFactory;
import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.AppExecutor;
import com.example.android.bookstudyplanner.database.BookEntity;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.bookstudyplanner.database.DatabaseUtils.ISBN_ABSENT_VALUE;

/**
 * Created by vanessa on 09/07/2019.
 */

public class BookDetailActivity extends AppCompatActivity implements TextWatcher{

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();

    private final String BUNDLE_KEY_TEXT_TITLE = "BUNDLE_KEY_TEXT_TITLE";

    @BindView(R.id.tvTitle)
     TextView mTvTitle;
    @BindView(R.id.valuePageCount)
     TextView mValuePageCount;
    @BindView(R.id.tvFromPage)
     TextView mTvFromPage;
    @BindView(R.id.tvToPage)
     TextView mTvToPage;
    @BindView(R.id.valueNbPagesToRead)
     TextView mValueNbPagesToRead;
    @BindView(R.id.labelNbPagesToRead)
     TextView mLabelNbPagesToRead;
    @BindView(R.id.valueTimeEstimated)
     TextView mValueTimeEstimated;
    @BindView(R.id.buttonSave)
     Button mButtonSave;
    @BindView(R.id.buttonDelete)
     Button mButtonDelete;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private int mCalculatedPageCount;

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
        }

        Intent intent = getIntent();
        if(intent != null) {
            String action = intent.getStringExtra(Utils.INTENT_KEY_BOOK_DETAIL_ACTION);

            // int tabPosition = intent.getIntExtra(Utils.INTENT_KEY_TAB_POSITION, -1);
            if (Utils.INTENT_VAL_BOOK_DETAIL_ACTION_MODIF.equals(action)) {
                //TODO test if null
                BookEntity book = intent.getParcelableExtra("BOOK");
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
                mToolbar.setTitle("CREATE");
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
            mTvFromPage.setText("0");
        } else {
            mTvFromPage.setText(String.valueOf(item.getFromPageNb()));
        }
        if(item.getToPageNb() == null) {
            mTvToPage.setText("0");
        } else {
            mTvToPage.setText(String.valueOf(item.getToPageNb()));
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

        mTvTitle.addTextChangedListener(this);
        mTvFromPage.addTextChangedListener(this);
        mTvToPage.addTextChangedListener(this);
        mValuePageCount.addTextChangedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_KEY_TEXT_TITLE, mTvTitle.getText().toString());
        super.onSaveInstanceState(outState);
    }


    public void onSaveButtonClicked() {
        //Title validation
        String title = mTvTitle.getText().toString();
        if( title.length() == 0 ) {
            mTvTitle.setError("Title is required!");
            return;
        }

        //PageCount validation
        int pageCount ;
        int intFromPage;
        int intToPage;

        String pageCountStr = mValuePageCount.getText().toString();
        if( pageCountStr.length() == 0 ) {
            mValuePageCount.setError("Page count is required!");
            return;
        } else if(Integer.parseInt(pageCountStr) <= 0) {
            mValuePageCount.setError("Page count must be positive");
            return;
        } else {
            pageCount = Integer.parseInt(mValuePageCount.getText().toString());
        }

        //From Page validation
        String fromPage = mTvFromPage.getText().toString();
        if( fromPage.length() == 0 ) {
            mTvFromPage.setError("fromPage is required!");
            return;
        } else {
            intFromPage = Integer.parseInt(fromPage);
            if(intFromPage > pageCount) {
                mTvFromPage.setError("fromPage greater than total pages!");
                return;
            }
        }

        //To Page validation
        String toPage = mTvToPage.getText().toString();
        if( toPage.length() == 0 ) {
            mTvToPage.setError("toPage is required!");
            return;
        } else {
            intToPage = Integer.parseInt(toPage);
            if(intToPage > pageCount) {
                mTvToPage.setError("intToPage greater than total pages!");
                return;
            }
        }

        //From and To Page validation
        if(intToPage - intFromPage <= 0) {
            mTvFromPage.setError("Pages to read must be > 0!");
            mTvToPage.setError("Pages to read must be > 0!");
            return;
        }

        Integer fromPageNb = Integer.parseInt(fromPage);
        Integer toPageNb = Integer.parseInt(toPage);
        Integer nbPagesToRead = toPageNb - fromPageNb +1;
        Date beginDate = null;
        Date endDate = null;
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

    private void validateInputs() {

    }
    public void onDeleteButtonClicked() {

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

        String title = mTvTitle.getText().toString();
        if( title.length() == 0 ) {
            mTvTitle.setError("Title is required!");
            mButtonSave.setEnabled(false);
            return;
        }

        if(fromPage.length() > 0 && toPage.length() > 0 && pageCountStr.length() > 0) {
            pageCount = Integer.parseInt(pageCountStr);
            from = Integer.parseInt(fromPage);
            to = Integer.parseInt(toPage);
            if (from > pageCount) {
                mTvFromPage.setError("fromPage greater than total pages!");
                mButtonSave.setEnabled(false);
                return;
            }
            if (from == 0) {
                mTvFromPage.setError("fromPage must be greater than 0 !");
                mButtonSave.setEnabled(false);
                return;
            }
            if (to > pageCount) {
                mTvToPage.setError("toPage greater than total pages!");
                mButtonSave.setEnabled(false);
                return;
            }
            if (to == 0) {
                mTvToPage.setError("toPage must be greater than 0 !");
                mButtonSave.setEnabled(false);
                return;
            }
            if (to - from +1 <=0) {
                mTvFromPage.setError("pages to read must be positive!");
                mTvToPage.setError("pages to read must be positive!");
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
