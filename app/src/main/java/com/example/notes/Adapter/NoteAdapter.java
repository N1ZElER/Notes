package com.example.notes.Adapter;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private List<Note> filterdNotes = new ArrayList<>();
    private Context context;
    private OnNoteClickListener onNoteClickListener;
    private OnNoteCountChangeListener noteCountChangeListener;
    private Set<Integer> selectedNotes = new HashSet<>(); // id выделенных заметок

    private static List<Note> recentlyDeletedNotes = new ArrayList<>();
    public NoteAdapter(List<Note> notes, Context context) {
        this.context = context;
    }

    public NoteAdapter(Noes_source context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
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


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v);
                return false;
            }
        });
    }


    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);

        popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.view_gallery) {
                    deleteSelectedNotes();
                    return true;
                } else if (itemId == R.id.select_notes) {
                    PinnedSelectedNotes();
                    return true;
                } else if (itemId == R.id.view_attachments) {

                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
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
                .setTitle("Выберите действие")
                .setMessage("Вы хотите удалить/восстановить заметку?")
                .setPositiveButton("Удалить", (dialog, which) -> showDeleteDialogReplay(position))
                .setNegativeButton("Восстановить", (dialog, which) -> moveToMainActivity(position))
                .setCancelable(false)
                .show();
        notifyDataSetChanged();
    }


    public void showDeleteDialogReplay(int position) {
        new AlertDialog.Builder(context, R.style.AlertDialogFastStyling)
                .setTitle("Вы действительно хотите удалить заметку?")
                .setMessage("Заметка будет удалена без возвратно!")
                .setPositiveButton("Удалить", (dialog, which) -> removeNoteAt(position))
                .setNegativeButton("Отмена", (dialog, which) ->
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
            if(selectedNotes.contains(note.getId()));
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


    public void PinnedSelectedNotes(){
        if(selectedNotes.isEmpty()) return;

        new Thread(()->{
            NoteDatabase db = NoteDatabase.getInstance(context);
            NoteDao noteDao = db.noteDao();

            for(Note note : notes){
                if(selectedNotes.contains(note.getId())){
                    note.setPinned(true);
                    noteDao.updatePinStatus(note.getId(),true);
                }
            }
            List<Note> updetedNotes = noteDao.getAllNotes();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                updetedNotes.sort((n1, n2)-> Boolean.compare(n2.isPinned(),n1.isPinned()));
            }

            ((Activity)context).runOnUiThread(()->{
                setNotes(updetedNotes);
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