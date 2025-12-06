package com.example.notes.MainClass;

import static androidx.core.util.TypedValueCompat.dpToPx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Class.Arhive;
import com.example.notes.Class.Delete;
import com.example.notes.Note;
import com.example.notes.Adapters.NoteAdapter;
import com.example.notes.R;
import com.example.notes.Class.Settings;
import com.example.notes.Class.SpravkaAndOzevs;
import com.example.notes.ViewModels.NoteViewModel;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NoteAdapter.SelectionModeListener {
    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;
    private RelativeLayout serchBar;
    private RecyclerView notesRecyclerView;
    private TextView countNotes;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Context context;
    private ImageButton sigment,addNoteButton,razdel;
    private SearchView searchView;
    private ActionMode actionMode;




    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
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
        serchBar = findViewById(R.id.serchBar);


        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);



        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setHasFixedSize(true);




        adapter = new NoteAdapter(new ArrayList<>(), noteViewModel,this);
        adapter.setSelectionModeListener(this);
        notesRecyclerView.setAdapter(adapter);
        adapter.setItemTouchHelper(notesRecyclerView);





        noteViewModel.getAllNotes().observe(this, notes -> {
            String text = getString(R.string.note_count_hin1);

            countNotes.setText(text+ " " + notes.size());
            adapter.setNotes(notes != null ? notes : new ArrayList<>());
        });

        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateNote.class);
            startActivity(intent);
        });


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
                Toast.makeText(context,R.string.InProgress,Toast.LENGTH_SHORT).show();
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

            if(newState){
                notesRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            }else{
                notesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
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


        adapter.setCollapsed(isCollapsed);
        adapter.notifyDataSetChanged();
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
                Toast.makeText(context,R.string.InProgress, Toast.LENGTH_SHORT).show();
            } else if (id == R.id.pin) {
                noteViewModel.PinStatus();
                mode.finish();
                return true;
            } else if (id == R.id.notification) {
                Toast.makeText(context,R.string.InProgress, Toast.LENGTH_SHORT).show();
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            noteViewModel.clearSelection();
            if (serchBar != null) {
                serchBar.setVisibility(View.VISIBLE);
                serchBar.clearFocus();
            }
        }
    };

    private void updateNotesCount(int count) {
        countNotes.setText(getResources().getQuantityString(R.plurals.note_count, count, count));
    }

    @Override
    public void onSelectionModeStarted() {
        if (serchBar != null) {
            serchBar.setVisibility(View.GONE);
        }
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
                return R.id.nav_home;
        }
    }
}
