package com.example.notes.Class;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.MainClass.MainActivity;
import com.example.notes.R;
import com.google.android.material.navigation.NavigationView;

public class Delete extends AppCompatActivity {

//    private SearchView searchView;
//    private RecyclerView notesRecyclerView;
    private DrawerLayout drawerLayout;
    private ImageButton sigment;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
//        searchView = findViewById(R.id.searchView);
//        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        drawerLayout = findViewById(R.id.drawerLayout);
        sigment = findViewById(R.id.sigment);
        navigationView = findViewById(R.id.navigationView);


        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_settings) {
                startActivity(new Intent(Delete.this, Settings.class));
            } else if (id == R.id.nav_home) {
                Intent intent = new Intent(Delete.this, MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_ozevs) {
                Intent intent = new Intent(Delete.this, SpravkaAndOzevs.class);
                startActivity(intent);
            } else if (id == R.id.nav_folder) {
                Toast.makeText(this,R.string.InProgress, Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_arhive) {
                Intent intent = new Intent(Delete.this,Arhive.class);
                startActivity(intent);
            } else if (id == R.id.nav_dell) {
                drawerLayout.closeDrawers();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        sigment.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
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
                return R.id.nav_dell;
        }
    }
}