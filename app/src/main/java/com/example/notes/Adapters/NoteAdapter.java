package com.example.notes.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Class.EditNotesAcitivty;
import com.example.notes.Note;
import com.example.notes.R;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private List<Note> filterdNotes = new ArrayList<>();

    public NoteAdapter(List<Note> notes) {
        this.notes = notes;
        this.filterdNotes = new ArrayList<>(notes);
    }

    @NonNull
    @Override
    public NoteAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = filterdNotes.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());
        holder.noteDateTextView.setText(note.getFormattedCreateTime());


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditNotesAcitivty.class);
            intent.putExtra(EditNotesAcitivty.EXTRA_NOTE_ID, note.getId());
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return filterdNotes != null ? filterdNotes.size() : 0;
    }

    public void setNotes(List<Note> notes) {
        if (notes != null) {
            this.notes = new ArrayList<>(notes);
            this.filterdNotes = new ArrayList<>(notes);
        } else {
            this.notes = new ArrayList<>();
            this.filterdNotes = new ArrayList<>();
        }
        notifyDataSetChanged();
    }


    public void filterNotes(String query) {
        filterdNotes.clear();
        if (query == null || query.isEmpty()) {
            filterdNotes.addAll(notes);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Note note : notes) {
                if (note.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        note.getContent().toLowerCase().contains(lowerCaseQuery)) {
                    filterdNotes.add(note);
                }
            }
        }
        notifyDataSetChanged();
    }

    public Note getNoteAt(int position) {
        return notes.get(position);
    }

    public void moveItem(int from, int to) {
        if (from < 0 || to < 0 || from >= filterdNotes.size() || to >= filterdNotes.size()) return;

        Note movedNote = filterdNotes.remove(from);
        filterdNotes.add(to, movedNote);
        notifyItemMoved(from, to);
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView contentTextView;
        private ImageButton pin;
        private ImageView pinnedIcon;
        private CheckBox box;
        private TextView noteDateTextView;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.noteTitleTextView);
            contentTextView = itemView.findViewById(R.id.noteContentTextView);
            noteDateTextView = itemView.findViewById(R.id.noteDateTextView);
            pinnedIcon = itemView.findViewById(R.id.pinnedIcon);
            box = itemView.findViewById(R.id.box);
            pin = itemView.findViewById(R.id.pin);
        }
    }
}