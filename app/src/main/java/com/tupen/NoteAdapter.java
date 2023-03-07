package com.tupen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private final List<Note> notes;
    Context context;

    public NoteAdapter(List<Note> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.body.setText(note.getBody());


        holder.image.setImageURI(Uri.parse(note.getImagePath()));

        holder.date.setText(note.getDate());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Details.class);
                intent.putExtra("id", note.getId());
                intent.putExtra("title", note.getTitle());
                intent.putExtra("body", note.getBody());
                intent.putExtra("image_1", note.getImagePath());
                intent.putExtra("image_2", note.getImagePath2());
                intent.putExtra("audio", note.getAudioPath());
                intent.putExtra("date", note.getDate());


                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView body;
        ImageView image;
        TextView date;

        CardView card;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title);
            body = itemView.findViewById(R.id.note_body);
            image = itemView.findViewById(R.id.note_image);
            date = itemView.findViewById(R.id.note_date);
            card = itemView.findViewById(R.id.note_card);
        }
    }
}

