package com.example.notes.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Class.MainActivity;
import com.example.notes.FolderDao;
import com.example.notes.GeterSrter.Folder;
import com.example.notes.Note;
import com.example.notes.NoteDao;
import com.example.notes.NoteDatabase;
import com.example.notes.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<Note> notes = new ArrayList<>();
    private final List<Note> filteredNotes = new ArrayList<>();
    private final Set<Integer> selectedFolder = new HashSet<>(); // ID выделенных папок
    private NoteAdapter.OnNoteCountChangeListener folderCountChangeListener;
    private NoteAdapter.OnNoteCountChangeListener noteCountChangeListener;
    private final List<Folder> folderList;
    private final Context context;


    // не удалять
//    public interface OnNoteCountChangeListener {
//        void onNoteCountChanged(int count);
//    }


    public FolderAdapter(Context context, List<Folder> folderList) {
        this.context = context;
        this.folderList = folderList;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        Folder folder = folderList.get(position);
        holder.folderName.setText(folder.getName());
        holder.itemView.setBackgroundColor(Color.WHITE);

        holder.folderBox.setOnCheckedChangeListener(null);
        holder.folderBox.setChecked(selectedFolder.contains(folder.getId()));

        holder.pinnedIcon.setVisibility(selectedFolder.contains(folder.getId()) ? View.VISIBLE: View.GONE);

        holder.folderBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedFolder.add(folder.getId());
            } else {
                selectedFolder.remove(folder.getId());
            }

        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("folder_path", folder.getPath());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                    .setTitle("Введите новое название папки");

            final EditText input = new EditText(v.getContext());
            input.setText(folder.getName());
            builder.setView(input);

            builder.setPositiveButton("Приминить", (dialog, which) -> {
               String newFolderName = input.getText().toString().trim();
               if(!newFolderName.isEmpty() && ! newFolderName.equals(folder.getName())){
                   rennameFolder(folder, newFolderName, position);
               }
            });

            builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

            builder.show();
            return true;
        });
    }

    private void rennameFolder(Folder folder, String newFolderName, int position) {
        File oldFolder = new File(folder.getPath());
        File newFolder = new File(oldFolder.getParent(), newFolderName);

        if(oldFolder.renameTo(newFolder)){
            executorService.execute(()-> {
                NoteDatabase db = NoteDatabase.getInstance(context);
                FolderDao folderDao = db.folderDao();
                folderDao.updateFolderName(folder.getId(), newFolderName, newFolder.getAbsolutePath());

                mainHandler.post(()->{
                   folderList.get(position).setName(newFolderName);
                   folderList.get(position).setPath(newFolder.getAbsolutePath());
                   notifyItemChanged(position);
                });
            });
        }else{
            Toast.makeText(context,"Ошибка переименования папки", Toast.LENGTH_SHORT).show();
        }
    }

//    public void deleteSelectedFolders() {
//        if (selectedFolder.isEmpty()) return;
//
//        List<Folder> foldersToRemove = new ArrayList<>();
//
//        for (Folder folder : folderList) {
//            if (selectedFolder.contains(folder.getId())) {
//                foldersToRemove.add(folder);
//            }
//        }
//        folderList.removeAll(foldersToRemove);
//        notifyDataSetChanged();
//
//        executorService.execute(() -> {
//            NoteDatabase db = NoteDatabase.getInstance(context);
//            FolderDao folderDao = db.folderDao();
//            for (Folder folder : foldersToRemove) {
//                folderDao.delete(folder);
//            }
//            mainHandler.post(() -> {
//                if (folderCountChangeListener != null) {
//                    folderCountChangeListener.onNoteCountChanged(filteredNotes.size());
//                }
//            });
//        });
//        selectedFolder.clear();
//    }



    public void showDeleteDialog(int position) {
        new AlertDialog.Builder(context, R.style.AlertDialogFastStyling2)
                .setTitle("Удалить папку")
                .setMessage("Вы уверены, что хотите удалить эту папку?")
                .setPositiveButton("Да", (dialog, which) -> {
                    removeFolderAt(position);
                })
                .setNegativeButton("Нет", (dialog, which) -> {
                    notifyItemChanged(position);
                })
                .show();
    }

    private void removeFolderAt(int position) {
        if (position < 0 || position >= folderList.size()) return;

        Folder folderToRemove = folderList.get(position);
        File folderFile = new File(folderToRemove.getPath());

        executorService.execute(() -> {
            if (folderFile.exists() && folderFile.isDirectory()) {
                File[] files = folderFile.listFiles();
                if (files != null) {
                    for (File file : files) {
                        recursivelyDelete(file);
                    }
                }
                boolean deleted = folderFile.delete();

                if (deleted) {
                    NoteDatabase db = NoteDatabase.getInstance(context);
                    FolderDao folderDao = db.folderDao();
                    folderDao.delete(folderToRemove);

                    mainHandler.post(() -> {
                        folderList.remove(position);
                        notifyItemRemoved(position);

                    });
                }
            }
        });
    }

    private void recursivelyDelete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    recursivelyDelete(f);
                }
            }
        }
        file.delete();
    }



//    public void togglePinStatuse(){
//        for(Folder folder : folderList){
//            if(selectedFolder.contains(folder.getId())){
//                boolean newPinedStatuse = !folder.isPinned();
//                folder.setPinned(newPinedStatuse);
//
//
//                executorService.execute(()->{
//                    NoteDatabase db = NoteDatabase.getInstance(context);
//                    FolderDao folderDao = db.folderDao();
//
//                    folderDao.updateFolderNames(folder.getId(), newPinedStatuse);
//
//                    mainHandler.post(()->{
//                       notifyDataSetChanged();
//                    });
//                });
//            }
//
//        }
//    }

//    private void removeNoteAt(int position) {
//        if (position < 0 || position >= filteredNotes.size()) return;
//
//        Note noteToRemove = filteredNotes.get(position);
//
//
//        filteredNotes.remove(position);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            notes.removeIf(note -> note.getId() == noteToRemove.getId());
//        }
//
//        notifyItemRemoved(position);
//
//        executorService.execute(() -> {
//            NoteDatabase.getInstance(context).noteDao().delete(noteToRemove);
//
//            mainHandler.post(() -> {
//                if (noteCountChangeListener != null) {
//                    noteCountChangeListener.onNoteCountChanged(filteredNotes.size());
//                }
//                notifyDataSetChanged();
//            });
//        });
//    }



    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderName;
        CheckBox folderBox;
        ImageView pinnedIcon;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
            folderBox = itemView.findViewById(R.id.folderBox);
            pinnedIcon = itemView.findViewById(R.id.pinnedIcon);
        }
    }
}
