package com.example.notes.MainClass;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.notes.Note;
import com.example.notes.R;
import com.example.notes.ViewModels.NoteViewModel;

public class CreateNote extends AppCompatActivity {

    private EditText noteTitle, noteContent;

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        noteTitle = findViewById(R.id.noteTitle);
        noteContent = findViewById(R.id.noteContent);

        noteTitle.requestFocus();

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
    }

    private void SaveNotes(){
        long currentTime = System.currentTimeMillis();

        String title = noteTitle.getText().toString().trim();
        String content = noteContent.getText().toString().trim();

        if (!title.isEmpty() || !content.isEmpty()){
            Note note = new Note(title,content,currentTime);
            noteViewModel.insert(note);
        }
        AsyncTask.execute(()->{
            Intent intent = new Intent(CreateNote.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        SaveNotes();
    }
}
