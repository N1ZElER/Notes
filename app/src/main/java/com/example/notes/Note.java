package com.example.notes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
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
    private String folderPath;
    private boolean isDeleted;


    // Конструктор
    public Note(String title, String content, long createTime, String folderPath, int id, boolean isDeleted) {
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.folderPath = folderPath;
        this.isDeleted = isDeleted;
        this.id = id;
    }

    @Ignore
    public Note(int id, String title, String content) {
        this.title = title;
        this.content = content;
        this.createTime = System.currentTimeMillis();
        this.folderPath = null;
        this.isDeleted = false;
    }


    @Ignore
    public Note(String title, String content,long createTime) {
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.folderPath = null;
    }

    // Designer for Parcel
    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        createTime = in.readLong();
        folderPath = in.readString();
    }

    // Creating instances via Parcelable.Creator
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




    // A method for formatting the creation time
    public String getFormattedCreateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return dateFormat.format(new Date(createTime));
    }

    // Getter and Setter
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
    public String getFolderPath() {
        return folderPath;
    }


    // Realez Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(createTime);
        dest.writeString(folderPath != null ? folderPath : "");
    }
}
