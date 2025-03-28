package com.example.notes;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notes.FolderDao;
import com.example.notes.GeterSrter.Folder;
import com.example.notes.Migration.Migration_1_2;
import com.example.notes.Note;
import com.example.notes.NoteDao;
import com.example.notes.RecentlyDeletedNote;

@Database(entities = {Note.class, Folder.class, RecentlyDeletedNote.class}, version = 6)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance;

    public abstract NoteDao noteDao();
    public abstract FolderDao folderDao();

    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addMigrations(new Migration_1_2())
                    .build();
        }
        return instance;
    }
}
