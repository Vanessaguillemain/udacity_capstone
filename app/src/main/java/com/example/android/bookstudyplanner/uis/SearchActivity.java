package com.example.android.bookstudyplanner.uis;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.bookservice.NetworkUtils;
import com.example.android.bookstudyplanner.bookservice.SearchTask;
import com.example.android.bookstudyplanner.database.GoogleBookMetaData;
import com.example.android.bookstudyplanner.entities.MyVolume;
import com.google.api.services.books.model.Volume;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vanessa on 22/07/2019.
 */

public class SearchActivity extends AppCompatActivity implements SearchTask.SearchListener, SearchRecyclerViewAdapter.ItemClickListener{
    // Constant for logging
    private static final String TAG = SearchActivity.class.getSimpleName();

    //For launch intent
    private static final int DETAIL_BOOK_REQUEST = 1;

    //layout elements
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.tv_error_no_book_found) TextView mErrorNoBookFound;
    @BindView(R.id.tv_error_isbn_invalid) TextView mErrorISBNInvalid;
    @BindView(R.id.search_view) SearchView mSearchView;
    @BindView(R.id.books_grid) RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;

    private boolean mInternetAvailable = false;

    //elements for search
    private SearchTask searchTask;
    private List<MyVolume> myVolumeList = new ArrayList<>();
    private String latestQuery;
    private SearchRecyclerViewAdapter searchRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_search);
        ButterKnife.bind(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3);
        searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(this, myVolumeList);
        searchRecyclerViewAdapter.setClickListener(this);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(searchRecyclerViewAdapter);

        if(savedInstanceState != null) {
            ArrayList<MyVolume> myVolumeArrayList;
            myVolumeArrayList = savedInstanceState.getParcelableArrayList(Utils.BUNDLE_KEY_VOLUME_LIST);
            boolean msgNoBook = savedInstanceState.getBoolean(Utils.BUNDLE_KEY_ERROR_NO_BOOK_FOUND);
            boolean msgIsbnInvalid = savedInstanceState.getBoolean(Utils.BUNDLE_KEY_ERROR_ISBN);
            if(msgIsbnInvalid) {
                mErrorISBNInvalid.setVisibility(View.VISIBLE);
            }
            if(msgNoBook) {
                mErrorNoBookFound.setVisibility(View.VISIBLE);
            }
            myVolumeList.addAll(myVolumeArrayList);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } else {
            mSearchView.requestFocus();
        }


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String cs) {
                if (TextUtils.isEmpty(cs)){
                    mErrorISBNInvalid.setVisibility(View.GONE);
                    mErrorNoBookFound.setVisibility(View.GONE);
                }
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                mInternetAvailable = NetworkUtils.isOnline(SearchActivity.this);
                if (mInternetAvailable) {
                    hideErrorMessageInternet();
                    searchBooks(query);
                } else {
                    showErrorMessageInternet();
                    myVolumeList.clear();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager != null && getCurrentFocus() != null)
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                mSearchView.clearFocus();

                return true;
            }
        });

        manageInternetConnection();

        mErrorMessageDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageInternetConnection();
            }
        });

        //checks permissions
        requestPermissions(savedInstanceState);
    }

    private void manageInternetConnection() {
        mInternetAvailable = NetworkUtils.isOnline(this);
        if (mInternetAvailable) {
            hideErrorMessageInternet();
        } else {
            showErrorMessageInternet();
        }
    }

    /**
     * Checks if there is an ISBN and validates it eventually. Then
     * creates a new SearchTask for the search.
     * @param query the query entered by user (title, author, isbn...)
     */
    private void searchBooks(String query) {
        if (query.equalsIgnoreCase(latestQuery)) {
            return;
        }
        boolean valid = true;
        //if it's an ISBN
        if (Utils.isInteger(query)) {
            if (query.length() == 13) {
                valid = Utils.isValidISBN13(query);
            }
            if (query.length() == 10) {
                valid = Utils.isValidISBN10(query);
            }
        }
        if(valid) {
            mErrorISBNInvalid.setVisibility(View.GONE);
            if (searchTask != null) {
                searchTask.cancel(true);
            }
            latestQuery = query;
            searchTask = new SearchTask();
            searchTask.setSearchListener(this);
            searchTask.execute(query);
        } else {
            mErrorISBNInvalid.setVisibility(View.VISIBLE);
            myVolumeList.clear();
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onSearching() {
        mProgressBar.setVisibility(View.VISIBLE);
        myVolumeList.clear();
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onResult(List<Volume> volumes) {
        mProgressBar.setVisibility(View.GONE);
        if(volumes.size() == 0) {
            mErrorNoBookFound.setVisibility(View.VISIBLE);
        } else {
            mErrorNoBookFound.setVisibility(View.GONE);
            for(Volume volume:volumes) {
                MyVolume myVolume = new MyVolume(volume);
                myVolumeList.add(myVolume);
            }
            searchRecyclerViewAdapter.setVolumes(myVolumeList);
        }
    }

    /**
     * This method will hide the error message and show the grid of Posters.
     */
    private void hideErrorMessageInternet() {
        mErrorMessageDisplay.setVisibility(View.GONE);
    }

    /**
     * This method will make the error message visible and hide the grid of Posters.
     */
    private void showErrorMessageInternet() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    private void requestPermissions(final Bundle bundle) {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(),  R.string.permission_error_occurred, Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
        builder.setTitle(R.string.permission_dialog_title);
        builder.setMessage(R.string.permission_dialog_msg);
        builder.setPositiveButton(R.string.permission_dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(R.string.permission_dialog_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onItemClick(View view, int position) {
        MyVolume volume = searchRecyclerViewAdapter.getItem(position);
        Bundle metadata = new Bundle();

        metadata.putString(GoogleBookMetaData.TITLE, volume.getVolumeInfoTitle());
        metadata.putInt(GoogleBookMetaData.PAGE_COUNT, volume.getVolumeInfoPageCount());
        metadata.putString(GoogleBookMetaData.IMAGE, volume.getVolumeInfoImageLink());

        Intent myIntent = new Intent(this, BookDetailActivity.class);
        myIntent.putExtra(Utils.INTENT_KEY_BOOK_DETAIL_ACTION, Utils.INTENT_VAL_BOOK_DETAIL_ACTION_FROM_SEARCH);
        myIntent.putExtra(Utils.INTENT_KEY_METADATA, metadata);

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(SearchActivity.this, view, getString(R.string.transition_photo));

        ActivityCompat.startActivityForResult(SearchActivity.this, myIntent, DETAIL_BOOK_REQUEST, options.toBundle());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == DETAIL_BOOK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayList<MyVolume> testing = new ArrayList<>();
        testing.addAll(myVolumeList);
        outState.putParcelableArrayList(Utils.BUNDLE_KEY_VOLUME_LIST, testing);
        if(mErrorISBNInvalid.getVisibility() == View.VISIBLE) {
            outState.putBoolean(Utils.BUNDLE_KEY_ERROR_ISBN, true);
        } else {
            outState.putBoolean(Utils.BUNDLE_KEY_ERROR_ISBN, false);
        }
        if(mErrorNoBookFound.getVisibility() == View.VISIBLE) {
            outState.putBoolean(Utils.BUNDLE_KEY_ERROR_NO_BOOK_FOUND, true);
        } else {
            outState.putBoolean(Utils.BUNDLE_KEY_ERROR_NO_BOOK_FOUND, false);
        }
        super.onSaveInstanceState(outState);
    }

}
