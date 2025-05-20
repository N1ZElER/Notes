package com.example.notes.Class;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.notes.MainClass.CreateNote;
import com.example.notes.MainClass.MainActivity;
import com.example.notes.Note;
import com.example.notes.R;
import com.example.notes.ViewModels.EditNoteViewModel;

public class EditNotesAcitivty extends AppCompatActivity {
    public static final String EXTRA_NOTE_ID = "note_id";

    private EditText titleEditText,contentEditText;
    private EditNoteViewModel viewModel;
    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes_acitivty);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);

        viewModel = new ViewModelProvider(this).get(EditNoteViewModel.class);

        int noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);
        if(noteId != -1){
          viewModel.loadNoteById(noteId);
          viewModel.getNote().observe(this, note -> {
              if(note != null){
                  currentNote = note;
                  titleEditText.setText(note.getTitle());
                  contentEditText.setText(note.getContent());
              }
          });
        }else{
            currentNote = new Note(0, "", "");
        }
    }

    private void SaveNote(){
        String title = titleEditText.getText().toString().trim();
        String context = contentEditText.getText().toString().trim();

        if(!title.isEmpty() || !context.isEmpty()){
            currentNote.setTitle(title);
            currentNote.setContent(context);
            viewModel.saveNote(currentNote);
        }
        AsyncTask.execute(()->{
            Intent intent = new Intent(EditNotesAcitivty.this, MainActivity.class);
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