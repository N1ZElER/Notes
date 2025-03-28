package com.example.notes;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.notes.GeterSrter.Folder;

import java.util.List;

@Dao
public interface FolderDao {
    @Insert
    void insert(Folder folder);

    @Delete
    void delete(Folder folder);

    @Query("SELECT * FROM folders")
    List<Folder> getAllFolders();

    @Query("SELECT * FROM folders")
    LiveData<List<Folder>> getAllFoldersLive();

    @Query("Update folders SET name = :newName, path = :newPath WHERE id = :id")
    void updateFolderName(int id, String newName, String newPath);

    @Query("UPDATE folders SET pinned = :pinned WHERE id = :folderId")
    void updateFolderNames(int folderId, boolean pinned );
}
