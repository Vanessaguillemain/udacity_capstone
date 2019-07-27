package com.example.android.bookstudyplanner.uis;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.bookstudyplanner.MainViewModel;
import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.AppExecutor;
import com.example.android.bookstudyplanner.database.BookEntity;
import com.example.android.bookstudyplanner.database.PlanningEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();

    private TabAdapter adapter;

    @BindView(R.id.tabLayout)
     TabLayout tabLayout;
    @BindView(R.id.viewPager)
     ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fabAdd)
     FloatingActionButton fabAdd;
    @BindView(R.id.fabEdit)
     FloatingActionButton fabEdit;
    @BindView(R.id.fabSearch)
     FloatingActionButton fabSearch;
    @BindView(R.id.layoutFabEdit)
     LinearLayout layoutFabEdit;
    @BindView(R.id.layoutFabSearch)
     LinearLayout layoutFabSearch;

    private boolean fabExpanded = false;
    private static final String BUNDLE_KEY_FAB_EXPANDED = "BUNDLE_KEY_FAB_EXPANDED";
    private static final String BUNDLE_KEY_TAB_POSITION = "BUNDLE_KEY_TAB_POSITION";
    // Member variable for the Database
    private AppDatabase mDb;
    private ArrayList<BookEntity> bookEntities = new ArrayList<>();
    private ArrayList<PlanningEntity> planningTodayEntities = new ArrayList<>();
    private TabBooksFragment tabBooksFragment;
    private TabTodayFragment tabTodayFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        adapter = new TabAdapter(getSupportFragmentManager());

        mDb = AppDatabase.getInstance(getApplicationContext());
        setUpViewModel();

        tabBooksFragment = new TabBooksFragment();
        tabBooksFragment.setBookEntities(bookEntities);

        tabTodayFragment = new TabTodayFragment();
        tabTodayFragment.setPlanningEntities(planningTodayEntities);

        adapter.addFragment(tabBooksFragment, getString(R.string.tab_books_title));
        adapter.addFragment(tabTodayFragment, getString(R.string.tab_today_title));
        adapter.addFragment(new TabPlanningFragment(), getString(R.string.tab_planning_title));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        fabExpanded = false;
        // recovering the instance state
        if (savedInstanceState != null) {
            fabExpanded = savedInstanceState.getBoolean(BUNDLE_KEY_FAB_EXPANDED);
            int tabPos = savedInstanceState.getInt(BUNDLE_KEY_TAB_POSITION);
            tabLayout.getTabAt(tabPos).select();
        }

        setSupportActionBar(toolbar);
        initListeners();

        hideFABMenu(!fabExpanded);

    }

    private void initListeners() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {hideFABMenu(fabExpanded);
            }
        });

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditBook();
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchBook();
            }
        });
    }

    private void hideFABMenu(boolean toHide) {
        if (toHide) closeSubMenusFab();
        else openSubMenusFab();
    }

    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through a BookDetailActivity,
     * so this re-queries the database data for any changes.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getBooks().observe(this, new Observer<List<BookEntity>>() {
            @Override
            public void onChanged(@Nullable List<BookEntity> books) {
                Log.d(TAG, "Updating list of books from LiveData in ViewModel");
                tabBooksFragment.setBooksToAdapter(books);
                bookEntities.clear();
                bookEntities.addAll(books);
            }
        });

        viewModel.getTodays().observe(this, new Observer<List<PlanningEntity>>() {
            @Override
            public void onChanged(@Nullable List<PlanningEntity> todays) {
                Log.d(TAG, "Updating list of today books from LiveData in ViewModel");
                tabTodayFragment.setPlanningsToAdapter(todays);
                planningTodayEntities.clear();
                planningTodayEntities.addAll(todays);
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BUNDLE_KEY_FAB_EXPANDED, fabExpanded);
        outState.putInt(BUNDLE_KEY_TAB_POSITION,  tabLayout.getSelectedTabPosition());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //textView.setText(savedInstanceState.getString(TEXT_VIEW_KEY));
    }

    private void closeSubMenusFab(){
        layoutFabEdit.animate().translationY(0);
        layoutFabSearch.animate().translationY(0);
        //Change close icon to add icon
        fabAdd.setImageResource(R.drawable.ic_menu_add);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabEdit.animate().translationY(-getResources().getDimension(R.dimen.standard_60));
        layoutFabSearch.animate().translationY(-getResources().getDimension(R.dimen.standard_110));
        //Change add icon to close icon
        fabAdd.setImageResource(R.drawable.ic_menu_close);
        fabExpanded = true;
    }

    private void openEditBook(){
        Intent myIntent = new Intent(MainActivity.this, BookDetailActivity.class);
        myIntent.putExtra(Utils.INTENT_KEY_BOOK_DETAIL_ACTION, Utils.INTENT_VAL_BOOK_DETAIL_ACTION_CREATE);

        MainActivity.this.startActivity(myIntent);
        closeSubMenusFab();
    }

    private void openSearchBook(){

        //Toast.makeText(this,"open search", Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
        MainActivity.this.startActivity(myIntent);
        closeSubMenusFab();

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.b_detail_msg_confirm_exit))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.b_detail_confirm_exit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.b_detail_confirm_stay), null)
                .show();
    }
}
