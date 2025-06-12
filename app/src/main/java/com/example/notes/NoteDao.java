package com.example.notes;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    long insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);


    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    LiveData<Note> getNoteById(int id);


    @Query("SELECT * FROM notes WHERE isDeleted = 0")
    LiveData<List<Note>> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Note note);

    @Delete
    void deleteNotes(List<Note> notes);

    @Query("SELECT * FROM notes WHERE isPinned = 1")
    LiveData<List<Note>> updatePinStatus();

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY isPinned DESC, createTime DESC")
    LiveData<List<Note>> getAllNotesPinned();

}

