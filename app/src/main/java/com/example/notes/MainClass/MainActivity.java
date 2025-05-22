package com.example.notes.MainClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Class.Arhive;
import com.example.notes.Class.Delete;
import com.example.notes.Class.EditNotesAcitivty;
import com.example.notes.Class.FileActivity;
import com.example.notes.LocaleHelper;
import com.example.notes.Note;
import com.example.notes.Adapters.NoteAdapter;
import com.example.notes.NoteDatabase;
import com.example.notes.R;
import com.example.notes.Class.Settings;
import com.example.notes.Class.SpravkaAndOzevs;
import com.example.notes.ViewModels.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;
    private RecyclerView notesRecyclerView;
    private TextView countNotes;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Context context;
    private ImageButton sigment,addNoteButton;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        countNotes = findViewById(R.id.countNotes);
        addNoteButton = findViewById(R.id.addNoteButton);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        sigment = findViewById(R.id.sigment);
        searchView = findViewById(R.id.searchView);

//        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
//        String lang = prefs.getString("language", "ru");
//        LocaleHelper.setLocale(this, lang);




        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setHasFixedSize(true);


        adapter = new NoteAdapter(new ArrayList<>());
        notesRecyclerView.setAdapter(adapter);


        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);


        noteViewModel.getAllNotes().observe(this, notes -> {
            countNotes.setText("Заметок: " + (notes.size()));
            adapter.setNotes(notes != null ? notes : new ArrayList<>());
        });


        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateNote.class);
            startActivity(intent);
        });

        ItemTouchHelper itemTouchHelperMove = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,ItemTouchHelper.LEFT | 0) { // Разрешаем двигать вверх и вниз

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

            adapter.moveItem(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (position >= 0 && position < adapter.getItemCount()) {
                    showDeleteDialog(position);
                }
            }


            @Override
            public boolean isLongPressDragEnabled() {
                return true;  // перетаскивания долгим зажатием
            }
        });
        itemTouchHelperMove.attachToRecyclerView(notesRecyclerView);






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




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterNotes(query);
                updateNotesCount(adapter.getItemCount());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterNotes(newText);
                updateNotesCount(adapter.getItemCount());
                return false;
            }
        });






    }


    private void loadNotes(){
        AsyncTask.execute(()->{
            NoteDatabase db = NoteDatabase.getInstance(getApplicationContext());
            List<Note> notes = db.noteDao().getDeletedNotes();
            adapter.notifyDataSetChanged();

            runOnUiThread(() -> {
                adapter.setNotes(notes != null ? notes : new ArrayList<>());
                updateNotesCount(adapter.getItemCount());
            });
        });
    }


    public void showDeleteDialog(int position) {
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        new AlertDialog.Builder(context = this, R.style.AlertDialogFastStyling)
                .setTitle(context.getString(R.string.delete_note))
                .setMessage(context.getString(R.string.delete_context))
                .setPositiveButton(context.getString(R.string.action_delete), (dialog, which) -> moveToRecentlyDeleted(position))
                .setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> adapter.notifyItemChanged(position))
                .setCancelable(false)
                .show();
    }

    public void moveToRecentlyDeleted(int position) {
        if (position >= 0 && position < adapter.getItemCount()) {
            Note note = adapter.getNoteAt(position);
            note.setDeleted(true); // Устанавливаем статус заметки как удалённый


            // Обновляем данные в базе данных (перемещаем в корзину)
            AsyncTask.execute(() -> {
                NoteDatabase db = NoteDatabase.getInstance(getApplicationContext());
                db.noteDao().moveToTrash(note.getId()); // Перемещаем заметку в корзину в базе данных
            });

            // Уведомляем адаптер, что элемент был удалён
            runOnUiThread(() -> {
                adapter.notifyItemRemoved(position);
                updateNotesCount(adapter.getItemCount());
                loadNotes();
            });
        }
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
                return R.id.nav_home;
        }
    }

    private void updateNotesCount(int count) {
        countNotes.setText(getResources().getQuantityString(R.plurals.note_count, count, count));
    }
}

