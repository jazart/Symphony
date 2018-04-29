package com.jazart.symphony;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jazart.symphony.model.Song;

import java.util.List;

import butterknife.ButterKnife;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicHolder> {
    private List<Song> mSongs;
    private LayoutInflater mInflater;

    public MusicAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public MusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.list_item_music, parent, false);
        return new MusicHolder(v);
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicHolder holder, int position) {
        holder.bind(mSongs.get(position));
    }

    @Override
    public int getItemCount() {
        return mSongs == null ? 0 : mSongs.size();
    }

    public void setSongs(List<Song> songs) {
        mSongs = songs;
    }

    public class MusicHolder extends RecyclerView.ViewHolder{
        TextView mSongTV;
        ImageButton mPlayButton;

        public MusicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
