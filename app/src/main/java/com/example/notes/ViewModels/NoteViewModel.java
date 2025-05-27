package com.example.notes.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.notes.Note;
import com.example.notes.Repostioriy.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private MutableLiveData<List<Note>> notes = new MutableLiveData<>();
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    public void insert(Note note){
        repository.insert(note);
    }

    public LiveData<List<Note>> getAllNotes(){
       return allNotes;
    }

    public LiveData<List<Note>> getNotes(){
        return notes;
    }

    public void delete(Note note){
        repository.delete(note);
    }


}