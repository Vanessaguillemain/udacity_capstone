package com.example.android.bookstudyplanner.uis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstudyplanner.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vanessa on 22/07/2019.
 */

public class SearchActivity extends AppCompatActivity {
    // Constant for logging
    private static final String TAG = SearchActivity.class.getSimpleName();

    @BindView(R.id.editTextSearch)
    EditText mEditTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_search);
        ButterKnife.bind(this);

        mEditTextSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Toast.makeText(getApplicationContext(), mEditTextSearch.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

}
