package com.example.notes.Class;

import android.app.Application;
import android.content.Context;

import com.example.notes.LocaleHelper;

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.setLocale(base, LocaleHelper.getPersistedLanguage(base)));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocaleHelper.setLocale(this, LocaleHelper.getPersistedLanguage(this));
    }
}
