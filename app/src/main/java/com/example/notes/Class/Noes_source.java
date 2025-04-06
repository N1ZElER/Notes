package com.example.notes.Class;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notes.Adapter.FolderAdapter;
import com.example.notes.Adapter.NoteAdapter;
import com.example.notes.Note;
import com.example.notes.NoteDatabase;
import com.example.notes.R;

import java.util.ArrayList;
import java.util.Locale;

public class Noes_source extends AppCompatActivity {


    EditText noteTitle, noteContent;
    private ImageView sigmentNotes;
    private NoteAdapter adapter;
    private ArrayList <Note> NoteList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noes_source);


        noteContent = findViewById(R.id.noteContent);
        noteTitle = findViewById(R.id.noteTitle);
        sigmentNotes = findViewById(R.id.sigmentNotes);

        NoteList = new ArrayList<>();
        adapter = new NoteAdapter(Noes_source.this, NoteList);


        // не зваисимость от темы телефона
        noteContent.setTextColor(Color.BLACK);
        noteContent.setHintTextColor(Color.LTGRAY);

        // Крусор на начало
        noteTitle.requestFocus();

        sigmentNotes.setOnClickListener(v -> showButton(v));
    }


    private void showButton(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.context_menu2, popup.getMenu());


        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.view_gallery) {
//                adapter.deleteSelectedFolders();
                return true;
            } else if (id == R.id.select_notes) {
//                adapter.PinCurrentFolderNotes();
                return true;
            } else if (id == R.id.view_attachments) {
                Toast.makeText(this, "Ничего нету", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }



    private void saveNoteToDatabase(String title, String content) {
        long currentTime = System.currentTimeMillis();
        boolean pin = false;
        boolean isBox = false;
        boolean isArchived = false;


        final String folderPath = getIntent().getStringExtra("folder_path");


        String finalFolderPath = folderPath != null ? folderPath : "main";

        Note note = new Note(title, content, currentTime, isBox, pin, finalFolderPath, isArchived);

        AsyncTask.execute(() -> {
            NoteDatabase.getInstance(getApplicationContext()).noteDao().insert(note);


            runOnUiThread(() -> {
                Intent intent = new Intent(Noes_source.this, MainActivity.class);
                intent.putExtra("folder_path", finalFolderPath);
                startActivity(intent);
                finish();
            });
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveNoteIfNotEmpty();
    }

    private void saveNoteIfNotEmpty() {
        String titleText = noteTitle.getText().toString().trim();
        String contentText = noteContent.getText().toString().trim();

        if (!titleText.isEmpty() || !contentText.isEmpty()) {
            saveNoteToDatabase(titleText, contentText);
        }
    }
}
