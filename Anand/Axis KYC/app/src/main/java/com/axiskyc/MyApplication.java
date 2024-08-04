package com.axiskyc;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } catch (Exception e) {}
    }
}