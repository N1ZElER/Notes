package com.example.notes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recently_deleted_notes")
public class RecentlyDeletedNote {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String content;
    private long deletedAt; // Время, когда заметка была удалена

    // Конструкторы, геттеры и сеттеры
    public RecentlyDeletedNote(String title, String content, long deletedAt) {
        this.title = title;
        this.content = content;
        this.deletedAt = deletedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(long deletedAt) {
        this.deletedAt = deletedAt;
    }
}
