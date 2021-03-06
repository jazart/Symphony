package com.jazart.symphony.featured;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jazart.symphony.R;
import com.jazart.symphony.di.App;

import entities.Song;


public class MusicAdapter extends ListAdapter<Song, MusicAdapter.MusicHolder> {
    private final LayoutInflater mInflater;

    public MusicAdapter(@NonNull DiffUtil.ItemCallback<Song> diffCallback, Context context) {
        super(diffCallback);
        mInflater = LayoutInflater.from(context);
        App app = (App) context.getApplicationContext();
        app.component.inject(this);
    }

    @NonNull
    @Override
    public MusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicHolder(mInflater.inflate(R.layout.list_item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MusicHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class MusicHolder extends RecyclerView.ViewHolder {
        final TextView songTV, artistTV;
        final ImageButton playButton;

        MusicHolder(View itemView) {
            super(itemView);
            songTV = itemView.findViewById(R.id.song_title);
            playButton = itemView.findViewById(R.id.play_button);
            artistTV = itemView.findViewById(R.id.artist);
        }

        void bind(final Song song) {
            songTV.setText(song.getName());
            String artist = song.getArtists().size() == 0 ? "Unknown" : song.getArtists().size() == 1 ? song.getArtists().get(0) : TextUtils.join(",", song.getArtists());
            artistTV.setText(artist);
            playButton.setOnClickListener(view -> {
            });
        }
    }
}
