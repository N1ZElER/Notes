package com.example.notes.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Note;
import com.example.notes.Adapter.NoteAdapter;
import com.example.notes.NoteDatabase;
import com.example.notes.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteCountChangeListener {

    private RecyclerView notesRecyclerView;
    private static NoteAdapter noteAdapter;
    private SearchView searchView;
    private ImageButton addNoteButton;
    private TextView countNotes;
    private String folderPath;
    private View sigment;
    private Context context;
    private static List<Note> notes = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static List<Note> recentlyDeletedNotes = new ArrayList<>();

    private static final int REQUEST_CODE_EDIT_NOTE = 1;


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


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.loadLocale(this);
        setContentView(R.layout.activity_main);

        // язык
        LocaleHelper.setLocale(getApplicationContext(), loadLanguage());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);



        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        countNotes = findViewById(R.id.countNotes);
        addNoteButton = findViewById(R.id.addNoteButton);
        searchView = findViewById(R.id.searchView);

        sigment = findViewById(R.id.sigment);

        noteAdapter = new NoteAdapter(notes, this);
        notesRecyclerView.setAdapter(noteAdapter);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter.setOnNoteCountChangeListener(this::updateNotesCount);

        context = MainActivity.this;
        folderPath = getIntent().getStringExtra("folder_path");
        loadNotes();

        addNoteButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Noes_source.class)));

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

        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Noes_source.class);
            startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
        });


        noteAdapter.setOnNoteClickListener(note -> {
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            intent.putExtra("note_id", note.getId());
            startActivity(intent);
        });
        updateCheckedItem();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, Settings.class));
            } else if (id == R.id.nav_home) {
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_ozevs) {
                Intent intent = new Intent(MainActivity.this, SpravkaAndOzevs.class);
                startActivity(intent);
            } else if (id == R.id.nav_folder) {
                startActivity(new Intent(MainActivity.this, FileActivity.class));
            } else if (id == R.id.nav_arhive) {
                Intent intent = new Intent(MainActivity.this, Arhive.class);
                startActivity(intent);
            } else if (id == R.id.nav_dell) {
                Intent intent = new Intent(MainActivity.this, Delete.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawers();
            return true;
        });

        sigment.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        ItemTouchHelper itemTouchHelperMove = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,ItemTouchHelper.LEFT | 0) { // Разрешаем двигать вверх и вниз

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

                if (position >= 0 && position < notes.size()) {
                    showDeleteDialog(position);
                } else {
                    noteAdapter.notifyItemChanged(position);
                }
            }


            @Override
            public boolean isLongPressDragEnabled() {
                return true;  // перетаскивания долгим зажатием
            }
        });

        itemTouchHelperMove.attachToRecyclerView(notesRecyclerView);
    }

    private void loadNotes() {
        AsyncTask.execute(() -> {
            if (folderPath == null) {
                folderPath = "main";
            }

            List<Note> loadedNotes = NoteDatabase.getInstance(getApplicationContext())
                    .noteDao()
                    .getNotesByFolder(folderPath);

            runOnUiThread(() -> {
                notes.clear();
                notes.addAll(loadedNotes);
                noteAdapter.setNotes(notes);
                noteAdapter.notifyDataSetChanged();
                updateNotesCount(notes.size());
            });
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_NOTE && resultCode == RESULT_OK) {
            loadNotes();
        }
    }

    private void updateNotesCount(int count) {
        countNotes.setText(getResources().getQuantityString(R.plurals.note_count, count, count));
    }

    @Override
    public void onNoteCountChanged(int count) {
        updateNotesCount(count);
    }

    private String loadLanguage() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        return prefs.getString("Selected_Language", "ru"); // По умолчанию русский
    }



    // обновления менюшки
    @Override
    protected void onResume() {
        super.onResume();
        updateCheckedItem();
        loadNotes();
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
                return R.id.nav_home;
        }
    }

    public void showDeleteDialog(int position) {
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        new AlertDialog.Builder(context, R.style.AlertDialogFastStyling)
                .setTitle("Удалить заметку?")
                .setMessage("Вы действительно хотите удалить эту заметку?")
                .setPositiveButton("Удалить", (dialog, which) -> moveToRecentlyDeleted(position))
                .setNegativeButton("Отмена", (dialog, which) -> noteAdapter.notifyItemChanged(position))
                .setCancelable(false)
                .show();
    }

    public void moveToRecentlyDeleted(int position) {
        if (position >= 0 && position < notes.size()) {
            Note note = notes.get(position);
            note.setDeleted(true); // Устанавливаем статус заметки как удалённый

            // Добавляем заметку в список недавно удалённых (если нужно)
            recentlyDeletedNotes.add(note);

            // Обновляем данные в базе данных (перемещаем в корзину)
            AsyncTask.execute(() -> {
                NoteDatabase db = NoteDatabase.getInstance(getApplicationContext());
                db.noteDao().moveToTrash(note.getId()); // Перемещаем заметку в корзину в базе данных
            });

            // Убираем заметку из текущего списка
            notes.remove(position);

            // Уведомляем адаптер, что элемент был удалён
            runOnUiThread(() -> {
                noteAdapter.notifyItemRemoved(position);
                updateNotesCount(notes.size()); // Обновляем количество заметок
                loadNotes();
            });
        }
    }
}