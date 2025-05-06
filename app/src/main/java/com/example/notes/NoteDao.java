package com.example.notes;

import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
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


//    @Query("SELECT * FROM notes")
//    List<Note> getAllNotes();

    @Query("SELECT * FROM notes WHERE folderPath = :folderPath AND isDeleted = 0")
    List<Note> getNotesByFolder(String folderPath);


    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    Note getNoteById(int id);


    @Query("UPDATE notes SET is_pinned = :isPinned WHERE id = :id")
    void updatePinStatus(int id, boolean isPinned);


    @Query("SELECT * FROM notes WHERE isDeleted = 1")
    LiveData<List<Note>> getDeletedNotes();

    @Query("UPDATE notes SET isDeleted = 1 WHERE id = :noteId")
    void moveToTrash(int noteId);

    @Query("UPDATE notes SET isDeleted = 0 WHERE id = :noteId")
    void restoreNote(int noteId);

    @Query("SELECT * FROM notes WHERE isDeleted = 0")
    List<Note> getAllNotess();




//    @Query("SELECT COUNT(*) FROM notes WHERE folderPath = :folderPath")
//    LiveData<Integer> getNoteCount(String folderPath);
//
//    @Query("SELECT COUNT(*) FROM notes")
//    int getNoteCountSync();

//    @Query("UPDATE notes SET isArchived = 1 WHERE id = :noteId")
//    void archiveNote(int noteId);
//
//    @Query("UPDATE notes SET isArchived = 0 WHERE id = :noteId")
//    void unarchiveNote(int noteId);
//
//    @Query("SELECT * FROM notes WHERE isArchived = 1")
//    List<Note> getArchivedNotes();
//
//    @Query("SELECT * FROM notes WHERE id = :id")
//    LiveData<Note> getNoteByIdd(int id);

//    @Query("SELECT * FROM notes WHERE isDeleted = 0")
//    List<Note> getAllActiveNotes();

//      @Query("SELECT COUNT(*) FROM notes")
//    LiveData<Integer> getNoteCount();

//    @Query("DELETE FROM notes WHERE isArchived = 1 AND createTime <= :timeLimit")
//    void deleteOldArchivedNotes(long timeLimit);
}
