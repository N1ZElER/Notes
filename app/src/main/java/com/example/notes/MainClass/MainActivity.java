package com.example.notes.MainClass;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Class.Arhive;
import com.example.notes.Class.Delete;
import com.example.notes.Class.EditNotesAcitivty;
import com.example.notes.Class.FileActivity;
import com.example.notes.Class.MyApplication;
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
    private ImageButton sigment,addNoteButton,razdel;
    private SearchView searchView;
    private ActionMode actionMode;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;


        boolean isCollapsed = ((MyApplication) getApplication()).isCollapsed();



        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        countNotes = findViewById(R.id.countNotes);
        addNoteButton = findViewById(R.id.addNoteButton);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        sigment = findViewById(R.id.sigment);
        searchView = findViewById(R.id.searchView);
        razdel = findViewById(R.id.razdel);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);



        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setHasFixedSize(true);


        adapter = new NoteAdapter(new ArrayList<>(), noteViewModel);
        adapter.setCollapsed(isCollapsed);
        notesRecyclerView.setAdapter(adapter);





        noteViewModel.getAllNotes().observe(this, notes -> {
            String text = getString(R.string.note_count_hin1);

            countNotes.setText(text+ " " + notes.size());
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
                return true;  // move to long click
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
                Toast.makeText(context,"В Разработке",Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_arhive) {
               Intent intent = new Intent(MainActivity.this,Arhive.class);
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


        razdel.setOnClickListener(v -> {
            MyApplication app = (MyApplication) getApplication();
            boolean newState = !app.isCollapsed();
            app.setCollapsed(newState);

            adapter.setCollapsed(newState);
            adapter.notifyDataSetChanged();

        });

        noteViewModel.getSelectedNotes().observe(this, selected -> {
            adapter.notifyDataSetChanged();

            if (!selected.isEmpty() && actionMode == null) {
                actionMode = startActionMode(actionModeCallback);
            }

            if (selected.isEmpty() && actionMode != null) {
                actionMode.finish();
            }
        });


        noteViewModel.isSelectionMode().observe(this, mode -> {
            adapter.setSelectionMode(mode);
        });

    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.context_note_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // nothing to update
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                noteViewModel.deleteSelectedNotes();
                mode.finish();
                return true;
            } else if (id == R.id.action_archive) {
                Toast.makeText(context,"В доработке", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.pin) {
                noteViewModel.PinStatus();
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            noteViewModel.clearSelection();
        }
    };

    public void showDeleteDialog(int position) {
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        Note noteToDelete = adapter.getNoteAt(position);
        new AlertDialog.Builder(context = this, R.style.AlertDialogFastStyling)
                .setTitle(context.getString(R.string.delete_note))
                .setMessage(context.getString(R.string.delete_context))
                .setPositiveButton(context.getString(R.string.action_delete), (dialog, which) -> noteViewModel.delete(noteToDelete))
                .setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> adapter.notifyItemChanged(position))
                .setCancelable(false)
                .show();
    }

    private void updateNotesCount(int count) {
        countNotes.setText(getResources().getQuantityString(R.plurals.note_count, count, count));
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
                return R.id.nav_home;
        }
    }
}
