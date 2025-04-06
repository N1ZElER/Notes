package com.example.notes.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.notes.Adapter.NoteAdapter;
import com.example.notes.Note;
import com.example.notes.NoteDao;
import com.example.notes.NoteDatabase;
import com.example.notes.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditNoteActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private Note note;
    private NoteAdapter noteAdapter;
    private List<Note> notes = new ArrayList<>();


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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);

        // не зависимость от темы телефона
        contentEditText.setTextColor(Color.BLACK);
        contentEditText.setHintTextColor(Color.LTGRAY);
        titleEditText.setTextColor(Color.BLACK);
        titleEditText.setHintTextColor(Color.LTGRAY);


        noteAdapter = new NoteAdapter(notes, this);



        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        int noteId = getIntent().getIntExtra("note_id", -1);
        if (noteId != -1) {
            loadNote(noteId);
        }

    }

    private void loadNote(int id) {
        AsyncTask.execute(() -> {
            note = NoteDatabase.getInstance(getApplicationContext()).noteDao().getNoteById(id);
            runOnUiThread(() -> {
                if (note != null) {
                    titleEditText.setText(note.getTitle());
                    contentEditText.setText(note.getContent());
                }
            });
        });
    }

    private void saveNote() {
        updateNote();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("note_updated", true);
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    private void updateNote() {
        if (note != null) {
            String newTitle = titleEditText.getText().toString().trim();
            String newContent = contentEditText.getText().toString().trim();

            if (!newTitle.equals(note.getTitle()) || !newContent.equals(note.getContent())) {
                note.setTitle(newTitle);
                note.setContent(newContent);

                new Thread(() -> {
                    NoteDatabase db = NoteDatabase.getInstance(getApplicationContext());
                    NoteDao noteDao = db.noteDao();
                    noteDao.update(note);

                    List<Note> updatedNotes = noteDao.getAllNotess();

                    runOnUiThread(() -> {
                        noteAdapter.setNotes(updatedNotes); // Обновляем адаптер
                        Log.d("EditNoteActivity", "Заметка обновлена и UI обновлен");
                    });
                }).start();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> {
            titleEditText.requestFocus();
            titleEditText.setSelection(titleEditText.getText().length());

            contentEditText.requestFocus();
            contentEditText.setSelection(contentEditText.getText().length());
        }, 200);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNoteIfNotEmpty();
    }

    private void saveNoteIfNotEmpty() {
        String titleText = titleEditText.getText().toString().trim();
        String contentText = contentEditText.getText().toString().trim();

        if (!titleText.isEmpty() || !contentText.isEmpty()) {
            saveNote();
        }
    }
}
