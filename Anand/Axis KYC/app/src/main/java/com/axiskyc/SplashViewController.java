package com.axiskyc;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.axiskyc.custom.Global;

public class SplashViewController extends BaseActivity {

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Global.controllerName = "SplashViewController";

        CheckPermission();
    }

    public void CheckPermission() {
        try {
            int MyVersion = android.os.Build.VERSION.SDK_INT;
            if (MyVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (!checkIfAlreadyhavePermission()) {
                    requestForSpecificPermission();
                } else {
                    goto_next_controller();
                }
            } else {
                goto_next_controller();
            }
        } catch (Exception ee) {
            goto_next_controller();
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (result == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        }, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    //granted
                    goto_next_controller();
                } else {
                    //not granted
                    //CheckPermission();

                    Toast.makeText(SplashViewController.this, "Enable permissions...", Toast.LENGTH_LONG).show();

                    android.content.Intent intent = new android.content.Intent();
                    intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void goto_next_controller() {
        Runnable priceRunnable = new Runnable() {
            public void run() {

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                android.os.Message msg = new android.os.Message();
                msg.what = 1;
                ResponseHandler.sendMessage(msg);
            }
        };
        Thread th = new Thread(null, priceRunnable, "thread");
        th.start();
    }

    public android.os.Handler ResponseHandler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:

                    Global.user_id = Global.ReadFileFromAppCache(SplashViewController.this, "user_id");
                    if (Global.user_id.equalsIgnoreCase("") || Global.user_id.equalsIgnoreCase("null")) {

                        Intent in = new Intent();
                        in.setClass(SplashViewController.this, LoginViewController.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);

                    } else {

                        Intent in = new Intent();
                        in.setClass(SplashViewController.this, MainViewController.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                    }

                    super.handleMessage(msg);
            }
        }
    };
}