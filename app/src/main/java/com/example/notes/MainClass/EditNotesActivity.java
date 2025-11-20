package com.example.notes.MainClass;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.notes.Note;
import com.example.notes.R;
import com.example.notes.ViewModels.EditViewModel;

public class EditNotesActivity extends AppCompatActivity {
    public static final String EXTRA_NOTE_ID = "note_id";
    private EditText titleEditText,contentEditText;
    private EditViewModel viewModel;
    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes_acitivty);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);



        titleEditText.requestFocus();
        titleEditText.setSelection(titleEditText.getText().length());

        contentEditText.requestFocus();
        contentEditText.setSelection(contentEditText.getText().length());




        viewModel = new ViewModelProvider(this).get(EditViewModel.class);

        int noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);
        if (noteId != -1) {
            viewModel.loadNoteById(noteId);
            viewModel.getNote().observe(this, note -> {
                if (note != null) {
                    currentNote = note;
                    titleEditText.setText(note.getTitle());
                    contentEditText.setText(note.getContent());
                }
            });
        } else {
            currentNote = new Note(0, "", "");
        }
    }

    private void SaveNote() {
        if (currentNote == null) return;

        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (!title.isEmpty() || !content.isEmpty()) {
            currentNote.setTitle(title);
            currentNote.setContent(content);
            viewModel.saveNote(currentNote);
        }

        AsyncTask.execute(() -> {
            Intent intent = new Intent(EditNotesActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        SaveNote();
    }
}