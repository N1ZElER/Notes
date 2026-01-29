package com.example.notes.Utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper{

    public static Context setLocale(Context context, String language){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return updateResources(context,language);
        }
        return updateResourcesLegacy(context, language);
    }


    private static Context updateResources(Context context, String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    private static Context updateResourcesLegacy(Context context, String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
        return context;
    }

    public static String getPersistedLanguage(Context context) {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getString("language", Locale.getDefault().getLanguage());
    }
}