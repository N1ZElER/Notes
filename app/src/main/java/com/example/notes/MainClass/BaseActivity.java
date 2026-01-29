package com.example.notes.MainClass;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notes.Class.LocaleHelper;

public abstract class BaseActivity extends AppCompatActivity {

    // Загрузка языков до загузки экрана
    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = newBase
                .getSharedPreferences("settings", MODE_PRIVATE)
                .getString("language", "ru");

        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang));
    }
}
