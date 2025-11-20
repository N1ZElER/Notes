package com.example.notes.ViewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.notes.SimpleEvent.SingleLiveEvent

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val languageLiveData = SingleLiveEvent<String>()
    private val themeLiveData = SingleLiveEvent<String>()
    private val acsessLiveData = SingleLiveEvent<String>()


        fun getLanguageLiveData() : SingleLiveEvent<String>{
        return languageLiveData
    }

    fun getThemeLiveData() : SingleLiveEvent<String>{
        return themeLiveData
    }

    fun getAcsessLiveData() : SingleLiveEvent<String>{
        return acsessLiveData
    }


    fun changeLanguage(context: Context, lang: String) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("language", lang).apply()
        languageLiveData.value = lang
    }

    fun changeThemes(context: Context, theme: String){
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("themes", theme).apply()
        themeLiveData.value = theme
    }

    fun changeAcsess(context: Context, acsess: String){
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("acsess", acsess).apply()
        acsessLiveData.value = acsess
    }
}