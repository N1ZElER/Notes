package com.example.notes.MainClass;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.notes.Utils.LocaleHelper;

public class MyApplication extends Application {

//    private boolean isCollapsed = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getPersistedLanguage(newBase);
        Context context = LocaleHelper.setLocale(newBase, lang);
        super.attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applyTheme();
        LocaleHelper.setLocale(this, LocaleHelper.getPersistedLanguage(this));
    }

    private void applyTheme() {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        String theme = preferences.getString("themes", "system");

        switch (theme) {
            case "day":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "night":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "system":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public boolean isCollapsed() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        return prefs.getBoolean("isCollapsed", false);
    }

    public void setCollapsed(boolean collapsed) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        prefs.edit().putBoolean("isCollapsed", collapsed).apply();
    }

}
