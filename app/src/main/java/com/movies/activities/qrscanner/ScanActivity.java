package com.movies.activities.qrscanner;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.movies.R;
import com.movies.dialog.BaseDialogFragment;
import com.movies.dialog.PositiveNegativeDialog;
import com.movies.utils.Global;
import com.movies.utils.PermissionHandler;

import java.io.IOException;


/**
 * This activity request camera permission and scan barcode,
 * just call with activityForResult and you can take with TAG_QR_SCANNER from intent in onActivityResult when scan cancel
 */
public class ScanActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 23;
    public static final String TAG_QR_SCANNER = "TAG_QR_SCANNER";
    private SurfaceView cameraView;
    private BarcodeDetector barcode;
    private CameraSource cameraSource;
    private PermissionHandler permissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Global.setFullScreen(this);
        setContentView(R.layout.activity_scan);
        checkIfHavePermissionAndStart();
    }

    @SuppressLint("MissingPermission")
    private void startInitAll() {
        initViews();
        initObjects();
        initListeners();
        if (!barcode.isOperational()) {
            Toast.makeText(getApplicationContext(), "Sorry, Couldn't setup the detector", Toast.LENGTH_LONG).show();
            this.finish();
        }
        try {
            cameraSource.start(cameraView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                checkIfHavePermissionAndStart();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        this.barcode.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0) {
                    whenBarcodeScan(barcodes.valueAt(0));
                }
            }
        });
    }

    private void showDialogNoHavePermission() {
        View.OnClickListener listenerNegative = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDialogFragment dialogFragment = (BaseDialogFragment) v.getTag();
                dialogFragment.dismiss();
                finish();
            }
        };
        View.OnClickListener listenerPositive = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDialogFragment dialogFragment = (BaseDialogFragment) v.getTag();
                dialogFragment.dismiss();
                permissionHandler.requestPermission();

            }
        };
        PositiveNegativeDialog positiveNegativeDialog = PositiveNegativeDialog.createInstance(getString(R.string.permission), getString(R.string.body_message_allow_permission), getString(R.string.back), getString(R.string.allow), listenerNegative, listenerPositive);
        positiveNegativeDialog.show(getSupportFragmentManager());
    }

    private void whenBarcodeScan(Barcode barcode) {
        Intent intent = new Intent();
        intent.putExtra(TAG_QR_SCANNER, barcode);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void checkIfHavePermissionAndStart() {
        permissionHandler = new PermissionHandler(this, REQUEST_CODE);
        permissionHandler.init(
                new Runnable() {
                    @Override
                    public void run() {
                        startInitAll();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        showDialogNoHavePermission();
                    }
                }, Manifest.permission.CAMERA);
        permissionHandler.requestPermission();
    }


    private void initObjects() {

        cameraView.setZOrderMediaOverlay(true);

        barcode = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920, 1024)
                .build();

    }

    private void initViews() {
        cameraView = findViewById(R.id.camera_view);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults, true);
    }

}

