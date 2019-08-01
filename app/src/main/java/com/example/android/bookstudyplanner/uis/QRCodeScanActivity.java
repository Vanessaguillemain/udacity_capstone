package com.example.android.bookstudyplanner.uis;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import com.example.android.bookstudyplanner.bookservice.NetworkUtils;
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

        boolean internetAvailable = NetworkUtils.isOnline(QRCodeScanActivity.this);
        if (internetAvailable) {
            initComponents();
        } else {
            alertErrorMessageInternet();
        }
    }

    /**
     * Initializes components of activity
     */
    public void initComponents() {
        // Initialize objects
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.EAN_13|Barcode.ISBN)
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
                    Log.e("Camera start error-->> ", e.getMessage());
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
                    scanResult = barcodes.valueAt(0).displayValue;

                    searchBookAndSendToDetail(scanResult);

                }
            }
        });

    }

    /**
     * Checks if there is an ISBN and validates it eventually. Then
     * creates a new SearchTask for the search. If the search succeeds,
     * launches an intent to DetailBookActivity
     * @param barcode the code scanned
     */
    public void searchBookAndSendToDetail(String barcode) {
        boolean valid = false;
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
            if (searchTask != null) {
                searchTask.cancel(true);
            }
            searchTask = new SearchTask();
            searchTask.setSearchListener(this);
            searchTask.execute(barcode);
        } else {
            alertISBNInvalid();
        }
    }

    private void alertErrorMessageInternet() {
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.err_no_web_connection))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ask_exit_retry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .show();
    }

    private void alertISBNInvalid() {
        alertDialog(getResources().getString(R.string.err_not_isbn_valid));
    }

    private void alertNoBookFound() {
        alertDialog(getResources().getString(R.string.err_no_book_found));
    }

    private void alertDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ask_retry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //have the scan reloaded. Didn't find best solution
                        Intent intent= new Intent (getBaseContext(),QRCodeScanActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
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
            alertNoBookFound();
        } else {
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
