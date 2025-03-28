package com.example.notes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "notes")
public class Note implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private long createTime;
    private boolean isBox;
    private String folderPath;

    @ColumnInfo(name = "is_pinned")
    private boolean isPinned;
    private boolean isArchived;
    private boolean isDeleted;


    // Конструктор
    public Note(String title, String content, long createTime, boolean isPinned, boolean isBox, String folderPath, boolean isArchived) {
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.isPinned = isPinned;
        this.isBox = isBox;
        this.folderPath = folderPath;
        this.isArchived = isArchived;
        this.isDeleted = false;
    }

    // Конструктор для Parcel
    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        createTime = in.readLong();
        isBox = in.readByte() != 0;
        folderPath = in.readString();
        isPinned = in.readByte() != 0;
        isArchived = in.readByte() != 0;
    }

    // Создание экземпляров через Parcelable.Creator
    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    // Метод для форматирования времени создания
    public String getFormattedCreateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return dateFormat.format(new Date(createTime));
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isBox() {
        return isBox;
    }

    public void setBox(boolean box) {
        this.isBox = box;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    // Реализация Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isDeleted(){
        return isDeleted;
    }
    public void setDeleted(boolean deleted){
        isDeleted = deleted;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(createTime);
        dest.writeByte((byte) (isBox ? 1 : 0));
        dest.writeString(folderPath != null ? folderPath : "");
        dest.writeByte((byte) (isPinned ? 1 : 0));
        dest.writeByte((byte) (isArchived ? 1 : 0));
    }
}
