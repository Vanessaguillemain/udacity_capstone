package com.example.android.bookstudyplanner.uis;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstudyplanner.AddBookViewModel;
import com.example.android.bookstudyplanner.BitmapUtils;
import com.example.android.bookstudyplanner.BuildConfig;
import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.bookservice.WidgetService;
import com.example.android.bookstudyplanner.database.AddBookViewModelFactory;
import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.AppExecutor;
import com.example.android.bookstudyplanner.database.BookEntity;
import com.example.android.bookstudyplanner.database.GoogleBookMetaData;
import com.example.android.bookstudyplanner.database.PlanningEntity;
import com.example.android.bookstudyplanner.entities.MyVolume;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private final String BUNDLE_KEY_IMAGE_LINK = "BUNDLE_KEY_IMAGE_LINK";
    private final String BUNDLE_KEY_PHOTO_PATH = "BUNDLE_KEY_PHOTO_PATH";

    private final String STRING_NUMBER_PAGE_NULL = "0";
    private final String STRING_NUMBER_PAGE_ONE = "1";
    private final String STRING_DEFAULT_WEEK_PLANNING = "1111100";

    @BindView(R.id.imageBook_detail) ImageView mImageBook;
    @BindView(R.id.tvTitle) TextView mTvTitle;
    @BindView(R.id.valuePageCount) TextView mValuePageCount;
    @BindView(R.id.tvFromPage) TextView mTvFromPage;
    @BindView(R.id.tvToPage) TextView mTvToPage;
    @BindView(R.id.valueNbPagesToRead) TextView mValueNbPagesToRead;
    @BindView(R.id.labelNbPagesToRead) TextView mLabelNbPagesToRead;
    @BindView(R.id.valueTimeEstimated) TextView mValueTimeEstimated;
    @BindView(R.id.buttonSave) Button mButtonSave;
    @BindView(R.id.buttonDelete) Button mButtonDelete;
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
    @BindView(R.id.labelPlanningNbDays) TextView mTvNbTotalDays;
    @BindView(R.id.valueSpeed) TextView mValueSpeed;

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
    private int mTotalDaysByWeek = 0;
    private String mWeekPlanning;
    private int mTabWeekPlanning[] ;
    private int mNbPagesToRead;
    private int mNbPagesToReadByDay;
    private int mNbTotalDays;
    private int mAvgNbSecByPage;
    private String mImageLink;
    private BookEntity mBook;

    //boolean
    private boolean mTitleValid = false;
    private boolean mPagesToReadValid = false;
    private boolean mDatesToFromValid = false;
    private boolean mPlanningValid = false;

    // Constant for default book id to be used when not in update mode
    private static final int DEFAULT_BOOK_ID = -1;

    // Member variable for the Database
    private AppDatabase mDb;
    private int mBookId = DEFAULT_BOOK_ID;

    //for taking Picture
    Uri mFile;
    private String mPhotoPath;
    private Uri mPhotoURI;
    private Bitmap mResizedBitmap;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail1);

        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());
        // recovering the instance state
        if (savedInstanceState != null) {
            mTvTitle.setText(savedInstanceState.getString(BUNDLE_KEY_TEXT_TITLE));
            String defaultDate = getResources().getString(R.string.b_detail_pick_a_date);

            String sBegin = savedInstanceState.getString(BUNDLE_KEY_TEXT_FROM_DATE);
            String sEnd = savedInstanceState.getString(BUNDLE_KEY_TEXT_TO_DATE);
            mImageLink = savedInstanceState.getString(BUNDLE_KEY_IMAGE_LINK);
            mPhotoPath = savedInstanceState.getString(BUNDLE_KEY_PHOTO_PATH);

            //Save locally the dates to avoid them to be wipe off by data base values
            if(sBegin != null && !sBegin.equals(defaultDate)) {
                mBeginDate = Utils.getDateFromFormatedDate(sBegin, BookDetailActivity.this);
                yearFrom = Utils.getYearFromDate(mBeginDate);
                monthFrom = Utils.getMonthFromDate(mBeginDate);
                dayOfMonthFrom = Utils.getDayFromDate(mBeginDate);
            }

            if(sEnd != null && !sEnd.equals(defaultDate)) {
                mEndDate = Utils.getDateFromFormatedDate(sEnd, BookDetailActivity.this);
                yearTo = Utils.getYearFromDate(mEndDate);
                monthTo = Utils.getMonthFromDate(mEndDate);
                dayOfMonthTo = Utils.getDayFromDate(mEndDate);
            }
            mLabelSelectFromDate.setText(sBegin);
            mLabelSelectToDate.setText(sEnd);
            if(mImageLink != null) {
                Picasso.with(this).load(mImageLink).into(mImageBook);
            } else {
                mImageBook.setImageResource(R.drawable.ic_camera);
                mImageBook.setAdjustViewBounds(true);
            }
        }

        Intent intent = getIntent();
        if(intent != null) {
            String action = intent.getStringExtra(Utils.INTENT_KEY_BOOK_DETAIL_ACTION);

           if (Utils.INTENT_VAL_BOOK_DETAIL_ACTION_MODIF.equals(action)) {

               //Transition
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                   Slide slide = (Slide)TransitionInflater.from(this).inflateTransition(R.transition.slide_bottom);
                   getWindow().setEnterTransition(slide);
               }

                mBook = intent.getParcelableExtra(Utils.INTENT_KEY_BOOK);
                mBookId = mBook.getId();

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
                mTabWeekPlanning = new int[]{1,1,1,1,1,0,0};
                mWeekPlanning = STRING_DEFAULT_WEEK_PLANNING;
                mTotalDaysByWeek = 5;
                mPlanningValid = true;
               if (Utils.INTENT_VAL_BOOK_DETAIL_ACTION_FROM_SEARCH.equals(action)) {
                   Bundle metaData = intent.getBundleExtra(Utils.INTENT_KEY_METADATA);
                   String title = metaData.getString(GoogleBookMetaData.TITLE);
                   int pageCount = metaData.getInt(GoogleBookMetaData.PAGE_COUNT);
                   mTvTitle.setText(title);
                   mTvFromPage.setText(STRING_NUMBER_PAGE_ONE);
                   if(pageCount == MyVolume.NO_PAGE_COUNT) {
                       mValuePageCount.setText("");
                   } else {
                       mValuePageCount.setText(String.valueOf(pageCount));
                       mTvToPage.setText(String.valueOf(pageCount));
                   }

                   mBeginDate = Utils.getToday();
                   String sBegin = Utils.getFormatedDateFromDate(mBeginDate, BookDetailActivity.this);
                   mLabelSelectFromDate.setText(sBegin);
                   yearFrom = Utils.getYearFromDate(mBeginDate);
                   monthFrom = Utils.getMonthFromDate(mBeginDate);
                   dayOfMonthFrom = Utils.getDayFromDate(mBeginDate);

                   mImageLink = metaData.getString(GoogleBookMetaData.IMAGE);
                   if(mImageLink != null && !mImageLink.equals("")) {
                       Picasso.with(this).load(mImageLink).into(mImageBook);
                   } else {
                       mImageBook.setImageResource(R.drawable.ic_camera);
                       mImageBook.setAdjustViewBounds(true);
                   }
               } else {
                   mImageBook.setImageResource(R.drawable.ic_camera);
                   mImageBook.setAdjustViewBounds(true);
               }

           }
        }

        //Manage Picture
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mImageBook.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }

    public void takePicture(View view) {
        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mPhotoPath = photoFile.getAbsolutePath();

                String authority = BuildConfig.APPLICATION_ID + ".provider";

                // Get the content URI for the image file
                mPhotoURI = FileProvider.getUriForFile(this, authority, photoFile);

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);

                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }
    //Manage Picture
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mImageBook.setEnabled(true);
            }
        }
    }

    //Manage Picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {

                // Resample the saved image to fit the ImageView
                mResizedBitmap = BitmapUtils.getResizedBitmap(mPhotoPath, getResources().getInteger(R.integer.picture_width),getResources().getInteger(R.integer.picture_height));

                // Delete the image file, and save the resized one
                BitmapUtils.deleteImageFile(this, mPhotoPath);
                BitmapUtils.saveImage(this, mResizedBitmap, mPhotoPath);

                mImageLink = mPhotoURI.toString();
                Picasso.with(this).load(mImageLink).into(mImageBook);

            } else {
                // Otherwise, delete the temporary image file
                BitmapUtils.deleteImageFile(this, mPhotoPath);
                if(mImageLink != null) {
                    Picasso.with(this).load(mImageLink).into(mImageBook);
                } else {
                    //Picasso.with(this).load(R.drawable.ic_camera).into((ImageView) mImageBook);
                    mImageBook.setImageResource(R.drawable.ic_camera);
                    mImageBook.setAdjustViewBounds(true);
                }
            }
        }
    }

    private void fillLayoutFields(BookEntity item) {
        mImageLink = item.getImageLink();
        if(mImageLink != null) {
            Picasso.with(this).load(mImageLink).into(mImageBook);
        } else {
            mImageBook.setImageResource(R.drawable.ic_camera);
            mImageBook.setAdjustViewBounds(true);
        }

        mTvTitle.setText(item.getTitle());
        String sPageCount = String.valueOf(item.getPageCount());
        mValuePageCount.setText(sPageCount);
        mTitleValid = true;
        if(item.getNbPagesToRead() != null) {
            mValueNbPagesToRead.setText(String.valueOf(item.getNbPagesToRead()));
        }
        if(item.getFromPageNb() == null) {
            mTvFromPage.setText(STRING_NUMBER_PAGE_ONE);
        } else {
            mTvFromPage.setText(String.valueOf(item.getFromPageNb()));
        }
        if(item.getToPageNb() == null) {
            mTvToPage.setText(sPageCount);
        } else {
            mTvToPage.setText(String.valueOf(item.getToPageNb()));
        }
        mPagesToReadValid = true;
        //If mBeginDate is not null, it has been initialized in savednstanceSTate
        if(mBeginDate == null) {
            mBeginDate = item.getBeginDate();
            String sBegin;
            if (mBeginDate != null) {
                sBegin = Utils.getFormatedDateFromDate(mBeginDate, BookDetailActivity.this);
            } else {
                sBegin = Utils.getFormatedDateFromDate(Utils.getToday(), BookDetailActivity.this);
            }
            mLabelSelectFromDate.setText(sBegin);
            yearFrom = Utils.getYearFromDate(mBeginDate);
            monthFrom = Utils.getMonthFromDate(mBeginDate);
            dayOfMonthFrom = Utils.getDayFromDate(mBeginDate);
        }
        //If mEndDate is not null, it has been initialized in savedInstanceState
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
        mDatesToFromValid = true;
        mWeekPlanning = item.getWeekPlanning();
        if(mWeekPlanning != null) {
            mTabWeekPlanning = Utils.getTabWeekPlanningFromString(mWeekPlanning);
            mTotalDaysByWeek = Utils.getNbTotalWeekPlanningFromTab(mTabWeekPlanning);
        } else {
            mTabWeekPlanning = new int[]{1,1,1,1,1,0,0};
            mWeekPlanning = STRING_DEFAULT_WEEK_PLANNING;
            mTotalDaysByWeek = 5;
        }
        mPlanningValid = true;
        //set checkboxes
        setCheckBoxes(mTabWeekPlanning);
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
                    mTitleValid = false;
                } else {
                    mTitleValid = true;
                }
                setButtonSaveState();
            }
        });

        mTvFromPage.addTextChangedListener(this);
        mTvToPage.addTextChangedListener(this);
        mValuePageCount.addTextChangedListener(this);

        mDateFromSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                removeKeyBoard();
                Date chosenDate = Utils.getDateFromDatePicker(view);
                mBeginDate = chosenDate;
                String formatedDate = Utils.getFormatedDateFromDatePicker(view, BookDetailActivity.this);
                mLabelSelectFromDate.setText(formatedDate);
                yearFrom = year;
                monthFrom = month;
                dayOfMonthFrom = dayOfMonth;

                if(dateIsBeforeToday(chosenDate)) {
                    mLabelSelectFromDate.setError("");
                    Toast.makeText(getApplicationContext(), getString(R.string.err_date_before_today), Toast.LENGTH_LONG).show();
                    mDatesToFromValid = false;
                    mButtonSave.setEnabled(false);
                    mAboutNbPages.setText("");
                } else {
                    if(mEndDate!=null) {
                        if (!Utils.dateOneIsBeforeDateTwo(chosenDate, mEndDate)) {
                            mLabelSelectFromDate.setError("");
                            Toast.makeText(getApplicationContext(), getString(R.string.err_date_from_after_to), Toast.LENGTH_LONG).show();
                            mDatesToFromValid = false;
                            mAboutNbPages.setText("");
                        } else {
                            mLabelSelectFromDate.setError(null);
                            mLabelSelectToDate.setError(null);
                            mDatesToFromValid = true;
                            setNbPagesAverage();
                        }
                    } else {
                        mLabelSelectFromDate.setError(null);
                        mDatesToFromValid = false;
                    }
                }
                setButtonSaveState();
            }
        };

        mDateToSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                removeKeyBoard();
                Date chosenDate = Utils.getDateFromDatePicker(view);
                mEndDate = chosenDate;
                String formatedDate = Utils.getFormatedDateFromDatePicker(view, BookDetailActivity.this);
                mLabelSelectToDate.setText(formatedDate);
                yearTo = year;
                monthTo = month;
                dayOfMonthTo = dayOfMonth;

                if(dateIsBeforeToday(chosenDate)) {
                    mLabelSelectToDate.setError("");
                    Toast.makeText(getApplicationContext(), getString(R.string.err_date_before_today), Toast.LENGTH_LONG).show();
                    mDatesToFromValid = false;
                    mButtonSave.setEnabled(false);
                    mAboutNbPages.setText("");
                } else {
                    if(mBeginDate!=null) {
                        if (!Utils.dateOneIsBeforeDateTwo(mBeginDate, chosenDate)) {
                            mLabelSelectToDate.setError("");
                            Toast.makeText(getApplicationContext(), getString(R.string.err_date_to_before_from), Toast.LENGTH_LONG).show();

                            mDatesToFromValid = false;
                            mAboutNbPages.setText("");
                        } else {
                            mDatesToFromValid = true;
                            mLabelSelectToDate.setError(null);
                            //if new book with date before today
                            //OR date from is invalid
                            if (((mBookId == DEFAULT_BOOK_ID) && dateIsBeforeToday(mBeginDate)) || mLabelSelectFromDate.getError() != null) {
                                mDatesToFromValid = false;
                                mAboutNbPages.setText("");
                            } else {
                                mLabelSelectFromDate.setError(null);
                            }
                            setNbPagesAverage();
                        }
                    } else {
                        mLabelSelectToDate.setError(null);
                        mDatesToFromValid = false;
                    }
                }
                setButtonSaveState();
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
        Resources res = getResources();
        mAvgNbSecByPage = res.getInteger(R.integer.avg_nb_sec_by_page);
    }

    private void removeKeyBoard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputManager != null && getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setButtonSaveState() {
        if(mTitleValid && mPagesToReadValid && mDatesToFromValid && mPlanningValid) {
            mButtonSave.setEnabled(true);
        } else {
            mButtonSave.setEnabled(false);
        }
    }
    private void setNbPagesAverage() {
        mNbTotalDays = Utils.calculateNbDaysToRead(mBeginDate, mEndDate, mTabWeekPlanning, mTotalDaysByWeek);
        mNbPagesToReadByDay = Utils.calculateNbPagesAverage(mNbPagesToRead, mBeginDate, mEndDate, mTabWeekPlanning, mTotalDaysByWeek);

        if(mNbTotalDays != Utils.ERROR_NB_DAYS_TO_READ) {
            String result = this.getResources().getQuantityString(R.plurals.days_count, mNbTotalDays, mNbTotalDays);
            mTvNbTotalDays.setText(result);
        } else {
            mTvNbTotalDays.setText("");
        }

        if (mNbPagesToReadByDay == Utils.ERROR_NB_PAGES_AVERAGE) {
            mAboutNbPages.setText("");
            mPagesToReadValid = false;
            mButtonSave.setEnabled(false);
        } else if (mNbPagesToReadByDay == Utils.ERROR_NB_DAYS_TO_READ_ZERO) {
            mAboutNbPages.setText(getString(R.string.zero_days_reading));
            mPagesToReadValid = false;
            mButtonSave.setEnabled(false);
        } else {
            int seconds = mAvgNbSecByPage*mNbPagesToReadByDay;
            String text = secondsToText(seconds);
            if(text.equals(Utils.ERROR_NB_SECONDS_A_DAY)) {
                mAboutNbPages.setText(getString(R.string.err_read_time_greater_than_day));
                mPagesToReadValid = false;
                mButtonSave.setEnabled(false);
            } else {
                String result = this.getResources().getQuantityString(R.plurals.b_detail_nb_pages_to_read_by_day, mNbPagesToReadByDay, mNbPagesToReadByDay, text);
                mAboutNbPages.setText(result);
                mPagesToReadValid = true;
                setButtonSaveState();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_KEY_TEXT_TITLE, mTvTitle.getText().toString());
        outState.putString(BUNDLE_KEY_TEXT_FROM_DATE, mLabelSelectFromDate.getText().toString());
        outState.putString(BUNDLE_KEY_TEXT_TO_DATE, mLabelSelectToDate.getText().toString());
        outState.putString(BUNDLE_KEY_IMAGE_LINK, mImageLink);
        outState.putString(BUNDLE_KEY_PHOTO_PATH, mPhotoPath);

        super.onSaveInstanceState(outState);
    }


    /**
     * Save Data
     */
    private void onSaveButtonClicked() {

        Date beginDate = Utils.getDateFromFormatedDate(mLabelSelectFromDate.getText().toString(), BookDetailActivity.this);
        Date endDate = Utils.getDateFromFormatedDate(mLabelSelectToDate.getText().toString(), BookDetailActivity.this);

        boolean intervaleOk = Utils.dateOneIsBeforeDateTwo(beginDate, endDate);
        if(!intervaleOk) {
            Toast.makeText(this,getString(R.string.err_date_to_before_from), Toast.LENGTH_LONG).show();
            mLabelSelectToDate.setError("");
            mButtonSave.setEnabled(false);
            return;
        }
        String title = mTvTitle.getText().toString();
        String toPage = mTvToPage.getText().toString();
        String fromPage = mTvFromPage.getText().toString();
        Integer pageCount = Integer.parseInt(mValuePageCount.getText().toString());

        final Integer fromPageNb = Integer.parseInt(fromPage);
        final Integer toPageNb = Integer.parseInt(toPage);
        final Integer nbPagesToRead = toPageNb - fromPageNb +1;

        final Boolean newBook = (mBookId == DEFAULT_BOOK_ID);
        Integer nbPagesRead = 0;
        Integer readTimeInSeconds = null;
        Integer nbSecondsByPage = null;
        Double percentRead = 0d;
        int lastPageRead = 0;

        if(!newBook) {
            nbPagesRead = mBook.getNbPagesRead();
            readTimeInSeconds = mBook.getReadTimeInSeconds();
            nbSecondsByPage = mBook.getNbSecondsByPage();
            percentRead = Utils.getPercentRead(nbPagesRead, nbPagesToRead);
            lastPageRead = mBook.getLastPageRead();
            //TODO
        }

        mWeekPlanning = Utils.getStringWeekPlanningFromTab(mTabWeekPlanning);
        //Book Entity
        final BookEntity book = new BookEntity(ISBN_ABSENT_VALUE,  title,  pageCount,  fromPageNb,  toPageNb, nbPagesToRead,
                beginDate, endDate, mWeekPlanning, nbPagesRead, lastPageRead, readTimeInSeconds, nbSecondsByPage, mImageLink, percentRead);

        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
            // insert the book only if mBookId matches DEFAULT_BOOK_ID
            // Otherwise update it. Call finish in any case
            if (mBookId == DEFAULT_BOOK_ID) {
                // insert new book
                mBookId = (int)(mDb.bookDao().insertBook(book));
            } else {
                //update book
                book.setId(mBookId);
                mDb.bookDao().updateBook(book);
            }
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
            }
        });

        //Planning calculation and insertion
        final List<Date> planningDates = Utils.getPlanning(mBeginDate, mEndDate, mTabWeekPlanning, mTotalDaysByWeek);
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(!newBook) {
                    //Delete all previous planning before inserting new plannings
                    Date firstDate = mBeginDate;
                    mDb.planningDao().deletePlanningByBookIdAfterIncludeDate(mBookId, firstDate);
                }
                int lastBookPage = mDb.bookDao().loadToPageNbById(mBookId);
                int firstPage = fromPageNb;
                int lastPage = fromPageNb + mNbPagesToReadByDay - 1;
                int nbMinutesReading = mNbPagesToReadByDay*mAvgNbSecByPage/60;
                //inserting new planning
                for(Date d : planningDates) {
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

                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        WidgetService.startActionUpdateWidgets(BookDetailActivity.this);
    }

    private void onDateFromButtonClicked() {
        int y = (yearFrom != 0) ? yearFrom:year;
        int m = (monthFrom != 0) ? monthFrom:month;
        int d = (dayOfMonthFrom != 0) ? dayOfMonthFrom:dayOfMonth;

        DatePickerDialog datePickerDialog = new DatePickerDialog(BookDetailActivity.this, android.R.style.Theme_Holo_Dialog,
                mDateFromSetListener, y, m, d);

        if(datePickerDialog.getWindow() != null)
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private void onDateToButtonClicked() {
        int y = (yearTo != 0) ? yearTo:year;
        int m = (monthTo != 0) ? monthTo:month;
        int d = (dayOfMonthTo != 0) ? dayOfMonthTo:dayOfMonth;

        DatePickerDialog datePickerDialog = new DatePickerDialog(BookDetailActivity.this, android.R.style.Theme_Holo_Dialog,
                mDateToSetListener, y, m, d);

        if(datePickerDialog.getWindow() != null)
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private void onDeleteButtonClicked() {
        new AlertDialog.Builder(this)
            .setMessage(getResources().getString(R.string.b_detail_msg_confirm_delete))
            .setCancelable(false)
            .setPositiveButton(getResources().getString(R.string.b_detail_confirm_delete_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    AppExecutor.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                        mDb.bookDao().deleteBookById(mBookId);
                        mDb.planningDao().deletePlanningByBookId(mBookId);
                        WidgetService.startActionUpdateWidgets(BookDetailActivity.this);
                        finish();
                        }
                    });
                }
            })
            .setNegativeButton(getResources().getString(R.string.b_detail_confirm_delete_no), null)
            .show();
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
                mAboutNbPages.setText("");
                mPagesToReadValid = false;
                return;
            }
            if (from == 0) {
                mTvFromPage.setError(getString(R.string.err_from_page_greater_than_zero));
                mButtonSave.setEnabled(false);
                mAboutNbPages.setText("");
                mPagesToReadValid = false;
                return;
            }
            if (to > pageCount) {
                mTvToPage.setError(getString(R.string.err_to_page_too_big));
                mButtonSave.setEnabled(false);
                mAboutNbPages.setText("");
                mPagesToReadValid = false;
                return;
            }
            if (to == 0) {
                mTvToPage.setError(getString(R.string.err_to_page_greater_than_zero));
                mButtonSave.setEnabled(false);
                mAboutNbPages.setText("");
                mPagesToReadValid = false;
                return;
            }
            if (to - from +1 <=0) {
                mTvFromPage.setError(getString(R.string.err_pages_to_read_positive));
                mTvToPage.setError(getString(R.string.err_pages_to_read_positive));
                mButtonSave.setEnabled(false);
                mAboutNbPages.setText("");
                mPagesToReadValid = false;
                return;
            } else {
                mTvFromPage.setError(null);
                mTvToPage.setError(null);
            }
            //mButtonSave.setEnabled(true);
            mPagesToReadValid = true;
            mNbPagesToRead = to-from+1;
            mValueNbPagesToRead.setText(String.valueOf(mNbPagesToRead));

            int total = mAvgNbSecByPage*mNbPagesToRead;
            String time = Utils.getTime(total, getString(R.string.label_hour), getString(R.string.label_minute));
            mValueTimeEstimated.setText(time);
            mValueTimeEstimated.setVisibility(View.VISIBLE);
            mValueNbPagesToRead.setVisibility(View.VISIBLE);
            mLabelNbPagesToRead.setVisibility(View.VISIBLE);

            setNbPagesAverage();
            setButtonSaveState();
        } else {
            mValueTimeEstimated.setVisibility(View.INVISIBLE);
            mValueNbPagesToRead.setVisibility(View.INVISIBLE);
            mLabelNbPagesToRead.setVisibility(View.INVISIBLE);
            mPagesToReadValid = false;
            mAboutNbPages.setText("");
            mButtonSave.setEnabled(false);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        // Check which checkbox was clicked
        switch(buttonView.getId()) {
            case R.id.cbx1:
                updatePlanningDatas(isChecked, 0);
                break;
            case R.id.cbx2:
                updatePlanningDatas(isChecked, 1);
                break;
            case R.id.cbx3:
                updatePlanningDatas(isChecked, 2);
                break;
            case R.id.cbx4:
                updatePlanningDatas(isChecked, 3);
                break;
            case R.id.cbx5:
                updatePlanningDatas(isChecked, 4);
                break;
            case R.id.cbx6:
                updatePlanningDatas(isChecked, 5);
                break;
            case R.id.cbx7:
                updatePlanningDatas(isChecked, 6);
                break;
        }

        if(mTotalDaysByWeek > 0) {
            setNbPagesAverage();
            mPlanningValid = true;
            setButtonSaveState();
        } else {
            mPlanningValid = false;
            mAboutNbPages.setText(R.string.zero_days_reading);
            mButtonSave.setEnabled(false);
        }
    }

    private void updatePlanningDatas(boolean isChecked, int index) {
        if (isChecked) {
            if (mTabWeekPlanning[index] == 0) {
                mTotalDaysByWeek += 1;
                mTabWeekPlanning[index] = 1;
            }
        } else {
            if (mTabWeekPlanning[index] == 1) {
                mTotalDaysByWeek -= 1;
                mTabWeekPlanning[index] = 0;
            }
        }
    }

    private void setCheckBoxes(int[] tabPlanning) {
        mCbx1.setChecked(tabPlanning[0]==1);
        mCbx2.setChecked(tabPlanning[1]==1);
        mCbx3.setChecked(tabPlanning[2]==1);
        mCbx4.setChecked(tabPlanning[3]==1);
        mCbx5.setChecked(tabPlanning[4]==1);
        mCbx6.setChecked(tabPlanning[5]==1);
        mCbx7.setChecked(tabPlanning[6]==1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
