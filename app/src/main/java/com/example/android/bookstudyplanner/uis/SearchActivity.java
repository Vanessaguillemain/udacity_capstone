package com.example.android.bookstudyplanner.uis;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.bookservice.SearchTask;
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

public class SearchActivity extends AppCompatActivity implements SearchTask.SearchListener{
    // Constant for logging
    private static final String TAG = SearchActivity.class.getSimpleName();

    //layout elements
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;

    @BindView(R.id.search_view)
    SearchView mSearchView;
    private boolean mInternetAvailable = false;

    //elements for search
    private SearchTask searchTask;
    private List<Volume> volumeList = new ArrayList<>();
    private String latestQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_search);
        ButterKnife.bind(this);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        mInternetAvailable = isOnline(this);
        if (mInternetAvailable) {
            Utils.tostS(SearchActivity.this, "internet OK");
            hideErrorMessageInternet();
            //populateRecyclerView();//TODO
        } else {
            showErrorMessageInternet();
            Utils.tostS(SearchActivity.this, "internet KO");
        }

        //checks permissions before loading Data
        requestPermissionsAndLoadData(savedInstanceState);//TODO
    }

    public void searchBooks(String query) {
        if (query.equalsIgnoreCase(latestQuery)) {
            return;
        }
        if (searchTask != null) {
            searchTask.cancel(true);
        }
        latestQuery = query;
        searchTask = new SearchTask();
        searchTask.setSearchListener(this);
        searchTask.execute(query);
    }

    @Override
    public void onSearching() {
        volumeList.clear();
    }

    @Override
    public void onResult(List<Volume> volumes) {
        volumeList = volumes;
        for(Volume v:volumes) {
            Log.d(TAG, v.toString());
        }
    }

    /**
     * This method will hide the error message and show the grid of Posters.
     */
    private void hideErrorMessageInternet() {
        mErrorMessageDisplay.setVisibility(View.GONE);
        //mRvFilmPosters.setVisibility(View.VISIBLE);//TODO
    }

    /**
     * This method will make the error message visible and hide the grid of Posters.
     */
    private void showErrorMessageInternet() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        //mRvFilmPosters.setVisibility(View.GONE);//TODO
    }


    private void requestPermissionsAndLoadData(final Bundle bundle) {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Utils.tostS(SearchActivity.this, "areAllPermissionsGranted");
                            //loadData(bundle);//TODO
                        }

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

    /**
     * This methods checks if there is a connection
     * @param context the context that will be used to check CONNECTIVITY_SERVICE
     * @return true if connected, false if not
     */
    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        }
        return false;
    }

}
