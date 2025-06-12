package com.example.notes;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notes.Migration.Migration_1_2;


@Database(entities = {Note.class}, version = 4)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance;

    public abstract NoteDao noteDao();

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
