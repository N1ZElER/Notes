package com.example.notes.Class;



import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Adapter.NoteAdapter;
import com.example.notes.Note;
import com.example.notes.NoteDao;
import com.example.notes.NoteDatabase;
import com.example.notes.R;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Delete extends AppCompatActivity {

    private TextView  countNotes;
    private NoteAdapter noteAdapter;
    private SearchView searchView;
    private List<Note> notes = new ArrayList<>();
    private ImageButton sigment;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView notesRecyclerView;
    private static List<Note> recentlyDeletedNotes = new ArrayList<>();


    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadDeletedNotes();
        }
    };


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        searchView = findViewById(R.id.searchView);
        sigment = findViewById(R.id.sigment);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        countNotes = findViewById(R.id.countNotes);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);


        noteAdapter = new NoteAdapter(notes, this);
        notesRecyclerView.setAdapter(noteAdapter);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter.setOnNoteCountChangeListener(this::updateNotesCount);

        loadDeletedNotes();


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
                startActivity(new Intent(Delete.this, FileActivity.class));
            } else if (id == R.id.nav_arhive) {
                Intent intent = new Intent(Delete.this, Arhive.class);
                startActivity(intent);
            } else if (id == R.id.nav_dell) {
                drawerLayout.closeDrawers();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        sigment.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));


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


        if (getIntent().hasExtra("deleted_note")) {
            Note deletedNote = getIntent().getParcelableExtra("deleted_note");
            if (deletedNote != null) {
                notes.add(deletedNote);
                noteAdapter.notifyDataSetChanged();
            }
        }


        if (getIntent().hasExtra("recentlyDeletedNotes")) {
            notes = getIntent().getParcelableArrayListExtra("recentlyDeletedNotes");
            noteAdapter.setNotes(notes);
            noteAdapter.notifyDataSetChanged();
        }


        updateNotesCount(notes.size());


        ItemTouchHelper itemTouchHelperMove = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | 0) { // Разрешаем двигать вверх и вниз

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                noteAdapter.moveItem(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                noteAdapter.showDeleteDialog(position);
            }


            @Override
            public boolean isLongPressDragEnabled() {
                return true;  // перетаскивания долгим зажатием
            }
        });

        itemTouchHelperMove.attachToRecyclerView(notesRecyclerView);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateReceiver); // Удаляем ресивер при закрытии активити
        noteAdapter.notifyDataSetChanged();
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

    private void updateNotesCount(int count) {
        countNotes.setText(getResources().getQuantityString(R.plurals.note_count, count, count));
    }


    private void loadDeletedNotes() {
        //следим за удаленной заметкой и обновляем ее в бд
        NoteDatabase.getInstance(getApplicationContext())
                .noteDao()
                .getDeletedNotes()
                .observe(this, deletedNotes -> {
                    recentlyDeletedNotes.clear();
                    recentlyDeletedNotes.addAll(deletedNotes);
                    noteAdapter.setNotes(recentlyDeletedNotes);
                    noteAdapter.notifyDataSetChanged();
                    updateNotesCount(recentlyDeletedNotes.size());
                });
    }
}