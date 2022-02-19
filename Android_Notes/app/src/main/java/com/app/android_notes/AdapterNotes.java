package com.app.android_notes;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterNotes extends RecyclerView.Adapter<AdapterNotes.MyViewHolder> {

    private final List<NotesClass> notesClassList;
    private final com.app.android_notes.MainActivity mainactivity;

    AdapterNotes(List<NotesClass> noteList, com.app.android_notes.MainActivity ma) {
        this.notesClassList = noteList;
        mainactivity = ma;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_design, parent, false);

        itemView.setOnClickListener(mainactivity);
        itemView.setOnLongClickListener(mainactivity);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        NotesClass notesClass = notesClassList.get(position);

        if(notesClass.getStrtitle().length()>80)
            holder.txtTitle.setText(notesClass.getStrtitle().substring(0,80)+"...");
        else
            holder.txtTitle.setText(notesClass.getStrtitle());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd, hh:mm a");
        holder.txtDate.setText(simpleDateFormat.format(new Date(notesClass.getSavetimelast())));
        if(notesClass.getStrcontent().length()>80)
            holder.txtContent.setText(notesClass.getStrcontent().substring(0,80)+"...");
        else
            holder.txtContent.setText(notesClass.getStrcontent());

    }

    @Override
    public int getItemCount() {
        return notesClassList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle,txtDate,txtContent;

        MyViewHolder(View view) {
            super(view);
            txtTitle = view.findViewById(R.id.title);
            txtDate = view.findViewById(R.id.date);
            txtContent = view.findViewById(R.id.content);
        }

    }
}