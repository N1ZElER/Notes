package com.example.notes.Class;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Adapter.NoteAdapter;
import com.example.notes.Note;
import com.example.notes.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class Arhive extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NoteAdapter noteAdapter;
    private TextView countNotes;
    private ImageButton sigment2;
    private NavigationView navigationView;
    private SearchView searchView;
    private RecyclerView notesRecyclerView;
    private List<Note> notes = new ArrayList<>();



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arhive);

        drawerLayout = findViewById(R.id.drawerLayout);
        sigment2 = findViewById(R.id.sigment2);
        navigationView = findViewById(R.id.navigationView);
        searchView = findViewById(R.id.searchView);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        countNotes = findViewById(R.id.countNotes);

        noteAdapter = new NoteAdapter(notes, this);
        notesRecyclerView.setAdapter(noteAdapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                noteAdapter.filterNotes(query);
                updateNotesCount(noteAdapter.getNoteCounts());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.filterNotes(newText);
                updateNotesCount(noteAdapter.getNoteCounts());
                return false;
            }
        });


        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_settings) {
                startActivity(new Intent(Arhive.this, Settings.class));
            } else if (id == R.id.nav_home) {
                Intent intent = new Intent(Arhive.this, MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_ozevs) {
                Intent intent = new Intent(Arhive.this, SpravkaAndOzevs.class);
                startActivity(intent);
            } else if (id == R.id.nav_folder) {
                startActivity(new Intent(Arhive.this, FileActivity.class));
            } else if (id == R.id.nav_arhive) {
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_dell) {
                Intent intent = new Intent(Arhive.this, Delete.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawers();
            return true;
        });

        sigment2.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

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
                return R.id.nav_arhive;
        }
    }

    private void updateNotesCount(int count) {
        countNotes.setText(getResources().getQuantityString(R.plurals.note_count, count, count));
    }
}