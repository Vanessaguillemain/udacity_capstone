package com.example.android.bookstudyplanner.uis;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.database.AppDatabase;
import com.example.android.bookstudyplanner.database.AppExecutor;
import com.example.android.bookstudyplanner.database.BookEntity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabEdit;
    private FloatingActionButton fabSearch;
    private LinearLayout layoutFabEdit;
    private LinearLayout layoutFabSearch;
    private boolean fabExpanded = false;
    private static final String BUNDLE_KEY_FAB_EXPANDED = "BUNDLE_KEY_FAB_EXPANDED";
    private static final String BUNDLE_KEY_TAB_POSITION = "BUNDLE_KEY_TAB_POSITION";
    // Member variable for the Database
    private AppDatabase mDb;
    private ArrayList<BookEntity> bookEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());

        mDb = AppDatabase.getInstance(getApplicationContext());
        retrieveBooks();

        TabBooksFragment tabBooksFragment = new TabBooksFragment();
        tabBooksFragment.setBookEntities(bookEntities);

        adapter.addFragment(tabBooksFragment, getString(R.string.tab_books_title));
        adapter.addFragment(new TabTodayFragment(), getString(R.string.tab_today_title));
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabAdd = (FloatingActionButton) this.findViewById(R.id.fabAdd);
        fabEdit = (FloatingActionButton) this.findViewById(R.id.fabEdit);
        fabSearch = (FloatingActionButton) this.findViewById(R.id.fabSearch);
        layoutFabEdit = (LinearLayout) this.findViewById(R.id.layoutFabEdit);
        layoutFabSearch = (LinearLayout) this.findViewById(R.id.layoutFabSearch);

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
        retrieveBooks();
    }

    private void retrieveBooks() {
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<BookEntity> books = mDb.bookDao().loadAllBooks();

                // We will be able to simplify this once we learn more
                // about Android Architecture Components
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(books != null) {
                            bookEntities.clear();
                            for(BookEntity book : books) {
                                bookEntities.add(book);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "nothing in db", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
        int tab_position = tabLayout.getSelectedTabPosition();
        Intent myIntent = new Intent(MainActivity.this, BookDetailActivity.class);
        myIntent.putExtra(Utils.INTENT_KEY_BOOK_DETAIL_ACTION, Utils.INTENT_VAL_BOOK_DETAIL_ACTION_CREATE);

        MainActivity.this.startActivity(myIntent);
        closeSubMenusFab();
    }

    private void openSearchBook(){
        Toast.makeText(this,"open search", Toast.LENGTH_SHORT).show();
    }
}
