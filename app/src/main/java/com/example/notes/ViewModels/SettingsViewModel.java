package com.example.notes.ViewModels;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.ViewModel;

import com.example.notes.SimpleEvent.SingleLiveEvent;

public class SettingsViewModel extends ViewModel {

    private final SingleLiveEvent<String> languageLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> themeLiveData = new SingleLiveEvent<>();


    public SingleLiveEvent<String> getLanguageLiveData(){
        return languageLiveData;
    }

    public SingleLiveEvent<String> getThemeLiveData(){
        return themeLiveData;
    }

    public void changeLanguage(Context context, String lang){
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

        preferences.edit().putString("language",lang).apply();

        languageLiveData.setValue(lang);
    }

    public void changeThemes(Context context, String theme){
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

        preferences.edit().putString("themes",theme).apply();

        themeLiveData.setValue(theme);
    }

}