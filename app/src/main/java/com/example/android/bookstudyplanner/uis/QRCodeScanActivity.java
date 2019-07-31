package com.example.android.bookstudyplanner.uis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.example.android.bookstudyplanner.R;
import com.example.android.bookstudyplanner.Utils;
import com.example.android.bookstudyplanner.bookservice.SearchTask;
import com.example.android.bookstudyplanner.database.GoogleBookMetaData;
import com.example.android.bookstudyplanner.entities.MyVolume;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.api.services.books.model.Volume;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vanessa on 31/07/2019.
 */

public class QRCodeScanActivity extends AppCompatActivity implements SearchTask.SearchListener {

    //Layout Elements
    @BindView(R.id.tv_error_isbn_invalid)
    TextView mErrorISBNInvalid;
    @BindView(R.id.tv_error_no_book_found) TextView mErrorNoBookFound;

    //Elements for scan
    @BindView(R.id.surfaceQRScanner) SurfaceView surfaceQRScanner;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    String scanResult = "";

    //Element for search
    private SearchTask searchTask;

    //For launch intent
    private static final int DETAIL_BOOK_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);
        ButterKnife.bind(this);
        initComponents();
    }

    /**
     * Initializes components of activity
     */
    public void initComponents() {
        // Initialize objects
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setRequestedPreviewSize(1024, 768)
                .setAutoFocusEnabled(true)
                .build();

        // Add Callback method to SurfaceView
        surfaceQRScanner.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    // Ask user to allow access of camera
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceQRScanner.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(QRCodeScanActivity.this, new
                                String[]{Manifest.permission.CAMERA}, 1024);
                    }
                } catch (IOException e) {
                    Log.e("Camera start error-->> ", e.getMessage().toString());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // Add Processor to Barcode detector
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0) {

                    //release detector
                    barcodeDetector.release();

                    //Beep sound
                    ToneGenerator toneNotification = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                    toneNotification.startTone(ToneGenerator.TONE_PROP_BEEP, 150);

                    //Text result
                    scanResult = barcodes.valueAt(0).displayValue.toString();

                    searchBookAndSendToDetail(scanResult);
                    /*
                    Intent intent = new Intent(QRCodeScanActivity.this, ScanResultActivity.class);
                    intent.putExtra("ScanResult", scanResult);
                    startActivity(intent);*/
                }
            }
        });
    }

    /**
     * Checks if there is an ISBN and validates it eventually. Then
     * creates a new SearchTask for the search. If the search succeeds,
     * launches an intent to DetailBookActivity
     * @param barcode
     */
    public void searchBookAndSendToDetail(String barcode) {
       boolean valid = true;
        //if it's an ISBN
        if (Utils.isInteger(barcode)) {
            if (barcode.length() == 13) {
                valid = Utils.isValidISBN13(barcode);
            }
            if (barcode.length() == 10) {
                valid = Utils.isValidISBN10(barcode);
            }
        }
        if(valid) {
            mErrorISBNInvalid.setVisibility(View.GONE);
            if (searchTask != null) {
                searchTask.cancel(true);
            }
            searchTask = new SearchTask();
            searchTask.setSearchListener(this);
            searchTask.execute(barcode);
        } else {
            mErrorISBNInvalid.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initComponents();
    }

    @Override
    public void onSearching() {
    }

    @Override
    public void onResult(List<Volume> volumes) {
        if(volumes.size() == 0) {
            mErrorNoBookFound.setVisibility(View.VISIBLE);
        } else {
            mErrorNoBookFound.setVisibility(View.GONE);
            MyVolume myVolume = new MyVolume(volumes.get(0));
            sendBookToDetail(myVolume);
        }
    }

    private void sendBookToDetail(MyVolume volume) {
        Bundle metadata = new Bundle();

        metadata.putString(GoogleBookMetaData.TITLE, volume.getVolumeInfoTitle());
        metadata.putInt(GoogleBookMetaData.PAGE_COUNT, volume.getVolumeInfoPageCount());
        metadata.putString(GoogleBookMetaData.IMAGE, volume.getVolumeInfoImageLink());

        Intent myIntent = new Intent(this, BookDetailActivity.class);
        myIntent.putExtra(Utils.INTENT_KEY_BOOK_DETAIL_ACTION, Utils.INTENT_VAL_BOOK_DETAIL_ACTION_FROM_SEARCH);
        myIntent.putExtra(Utils.INTENT_KEY_METADATA, metadata);

        startActivityForResult(myIntent, DETAIL_BOOK_REQUEST);

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
}
