package com.example.notes.Class;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Adapters.NoteAdapter;
import com.example.notes.MainClass.MainActivity;
import com.example.notes.R;
import com.example.notes.ViewModels.NoteViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class Arhive extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NoteViewModel noteViewModel;
    private ImageButton sigment;
//    private ImageButton razdel;
//    private SearchView searchView;
    private RecyclerView notesRecyclerView;
    private TextView countNotes;
    private NavigationView navigationView;
    private Context context;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arhive);
        context = this;
        drawerLayout = findViewById(R.id.drawerLayout);
        sigment = findViewById(R.id.sigment);
//        razdel = findViewById(R.id.razdel);
//        searchView = findViewById(R.id.searchView);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        countNotes = findViewById(R.id.countNotes);
        navigationView = findViewById(R.id.navigationView);


        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(new ArrayList<>(), noteViewModel);
        notesRecyclerView.setAdapter(adapter);


        NoteViewModel noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

//        noteViewModel.getArchivedNotes().observe(this, notes -> {
//            adapter.setNotes(notes != null ? notes : new ArrayList<>());
//        });




        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_settings) {
                startActivity(new Intent(Arhive.this, Settings.class));
            } else if (id == R.id.nav_home) {
                Intent intent = new Intent(Arhive.this,MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_ozevs) {
                Intent intent = new Intent(Arhive.this, SpravkaAndOzevs.class);
                startActivity(intent);
            } else if (id == R.id.nav_folder) {
                Toast.makeText(context,R.string.InProgress,Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_arhive) {
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_dell) {
                Intent intent = new Intent(Arhive.this, Delete.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawers();
            return true;
        });

        sigment.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
    }

    // notify menu
    @Override
    protected void onResume() {
        super.onResume();
        updateCheckedItem();
    }

    // notify menu
    private void updateCheckedItem() {
        MenuItem item = navigationView.getMenu().findItem(getCheckedItemId());
        if (item != null) {
            item.setChecked(true);
        }
    }

    // notify menu
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
                return R.id.nav_arhive;
        }
    }
}