package com.example.notes.Class;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.notes.MainClass.MainActivity;
import com.example.notes.R;
import com.google.android.material.navigation.NavigationView;

public class SpravkaAndOzevs extends AppCompatActivity {

    private DrawerLayout drawerLayout;
//    private SearchView searchView;
//    private TextView tvQuestion,tvAnswer;
    private NavigationView nav_view;
    private ImageButton sigment;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spravka_and_ozevs);
        drawerLayout = findViewById(R.id.drawerLayout);
//        searchView = findViewById(R.id.searchView);
        nav_view = findViewById(R.id.nav_view);
        sigment = findViewById(R.id.sigment);


        nav_view.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_settings) {
                startActivity(new Intent(SpravkaAndOzevs.this, Settings.class));
            } else if (id == R.id.nav_home) {
                Intent intent = new Intent(SpravkaAndOzevs.this, MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_ozevs) {
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_folder) {
                Toast.makeText(this,R.string.InProgress, Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_arhive) {
                Intent intent = new Intent(SpravkaAndOzevs.this,Arhive.class);
                startActivity(intent);
            } else if (id == R.id.nav_dell) {
                Intent intent = new Intent(SpravkaAndOzevs.this, Delete.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawers();
            return true;
        });

        sigment.setOnClickListener(v -> drawerLayout.openDrawer(nav_view));
    }

    // Update menu
    @Override
    protected void onResume() {
        super.onResume();
        updateCheckedItem();
    }

    // Update menu
    private void updateCheckedItem() {
        MenuItem item = nav_view.getMenu().findItem(getCheckedItemId());
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
                return R.id.nav_ozevs;
        }
    }
}