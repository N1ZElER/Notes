package com.example.notes.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.notes.Note
import com.example.notes.Repostioriy.NoteRepository

class EditViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NoteRepository(application)
    private var note: LiveData<Note>? = null

    fun loadNoteById(noteId: Int) {
        note = repository.getNoteById(noteId)
    }

    fun getNote(): LiveData<Note>? {
        return note
    }

    fun saveNote(note: Note) {
        repository.save(note)
    }
}
