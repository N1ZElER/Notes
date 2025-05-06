package com.example.notes.Adapter;



import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Class.MainActivity;
import com.example.notes.Class.Noes_source;
import com.example.notes.Note;
import com.example.notes.NoteDao;
import com.example.notes.NoteDatabase;
import com.example.notes.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private List<Note> filterdNotes = new ArrayList<>();
    private Context context;
    private OnNoteClickListener onNoteClickListener;
    private androidx.appcompat.view.ActionMode actionMode;
    private OnNoteCountChangeListener noteCountChangeListener;
    private Set<Integer> selectedNotes = new HashSet<>(); // id выделенных заметок

    private static List<Note> recentlyDeletedNotes = new ArrayList<>();
    public NoteAdapter(List<Note> notes, Context context) {
        this.context = context;
        this.notes = new ArrayList<>(notes);
        this.filterdNotes = new ArrayList<>(notes);
    }


    public interface OnNoteCountChangeListener {
        void onNoteCountChanged(int count);
    }

    public void setOnNoteCountChangeListener(OnNoteCountChangeListener listener) {
        this.noteCountChangeListener = listener;
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_note_item, parent, false);
        return new NoteViewHolder(itemView);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {


        Note currentNote = filterdNotes.get(position);
        holder.titleTextView.setText(currentNote.getTitle());
        holder.contentTextView.setText(currentNote.getContent());
        holder.timeTextView.setText(currentNote.getFormattedCreateTime());


        holder.box.setOnCheckedChangeListener(null);
        holder.box.setChecked(selectedNotes.contains(currentNote.getId()));


        holder.box.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                selectedNotes.add(currentNote.getId());
            }else{
                selectedNotes.remove(currentNote.getId());
            }
        });


        holder.itemView.setOnClickListener(v -> {
            if(onNoteClickListener != null){
                onNoteClickListener.onNoteClick(currentNote);
            }
        });


        if (currentNote.isPinned()){
            holder.pinnedIcon.setVisibility(View.VISIBLE);
        }else{
            holder.pinnedIcon.setVisibility(View.GONE);
        }

        holder.pin.setOnClickListener(v -> {
            boolean newPinState = !currentNote.isPinned();
            currentNote.setPinned(newPinState);

            new Thread(() -> {
                NoteDatabase db = NoteDatabase.getInstance(context);
                NoteDao noteDao = db.noteDao();
                noteDao.updatePinStatus(currentNote.getId(), newPinState);


                List<Note> updatedNotes = noteDao.getAllNotess();
                updatedNotes.sort((n1, n2) -> Boolean.compare(n2.isPinned(), n1.isPinned()));

                ((Activity) context).runOnUiThread(() -> {
                    setNotes(updatedNotes);
                });
            }).start();
        });


        holder.titleTextView.setText(currentNote.getTitle());
        holder.contentTextView.setText(currentNote.getContent());
        holder.timeTextView.setText(currentNote.getFormattedCreateTime());


        holder.itemView.setOnClickListener(v -> {
            if (onNoteClickListener != null) {
                onNoteClickListener.onNoteClick(currentNote);
            }
        });


        holder.box.setOnClickListener(v -> {



            if (selectedNotes.contains(position)) {
                selectedNotes.remove(position);
                v.setSelected(false);
            } else {
                selectedNotes.add(position);
                v.setSelected(true);
            }


            if (selectedNotes.isEmpty()) {
                if (actionMode != null) {
                    actionMode.finish();


                }
            } else {
                if (actionMode == null) {
                    actionMode = ((AppCompatActivity) v.getContext()).startSupportActionMode(actionModeCallback);
                }
            }
        });

    }


    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.poupapmenu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();

            if (itemId == R.id.view_gallery) {
                PinnedSelectedNotes();
                actionMode.finish();
                return true;
            } else if (itemId == R.id.select_notes) {
                Toast toast = Toast.makeText(context, "В разработке", Toast.LENGTH_LONG);
                toast.show();
                return true;
            } else if (itemId == R.id.view_attachments) {
                Toast toast = Toast.makeText(context, "В разработке", Toast.LENGTH_LONG);
                toast.show();
                return true;
            } else if (itemId == R.id.nav_arhive) {
                Toast toast = Toast.makeText(context, "В разработке", Toast.LENGTH_LONG);
                toast.show();
                return true;
            } else if (itemId == R.id.action_delete) {
                deleteSelectedNotes();
                actionMode.finish();
                return true;
            } else if (itemId == R.id.copy) {
                Toast toast = Toast.makeText(context, "В разработке", Toast.LENGTH_LONG);
                toast.show();
                return true;
            } else if (itemId == R.id.send) {
                Toast toast = Toast.makeText(context, "В разработке", Toast.LENGTH_LONG);
                toast.show();
                return true;
            }else{
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            notifyDataSetChanged();
        }

    };


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






    public void setNotes(List<Note> notes) {
        if (notes != null) {
            this.notes.clear();
            this.notes.addAll(notes);
            notifyDataSetChanged();
            this.filterdNotes.clear();
            this.filterdNotes.addAll(notes);
        } else {
            this.notes = new ArrayList<>();
            this.filterdNotes = new ArrayList<>();
        }
        notifyDataSetChanged();
    }


    private void removeNoteAt(int position) {
        if (position < 0 || position >= filterdNotes.size()) return;

        Note noteToRemove = filterdNotes.get(position);
        filterdNotes.remove(position);
        notes.remove(noteToRemove);
        notifyItemRemoved(position);

        new Thread(() -> {
            NoteDatabase.getInstance(context).noteDao().delete(noteToRemove);
            ((Activity) context).runOnUiThread(() -> {
                if (noteCountChangeListener != null) {
                    noteCountChangeListener.onNoteCountChanged(filterdNotes.size());
                }
            });
        }).start();
    }

    public void showDeleteDialog(int position) {
        new AlertDialog.Builder(context, R.style.AlertDialogFastStyling)
                .setTitle(context.getString(R.string.nav_deistvie))
                .setMessage(context.getString(R.string.nav_deistvie_info))
                .setPositiveButton(context.getString(R.string.action_delete), (dialog, which) -> showDeleteDialogReplay(position))
                .setNegativeButton(context.getString(R.string.restore), (dialog, which) -> moveToMainActivity(position))
                .setCancelable(false)
                .show();
        notifyDataSetChanged();
    }


    public void showDeleteDialogReplay(int position) {
        new AlertDialog.Builder(context, R.style.AlertDialogFastStyling)
                .setTitle(context.getString(R.string.nav_deistvie_info2))
                .setMessage(context.getString(R.string.perm_delete))
                .setPositiveButton(context.getString(R.string.action_delete), (dialog, which) -> removeNoteAt(position))
                .setNegativeButton(context.getString(R.string.cancel), (dialog, which) ->
                        notifyDataSetChanged())
                .setCancelable(false)
                .show();
        notifyDataSetChanged();
    }


    public void moveToMainActivity(int position) {
        if (position < 0 || position >= filterdNotes.size()) return;

        Note noteToRestore = filterdNotes.get(position);
        noteToRestore.setDeleted(false); // Ставим статус не удалена заметка

        // Удаляем из списка удаленных
        filterdNotes.remove(position);
        notes.remove(noteToRestore);
        notifyItemRemoved(position);

        // Восстанавливаем в базе данных
        new Thread(() -> {
            NoteDatabase db = NoteDatabase.getInstance(context);
            db.noteDao().restoreNote(noteToRestore.getId());

            ((Activity) context).runOnUiThread(() -> {
                if (noteCountChangeListener != null) {
                    noteCountChangeListener.onNoteCountChanged(filterdNotes.size());
                }
            });
        }).start();
    }





    public void deleteSelectedNotes(){
        if(selectedNotes.isEmpty()) return;

        List<Note> noteToRemove = new ArrayList<>();
        for(Note note : notes){
            if (selectedNotes.contains(note.getId()))
                    noteToRemove.add(note);
                    note.setDeleted(true);
                    recentlyDeletedNotes.add(note);
            }

        notes.removeAll(noteToRemove);
        filterdNotes.removeAll(noteToRemove);
        notifyDataSetChanged();


        new Thread(() ->{
            NoteDatabase db = NoteDatabase.getInstance(context);
            NoteDao noteDao = db.noteDao();
            for(Note note : noteToRemove){
                noteDao.moveToTrash(note.getId());
            }
            ((Activity)context).runOnUiThread(()->{
                if(noteCountChangeListener != null){
                    noteCountChangeListener.onNoteCountChanged(filterdNotes.size());
                }
            });
        }).start();
        selectedNotes.clear();
    }


    public void PinnedSelectedNotes() {
        if (selectedNotes.isEmpty()) return;

        new Thread(() -> {
            NoteDatabase db = NoteDatabase.getInstance(context);
            NoteDao noteDao = db.noteDao();

            for (Integer noteId : selectedNotes) {
                Note note = noteDao.getNoteById(noteId);
                if (note != null) {
                    note.setPinned(true);
                    noteDao.updatePinStatus(noteId, true);
                }
            }

            List<Note> updatedNotes = noteDao.getAllNotess();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                updatedNotes.sort((n1, n2) -> Boolean.compare(n2.isPinned(), n1.isPinned()));
            }

            ((Activity) context).runOnUiThread(() -> {
                setNotes(updatedNotes);
                selectedNotes.clear();
                notifyDataSetChanged();
            });
        }).start();
    }


    public void moveItem(int from, int to) {
        if (from < 0 || to < 0 || from >= filterdNotes.size() || to >= filterdNotes.size()) return;

        Note movedNote = filterdNotes.remove(from);
        filterdNotes.add(to, movedNote);
        notifyItemMoved(from, to);
    }

    @Override
    public int getItemCount() {
        return filterdNotes.size();
    }




    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView contentTextView;
        private TextView timeTextView;
        private ImageButton pin;
        private ImageView pinnedIcon;
        private CheckBox box;



        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.noteTitleTextView);
            contentTextView = itemView.findViewById(R.id.noteContentTextView);
            timeTextView = itemView.findViewById(R.id.noteDateTextView);
            pinnedIcon = itemView.findViewById(R.id.pinnedIcon);
            box = itemView.findViewById(R.id.box);
            pin = itemView.findViewById(R.id.pin);
        }
    }


    public interface OnNoteClickListener{
        void onNoteClick(Note note);
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.onNoteClickListener = listener;
    }

    public int getNoteCounts() {
        return filterdNotes.size();
    }
}