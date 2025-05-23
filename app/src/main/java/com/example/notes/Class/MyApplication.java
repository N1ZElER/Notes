package com.example.notes.Class;

import static android.content.Intent.getIntent;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.notes.LocaleHelper;
import com.example.notes.MainClass.MainActivity;

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
                break;
            case "system":
                int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
                if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                } else if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_NO) {
                } else {
                }
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
