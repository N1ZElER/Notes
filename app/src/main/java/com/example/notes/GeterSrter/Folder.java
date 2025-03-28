package com.example.notes.GeterSrter;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "folders")
public class Folder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String path;
    private boolean pinned;


    public Folder(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public boolean isPinned(){
        return pinned;
    }

    public void setPinned(boolean pinned){
        this.pinned = pinned;
    }


    public int getId() { return id; }
    public String getName() { return name; }
    public String getPath() { return path; }


    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPath(String path) { this.path = path; }
}
