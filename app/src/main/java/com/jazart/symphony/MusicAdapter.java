package com.jazart.symphony;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jazart.symphony.model.Song;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicHolder> {
    private List<Song> mSongs;
    @NonNull
    @Override
    public MusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicHolder holder, int position) {
        holder.bind(mSongs.get(position));
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public class MusicHolder extends RecyclerView.ViewHolder{
        TextView mSongTV;
        ImageButton mPlayButton;

        public MusicHolder(View itemView) {
            super(itemView);
            mSongTV = itemView.findViewById(R.id.songtitle);
            mPlayButton = itemView.findViewById(R.id.playbutton);
        }

        public void bind(Song song) {
            mSongTV.setText(song.getName());
            mPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: 4/9/2018 get song url and steam music
                }
            });
        }
    }
}
