package com.example.notes.ViewModels;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.notes.R;

public class SettingsViewModel extends ViewModel {

    private final SingleLiveEvent<String> languageLiveData = new SingleLiveEvent<>();


    public SingleLiveEvent<String> getLanguageLiveData(){
        return languageLiveData;
    }

    public void changeLanguage(Context context, String lang){
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

        preferences.edit().putString("language",lang).apply();

        languageLiveData.setValue(lang);
    }
}