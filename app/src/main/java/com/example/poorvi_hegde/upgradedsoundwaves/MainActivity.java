package com.example.poorvi_hegde.upgradedsoundwaves;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;

public class MainActivity extends Activity implements OnUpdateListener {
    MySurfaceView mView;
    AudioMonitor r;

    Thread mThread;

    private static final int MY_PERMISSIONS_REQUEST = 1234;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = new MySurfaceView(this);
        setContentView(mView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();
        permissionCheck();
    }

    protected void initialize() {
        r = new AudioMonitor(this);
        if (r.isInitialized()) {
            mThread = new Thread(r);
            mThread.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (r != null && r.isInitialized()) {
            r.done = true;
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected boolean permissionCheck() {
        boolean result = false;

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            initialize();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                new AlertDialog.Builder(this)
                        .setTitle("Alert")
                        .setMessage("This application requires the RECORD_AUDIO permission in order to display audio waves on the screen. Without it, this application will not be able to function")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.RECORD_AUDIO},
                                        MY_PERMISSIONS_REQUEST);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST);
            }
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initialize();
        } else {
            ActivityCompat.finishAffinity(this);
        }
    }

    @Override
    public void update(final short[] bytes, final int length, final float sampleLength) {
        runOnUiThread(new Runnable() {
            public void run() {
                mView.setData(bytes, length, sampleLength);
            }
        });
    }

    public void quit(int errorCode) {
        if (errorCode == OnUpdateListener.ERROR_CODE_MICROPHONE_LOCKED) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Unable to capture audio. Likely permission for the microphone isn\\'t enabled. Please verify this in your device\\'s application settings.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.finishAffinity(MainActivity.this);
                        }
                    })
                    .show();
        }
    }

}
