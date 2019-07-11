package com.example.android.bookstudyplanner.uis;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import static com.example.android.bookstudyplanner.database.DatabaseUtils.ISBN_ABSENT_VALUE;

/**
 * Created by vanessa on 09/07/2019.
 */

public class BookDetailActivity extends AppCompatActivity {

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();

    private final String BUNDLE_KEY_TEXT_TITLE = "BUNDLE_KEY_TEXT_TITLE";
    private TextView mTvTitle;
    private TextView mTvId;
    private TextView mTvPageCount;
    private Button mButtonSave;
    private Button mButtonDelete;

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

            }
        }
    }

    private void fillLayoutFields(BookEntity item) {
        mTvTitle.setText(item.getTitle());
        mTvId.setText(String.valueOf(item.getId()));
        mTvPageCount.setText(String.valueOf(item.getPageCount()));
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mTvTitle = findViewById(R.id.tvTitle);
        mTvId = findViewById(R.id.tvId);
        mTvPageCount = findViewById(R.id.tvPageCount);
        mButtonSave = findViewById(R.id.buttonSave);
        mButtonDelete = findViewById(R.id.buttonDelete);
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

        //PageCount Validation
        int pageCount;
        String pageCountStr = mTvPageCount.getText().toString();
        if( pageCountStr.length() == 0 ) {
            mTvPageCount.setError("Page count is required!");
            return;
        } else if(Integer.parseInt(pageCountStr) <= 0) {
            mTvPageCount.setError("Page count must be positive");
            return;
        } else {
            pageCount = Integer.parseInt(mTvPageCount.getText().toString());
        }

        //Book Entity
        final BookEntity book = new BookEntity(ISBN_ABSENT_VALUE, title, pageCount);//TODO fill always pagecount

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
        final int id = Integer.parseInt(mTvId.getText().toString());

        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.bookDao().deleteBookById(id);
                finish();
            }
        });
    }

}
