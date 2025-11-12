package com.example.notes.Repostioriy;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.notes.Note;
import com.example.notes.NoteDao;
import com.example.notes.NoteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class NoteRepository{
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    private List<Note> notes = new ArrayList<>();


    public NoteRepository(Application application){
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();

    }


    public void insert(Note note){
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void update(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    public LiveData<Note> getNoteById(int id) {
        return noteDao.getNoteById(id);
    }

//    public LiveData<List<Note>> getAllNotesPinned(){
//        return noteDao.getAllNotesPinned();
//    }

    public void deleteNotes(List<Note> notes) {
        Executors.newSingleThreadExecutor().execute(() -> noteDao.deleteNotes(notes));
    }





    private static class InsertNoteAsyncTask extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;

        private InsertNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }



    private static class UpdateNoteAsyncTask extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }



    private static class DeleteNoteAsyncTask extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    public List<Note> getNotes(){
        return new ArrayList<>(notes);
    }

    public void save(Note note){
        new SaveNoteAsyncTask(noteDao).execute(note);
    }

    public LiveData<List<Note>> getPinnedNotes() {
        return noteDao.updatePinStatus();
    }

    private static class SaveNoteAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao;

        private SaveNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insertOrUpdate(notes[0]);
            return null;
        }
    }
}