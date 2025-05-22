package com.example.notes.Class;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.notes.LocaleHelper;

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.setLocale(base, LocaleHelper.getPersistedLanguage(base)));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applyTheme();
        LocaleHelper.setLocale(this, LocaleHelper.getPersistedLanguage(this));
    }

    private void applyTheme(){
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        String theme = preferences.getString("themes", "system");

        switch (theme){
            case "day":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "night":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            case "system":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
