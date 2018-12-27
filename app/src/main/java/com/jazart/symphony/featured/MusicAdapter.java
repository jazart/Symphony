package com.jazart.symphony.featured;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.Player;
import com.jazart.symphony.R;
import com.jazart.symphony.di.App;
import com.jazart.symphony.model.Song;
import com.jazart.symphony.playback.PlayerListener;

import java.util.List;



public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicHolder> {
    private List<Song> mSongs;
    private final LayoutInflater mInflater;

    private final Player.EventListener eventListener = new PlayerListener();

    MusicAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        App app = (App) context.getApplicationContext();
        app.component.inject(this);
    }

    @NonNull
    @Override
    public MusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.list_item_music, parent, false);
        return new MusicHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MusicHolder holder, int position) {
        holder.bind(mSongs.get(position));
    }

    @Override
    public int getItemCount() {
        return mSongs == null ? 0 : mSongs.size();
    }

    void setSongs(List<Song> songs) {
        mSongs = songs;
        notifyDataSetChanged();
    }

    static class MusicHolder extends RecyclerView.ViewHolder{
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
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   // prepareExoPlayerFromURL(Uri.parse(song.getURI()));
//                    exoPlayer.setPlayWhenReady(true);


                }
            });
        }

//        private void prepareExoPlayerFromURL(Uri uri) {
//            if (exoPlayer != null) {
//                exoPlayer.stop();
//            }
//
//            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(itemView.getContext(),
//                    "exo");
//            MediaSource audioSource = new ExtractorMediaSource.Factory(dataSourceFactory)
//                    .setExtractorsFactory(new DefaultExtractorsFactory())
//                    .createMediaSource(uri);
//            exoPlayer.addListener(eventListener);
//            exoPlayer.prepare(audioSource);
//            playerCreated.setPlayerBool(true);
//            songPlaying = true;
//        }

    }
}
