package com.example.notes.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notes.Note
import com.example.notes.Repostioriy.NoteRepository

class EditNoteViewModel2(application: Application) : AndroidViewModel(application) {

    private val repository = NoteRepository(application)

    private val _note = MutableLiveData<Note>()
    val note: LiveData<Note> get() = _note

    private val selectedNotes = MutableLiveData<MutableList<Note>>(mutableListOf())
    private val selectionMode = MutableLiveData(false)

    fun insert(note: Note) {
        repository.insert(note)
    }

    fun update(note: Note) {
        repository.update(note)
    }

    fun delete(note: Note) {
        repository.delete(note)
    }

    fun deleteSelectedNotes() {
        val selected = selectedNotes.value ?: return
        if (selected.isEmpty()) return

        repository.deleteNotes(selected)
        clearSelection()
    }

    fun loadNoteById(id: Int) {
        repository.getNoteById(id).observeForever { note ->
            note?.let {
                _note.value = it
            }
        }
    }

    fun saveNote(noteToSave: Note) {
        _note.value = noteToSave
        repository.update(noteToSave)
    }

    fun toggleSelection(note: Note) {
        val current = selectedNotes.value ?: mutableListOf()
        if (note in current) {
            current.remove(note)
        } else {
            current.add(note)
        }
        selectedNotes.value = ArrayList(current)
        if (current.isEmpty()) selectionMode.value = false
    }

    fun clearSelection() {
        selectedNotes.value = ArrayList()
        selectionMode.value = false
    }

    fun startSelection() {
        selectedNotes.value = ArrayList()
        selectionMode.value = true
    }

    fun PinStatus() {
        val selected = selectedNotes.value ?: return
        if (selected.isEmpty()) return

        selected.forEach { note ->
            note.setPinned(!note.isPinned())
            repository.update(note)
        }
        clearSelection()
    }

    fun getSelectedNotes(): LiveData<MutableList<Note>> = selectedNotes
    fun isSelectionMode(): LiveData<Boolean> = selectionMode
}
