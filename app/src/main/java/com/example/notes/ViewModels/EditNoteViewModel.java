package com.example.notes.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.notes.Note;
import com.example.notes.Repostioriy.NoteRepository;

public class EditNoteViewModel extends AndroidViewModel {

    private NoteRepository repository;
    private LiveData<Note> note;

    public EditNoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
    }

    public LiveData<Note> getNoteById(int id){
        return repository.getNoteById(id);
    }

    public void loadNoteById(int noteId) {
        note = repository.getNoteById(noteId);
    }

    public LiveData<Note> getNote(){
        return note;
    }

    public void updateNote(Note note){
        repository.update(note);
    }

    public void saveNote(Note note){
        repository.save(note);
    }

}