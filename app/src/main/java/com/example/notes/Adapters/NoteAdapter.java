package com.example.notes.Adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Class.EditNotesAcitivty;
import com.example.notes.Note;
import com.example.notes.NoteDatabase;
import com.example.notes.R;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private boolean isCollapsed = false;
    private List<Note> notes;
    private List<Note> filteredNotes = new ArrayList<>();
    private OnNoteLongClickListener longClickListener;

    public NoteAdapter(List<Note> notes) {
        this.notes = notes;
        this.filteredNotes = new ArrayList<>(notes);
    }

    public void setOnNoteLongClickListener(OnNoteLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public NoteAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = filteredNotes.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());
        holder.noteDateTextView.setText(note.getFormattedCreateTime());


        if (isCollapsed) {
            holder.itemView.getLayoutParams().width = dpToPx(holder.itemView.getContext(), 155);
            holder.titleTextView.setVisibility(View.VISIBLE);
            holder.contentTextView.setVisibility(View.VISIBLE);
            holder.noteDateTextView.setVisibility(View.GONE);
            holder.noteDetailsTextView.setVisibility(View.GONE);
        } else {
            holder.itemView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.titleTextView.setVisibility(View.VISIBLE);
            holder.contentTextView.setVisibility(View.VISIBLE);
            holder.noteDateTextView.setVisibility(View.VISIBLE);
        }


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditNotesAcitivty.class);
            intent.putExtra(EditNotesAcitivty.EXTRA_NOTE_ID, note.getId());
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onNoteLongClick(holder.getAdapterPosition());
            }
            return true;
        });
    }

    public interface OnNoteLongClickListener {
        void onNoteLongClick(int position);
    }



    @Override
    public int getItemCount() {
        return filteredNotes != null ? filteredNotes.size() : 0;
    }

    public void setNotes(List<Note> notes) {
        if (notes != null) {
            this.notes = new ArrayList<>(notes);
            this.filteredNotes = new ArrayList<>(notes);
        } else {
            this.notes = new ArrayList<>();
            this.filteredNotes = new ArrayList<>();
        }
        notifyDataSetChanged();
    }


    public void filterNotes(String query) {
        filteredNotes.clear();
        if (query == null || query.isEmpty()) {
            filteredNotes.addAll(notes);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Note note : notes) {
                if (note.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        note.getContent().toLowerCase().contains(lowerCaseQuery)) {
                    filteredNotes.add(note);
                }
            }
        }
        notifyDataSetChanged();
    }

    public Note getNoteAt(int position) {
        return notes.get(position);
    }

    public void moveItem(int from, int to) {
        if (from < 0 || to < 0 || from >= filteredNotes.size() || to >= filteredNotes.size()) return;

        Note movedNote = filteredNotes.remove(from);
        filteredNotes.add(to, movedNote);
        notifyItemMoved(from, to);
    }

    public void setCollapsed(boolean collapsed) {
        this.isCollapsed = collapsed;
    }
    private int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView contentTextView;
        private TextView noteDetailsTextView;
//        private ImageButton pin;
        private ImageView pinnedIcon;
//        private CheckBox box;
        private TextView noteDateTextView;
        private LinearLayout noteRoot;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteRoot = itemView.findViewById(R.id.noteRoot);
            titleTextView = itemView.findViewById(R.id.noteTitleTextView);
            contentTextView = itemView.findViewById(R.id.noteContentTextView);
            noteDateTextView = itemView.findViewById(R.id.noteDateTextView);
            pinnedIcon = itemView.findViewById(R.id.pinnedIcon);
            noteDetailsTextView = itemView.findViewById(R.id.noteDetailsTextView);
//            box = itemView.findViewById(R.id.box);
//            pin = itemView.findViewById(R.id.pin);

        }
    }
}
