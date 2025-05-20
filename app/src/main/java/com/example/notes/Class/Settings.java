package com.example.notes.Class;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.example.notes.LocaleHelper;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.notes.MainClass.MainActivity;
import com.example.notes.R;
import com.example.notes.ViewModels.SettingsViewModel;
import com.google.android.material.navigation.NavigationView;

public class Settings extends AppCompatActivity {

    private DrawerLayout drawer_layout;
    private TextView languageText, themeText;
    private String[] themes;
    private String[] languages = {"English", "Русский"};
    private String[] languageCodes = {"en", "ru",};
    private String[] ThemeCodes = {"day", "night", "system"};
    private ImageButton sigment2;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private LinearLayout languageLayout, themeLayout;
    private SettingsViewModel viewModel;


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

        // UI update
        loadLanguageToUI();



        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);





        viewModel.getLanguageLiveData().observe(this, lang ->{
            LocaleHelper.setLocale(this,lang);
            recreate();

        });

        languageLayout.setOnClickListener(v -> {
            showLanguageDialog();
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
                startActivity(new Intent(Settings.this, FileActivity.class));
            } else if (id == R.id.nav_arhive) {
                Intent intent = new Intent(Settings.this, Arhive.class);
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
//        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getPersistedLanguage(newBase)));
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

    //Menu language
    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogFastStyling);
        builder.setTitle(getString(R.string.choose_language));
        builder.setItems(languages, (dialog, which) -> {
            viewModel.changeLanguage(this, languageCodes[which]);
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});
        builder.create().show();
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