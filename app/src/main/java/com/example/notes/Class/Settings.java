package com.example.notes.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;

import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.notes.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class Settings extends AppCompatActivity {

    private TextView languageText, themeText;
    private String[] languages;
    private String[] themes;
    private String[] languageCodes = {"ru", "en"};
    private String[] ThemeCodes = {"day", "night", "system"};
    private LinearLayout languageLayout,themeLayout;
    private ImageButton sigment2;
    private NavigationView navigationView;
    private DrawerLayout drawer_layout;
    private Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("Settings", MODE_PRIVATE);
        String lang = prefs.getString("Selected_Language", "ru");

        Locale newLocale = new Locale(lang);
        Locale.setDefault(newLocale);

        Configuration config = new Configuration();
        config.setLocale(newLocale);

        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.loadLocale(this);
        setContentView(R.layout.activity_settings);

        // темная тема(не удалять)
//        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
//        String savedThemes = prefs.getString("Selected_Theme", "system");
//
//        int newMode;
//        if (savedThemes.equals("day")) {
//            newMode = AppCompatDelegate.MODE_NIGHT_NO;
//        } else if (savedThemes.equals("night")) {
//            newMode = AppCompatDelegate.MODE_NIGHT_YES;
//        } else {
//            newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
//        }
//
//        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
//            AppCompatDelegate.setDefaultNightMode(newMode);
//        }

        super.onCreate(savedInstanceState);


        themeLayout = findViewById(R.id.themeLayout);
        themeText = findViewById(R.id.themeText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.navigationView);
        drawer_layout = findViewById(R.id.drawer_layout);
        sigment2 = findViewById(R.id.sigment2);
        languageText = findViewById(R.id.languageText);
        languageLayout = findViewById(R.id.languageLayout);
        languages = new String[]{getString(R.string.language_russian), getString(R.string.language_english)};
        themes = new String[]{getString(R.string.themes_day), getString(R.string.themes_night), getString(R.string.themes_system)};


        themeLayout.setEnabled(true);
        themeLayout.setClickable(true);



        String savedTheme = loadTheme();
        themeText.setText(savedTheme.equals("day")
                ? getString(R.string.themes_day)
                : savedTheme.equals("night")
                ? getString(R.string.themes_night)
                : getString(R.string.themes_system));

        String savedLanguage = loadLanguage();
        languageText.setText(savedLanguage.equals("ru") ? getString(R.string.language_russian) : getString(R.string.language_english));

        languageLayout.setOnClickListener(v -> showLanguageDialog());
        themeLayout.setOnClickListener(v -> showThemesDialog());

        // не удалять
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_settings) {
                drawer_layout.closeDrawers();
            } else if (id == R.id.nav_home) {
                Intent intent = new Intent(Settings.this, MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_ozevs) {
                Toast.makeText(Settings.this, "Cкоро", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_folder) {
                startActivity(new Intent(Settings.this, FileActivity.class));
            } else if (id == R.id.nav_arhive) {
                Intent intent = new Intent(Settings.this, Arhive.class);
                startActivity(intent);
            } else if (id == R.id.nav_dell){
                Intent intent = new Intent(Settings.this, Delete.class);
                startActivity(intent);
            }

            drawer_layout.closeDrawers();
            return true;
        });

        sigment2.setOnClickListener(v -> drawer_layout.openDrawer(navigationView));
    }



    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogFastStyling);
        builder.setTitle(getString(R.string.choose_language));
        builder.setItems(languages, (dialog, which) -> setLanguage(languageCodes[which]));
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});
        builder.create().show();
    }

    private void showThemesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogFastStyling);
        builder.setTitle(getString(R.string.choose_themes));
        builder.setItems(themes, (dialog, which) -> setTheme(ThemeCodes[which]));
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});
        builder.create().show();
    }

    private void setTheme(String themeCode) {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String currentTheme = prefs.getString("Selected_Theme", "system");

        // Если тема не изменилась, ничего не делаем
        if (currentTheme.equals(themeCode)) {
            return;
        }

        // Сохраняем новый выбор темы
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Selected_Theme", themeCode);
        editor.apply();

        themeText.setText(themeCode.equals("day")
                ? getString(R.string.themes_day)
                : themeCode.equals("night")
                ? getString(R.string.themes_night)
                : getString(R.string.themes_system));

        // Устанавливаем новую тему
        if (themeCode.equals("day")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (themeCode.equals("night")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        // Перезапускаем Activity только при изменении темы
        recreate();
    }



    private void setLanguage(String langCode) {
        LocaleHelper.setLocale(this, langCode);
        saveLanguage(langCode);
        recreate();
    }

    private void saveLanguage(String langCode) {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Selected_Language", langCode);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);




    }

    private String loadLanguage() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        return prefs.getString("Selected_Language", "ru");
    }

    private String loadTheme() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        return prefs.getString("Selected_Theme", "system");
    }


    // обновления менюшки
    @Override
    protected void onResume() {
        super.onResume();
        updateCheckedItem();
    }

    // обновления менюшки
    private void updateCheckedItem() {
        MenuItem item = navigationView.getMenu().findItem(getCheckedItemId());
        if (item != null) {
            item.setChecked(true);
        }
    }

    // обновления менюшки
    private int getCheckedItemId() {
        String currentActivity = this.getClass().getSimpleName();
        switch (currentActivity) {
            case "NotesActivity":
                return R.id.nav_home;
            case "SettingsActivity":
                return R.id.nav_settings;
            case "ArchiveActivity":
                return R.id.nav_arhive;
            case "FolderActivity":
                return R.id.nav_folder;
            case "HelpActivity":
                return R.id.nav_ozevs;
            case "DeleteActivity":
                return R.id.nav_dell;
            default:
                return R.id.nav_settings;
        }
    }
}
