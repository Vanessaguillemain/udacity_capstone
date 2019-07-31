package com.example.android.bookstudyplanner.uis;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.android.bookstudyplanner.R;

/**
 * Created by vanessa on 31/07/2019.
 */

public class ScanResultActivity extends AppCompatActivity {
    TextView txtScanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        txtScanResult = (TextView) findViewById(R.id.txtScanResult); /* Find TextView and initialize it to object */

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String scanResult = extras.getString("ScanResult"); /* Retrieving text of QR Code */
            txtScanResult.setText(scanResult);
        }
    }
}
