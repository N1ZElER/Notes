package com.example.notes.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.notes.MainClass.MainActivity;
import com.example.notes.R;
import com.example.notes.ViewModels.SettingsViewModel;
import com.google.android.material.navigation.NavigationView;

public class Settings extends AppCompatActivity {

    private DrawerLayout drawer_layout;
    private TextView languageText, themeText,accessText;
    private String[] languages = {"English", "Русский"};
    private String[] languageCodes = {"en", "ru",};
    private String[] ThemeCodes = {"day", "night", "system"};
    private String[] acsessCodes = {"yes", "no"};
    private ImageButton sigment2;
    private NavigationView navigationView;
    private LinearLayout languageLayout, themeLayout,allow_shared_access;
    private SettingsViewModel viewModel;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        navigationView = findViewById(R.id.navigationView);
        sigment2 = findViewById(R.id.sigment2);
        languageText = findViewById(R.id.languageText);
        themeText = findViewById(R.id.themeText);
        drawer_layout = findViewById(R.id.drawer_layout);
        languageLayout = findViewById(R.id.languageLayout);
        accessText = findViewById(R.id.accessText);
        themeLayout = findViewById(R.id.themeLayout);
        allow_shared_access = findViewById(R.id.allow_shared_access);


        // UI update
        loadLanguageToUI();
        loadThemesToUI();
        loadAccessToUI();


        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);


        viewModel.getLanguageLiveData().observe(this, lang ->{
            LocaleHelper.setLocale(this,lang);
            recreate();

        });



        languageLayout.setOnClickListener(v -> {
            showLanguageDialog();
        });

        viewModel.getThemeLiveData().observe(this, themes -> {
            switch (themes) {
                case "day":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case "night":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case "system":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                    if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                        themeText.setText(getString(R.string.themes_night));
                    } else {
                        themeText.setText(getString(R.string.themes_day));
                    }
                    break;
                default:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
            }
            recreate();
        });


        themeLayout.setOnClickListener(v->{
            showThemesDialog();

        });

        allow_shared_access.setOnClickListener(v->{
            showAcssesDialog();

        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_settings) {
                drawer_layout.closeDrawers();
            } else if (id == R.id.nav_home) {
                startActivity(new Intent(Settings.this, MainActivity.class));
            } else if (id == R.id.nav_ozevs) {
                Intent intent = new Intent(Settings.this, SpravkaAndOzevs.class);
                startActivity(intent);
            } else if (id == R.id.nav_folder) {
                Toast.makeText(this,R.string.InProgress, Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_arhive) {
                Intent intent = new Intent(Settings.this,Arhive.class);
                startActivity(intent);
            } else if (id == R.id.nav_dell) {
                Intent intent = new Intent(Settings.this, Delete.class);
                startActivity(intent);
            }

            drawer_layout.closeDrawers();
            return true;
        });

        sigment2.setOnClickListener(v -> drawer_layout.openDrawer(navigationView));




    }

    // uploading to activity
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String lang = preferences.getString("language","ru");
        Context context = LocaleHelper.setLocale(newBase,lang);
        super.attachBaseContext(context);
    }

    private void loadLanguageToUI() {
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String savedLanguage = prefs.getString("language", "ru");
        if (savedLanguage.equals("ru")) {
            languageText.setText(getString(R.string.language_russian));
        } else {
            languageText.setText(getString(R.string.language_english));
        }
    }

    private void loadAccessToUI() {
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String savedAccess = prefs.getString("acsess", "no");
        if (savedAccess.equals("yes")) {
            accessText.setText(R.string.yes);
        } else {
            accessText.setText(R.string.no);
        }
    }


    private void loadThemesToUI() {
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String savedTheme = prefs.getString("themes", "system");

        switch (savedTheme) {
            case "day":
                themeText.setText(getString(R.string.themes_day));
                break;
            case "night":
                themeText.setText(getString(R.string.themes_night));
                break;
            case "system":
                themeText.setText(getString(R.string.themes_system));
                break;
            default:
                themeText.setText(getString(R.string.themes_day));
                break;
        }
    }

    //Menu language
    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogFastStyling);
        builder.setTitle(getString(R.string.choose_language));
        builder.setItems(languages, (dialog, which) -> {
            viewModel.changeLanguage(this, languageCodes[which]);
            recreate();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});
        builder.create().show();
    }

    private void showThemesDialog() {
        String[] currentThemes = getResources().getStringArray(R.array.themes_array);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogFastStyling);
        builder.setTitle(getString(R.string.choose_themes));
        builder.setItems(currentThemes, (dialog, which) -> {
            viewModel.changeThemes(this, ThemeCodes[which]);
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});
        builder.create().show();
    }

    private void showAcssesDialog() {
        String[] acsessAll = getResources().getStringArray(R.array.acsess_array);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogFastStyling);
        builder.setTitle(R.string.AllowAccess);
        builder.setItems(acsessAll, (dialog, which) -> {
            viewModel.changeAcsess(this, acsessCodes[which]);
            if (acsessCodes[which].equals("yes")) {
                accessText.setText(R.string.yes);
            } else {
                accessText.setText(R.string.no);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});
        builder.create().show();
    }





    // Update menu
    @Override
    protected void onResume() {
        super.onResume();
        updateCheckedItem();
    }

    // Update menu
    private void updateCheckedItem() {
        MenuItem item = navigationView.getMenu().findItem(getCheckedItemId());
        if (item != null) {
            item.setChecked(true);
        }
    }

    // Update menu
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
