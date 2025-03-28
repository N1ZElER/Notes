package com.example.notes.Class;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Adapter.FolderAdapter;
import com.example.notes.FolderDao;
import com.example.notes.GeterSrter.Folder;
import com.example.notes.NoteDatabase;
import com.example.notes.R;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileActivity extends AppCompatActivity {

    private ImageButton addFolderButton;
    private RecyclerView notesRecyclerView;
    private SearchView searchView;
    private Button notesRecyclerView2;
    private TextView countFolders;
    private ImageButton sigment,sigment2;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FolderAdapter adapter;
    private ArrayList<Folder> folderList;
    private FolderDao folderDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        searchView = findViewById(R.id.searchView);

        drawerLayout = findViewById(R.id.drawerLayout);
        sigment2 = findViewById(R.id.sigment2);
        navigationView = findViewById(R.id.navigationView);
        sigment = findViewById(R.id.sigment);


        notesRecyclerView2 = findViewById(R.id.notesRecyclerView2);

        countFolders = findViewById(R.id.countFolders);

        addFolderButton = findViewById(R.id.addFolderButton);

        notesRecyclerView = findViewById(R.id.notesRecyclerView);



        NoteDatabase database = NoteDatabase.getInstance(this);
        folderDao = database.folderDao();



        // cписки и адаптеры
        folderList = new ArrayList<>();
        adapter = new FolderAdapter(FileActivity.this, folderList);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(adapter);


        observeFolder();


        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_settings) {
                Intent intent = new Intent(FileActivity.this, Settings.class);
                startActivity(intent);
            } else if (id == R.id.nav_home) {
                Intent intent = new Intent(FileActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_ozevs) {
                Intent intent = new Intent(FileActivity.this, SpravkaAndOzevs.class);
                startActivity(intent);
            } else if (id == R.id.nav_folder) {
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_arhive) {
                Toast.makeText(FileActivity.this, "Пока в доработке", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_dell){
                if (!isTaskRoot()) {
                    onBackPressed();
                } else {
                    Intent intent = new Intent(FileActivity.this, Delete.class);
                    startActivity(intent);
                }
            }
            drawerLayout.closeDrawers();
            return true;
        });


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                adapter.showDeleteDialog(position);
            }
        };
        ItemTouchHelper itemTouchHelpers = new ItemTouchHelper(simpleCallback);
        itemTouchHelpers.attachToRecyclerView(notesRecyclerView);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFolders(newText);
                return false;
            }
        });

        notesRecyclerView2.setOnClickListener(v -> finish());
        sigment2.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        addFolderButton.setOnClickListener(view -> showInputDialog());
    }



    private void filterFolders(String query){
        executorService.execute(()->{
            List<Folder> filteredFolderes = folderDao.getAllFolders();
            if(!query.isEmpty()){
                List<Folder> tempList = new ArrayList<>();
                for (Folder folder : filteredFolderes){
                    if(folder.getName().toLowerCase().contains(query.toLowerCase())){
                        tempList.add(folder);
                    }
                }
                filteredFolderes = tempList;
            }
            List<Folder> finalFilteredFolderes = filteredFolderes;
            runOnUiThread(()->{
                folderList.clear();
                folderList.addAll(finalFilteredFolderes);
                adapter.notifyDataSetChanged();
            });
        });
    }



    private void showInputDialog(){
        AlertDialog.Builder bilder = new AlertDialog.Builder(this);
        bilder.setTitle("Введи название папки");

        // поле создания папки
        final EditText input = new EditText(this);
        bilder.setView(input);



        // кнопки на панели создания папки
        bilder.setPositiveButton("Ок", ((dialog, which) -> {
            String folderName = input.getText().toString().trim();

            if (!folderName.isEmpty()) {
                // главная папка заметок
                File mainFolder = new File(getFilesDir(), "Notes");
                if (!mainFolder.exists()) {
                    mainFolder.mkdirs(); // корневая папка Notes
                }


            File folder = new File(mainFolder,folderName);


            if(!folder.exists()){
                boolean isCreated = folder.mkdirs();

                if (isCreated) {
                    Folder folderData = new Folder(folderName, folder.getAbsolutePath());

                    executorService.execute(() -> {
                        folderDao.insert(folderData);

                        runOnUiThread(() -> {
                            folderList.add(folderData);
                            adapter.notifyItemInserted(folderList.size() - 1);
                            Toast.makeText(this, "Папка " + folderName + " создана!", Toast.LENGTH_SHORT).show();
                        });
                    });
                } else {
                    Toast.makeText(this, "Ошибка при создании папки!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Папка уже существует!", Toast.LENGTH_SHORT).show();
            }
            } else {
                Toast.makeText(this, "Неверное название!", Toast.LENGTH_SHORT).show();
            }
        }));
        bilder.show();
    }

    private void observeFolder(){
        folderDao.getAllFoldersLive().observe(this,folders -> {
            folderList.clear();
            folderList.addAll(folders);
            adapter.notifyDataSetChanged();
            updateNotesCount(folders.size());
        });
    }

    private void updateNotesCount(int count) {
        String countText = CountFolder(count);
        countFolders.setText(countText);
    }

    private String CountFolder(int count) {
        if (count == 0) {
            return "0 папок";
        } else if (count == 1) {
            return "1 папка";
        } else if (count >= 2 && count <= 4) {
            return count + " папки";
        } else {
            return count + " папок";
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
                return R.id.nav_folder;
        }
    }
}