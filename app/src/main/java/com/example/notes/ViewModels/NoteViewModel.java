package com.example.notes.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.notes.Note;
import com.example.notes.Repostioriy.NoteRepository;

import java.util.ArrayList;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private MutableLiveData<List<Note>> notes = new MutableLiveData<>();
    private final LiveData<List<Note>> pinnedNotes;
    private LiveData<List<Note>> allNotes;
    private final MutableLiveData<List<Note>> archivedNotes = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Note>> selectedNotes = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> selectionMode = new MutableLiveData<>(false);

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        pinnedNotes = repository.getPinnedNotes();
        allNotes = repository.getAllNotes();
    }

    public void insert(Note note){
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
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

//    public void archiveNote(Note note) {
//        note.setArchived(true);
//
//        List<Note> currentList = archivedNotes.getValue();
//        if (currentList != null) {
//            currentList.add(note);
//            archivedNotes.setValue(currentList);
//        }
//    }

    public LiveData<List<Note>> getArchivedNotes() {
        return archivedNotes;
    }


    public LiveData<List<Note>> getSelectedNotes(){
        return selectedNotes;
    }

    public LiveData<Boolean> isSelectionMode(){
        return selectionMode;
    }

    public void toggleSelection(Note note) {
        List<Note> current = selectedNotes.getValue();
        if (current.contains(note)) {
            current.remove(note);
        } else {
            current.add(note);
        }
        selectedNotes.setValue(new ArrayList<>(current));

        if(current.isEmpty()){
            selectionMode.setValue(false);
        }
    }

    public void clearSelection(){
        selectedNotes.setValue(new ArrayList<>());
        selectionMode.setValue(false);
    }

    public void deleteSelectedNotes(){
        List<Note> selected = selectedNotes.getValue();
        if(selected != null && !selected.isEmpty()){
            repository.deleteNotes(selected);
            clearSelection();
        }
    }

    public void startSelection() {
        selectedNotes.setValue(new ArrayList<>());
        selectionMode.setValue(true);
    }


    public void PinStatus(){
        List<Note> selected = selectedNotes.getValue();
        if (selected != null && !selected.isEmpty()) {
            for(Note note : selected){
                note.setPinned(!note.isPinned());
                repository.update(note);
            }
            clearSelection();
        }
    }
}