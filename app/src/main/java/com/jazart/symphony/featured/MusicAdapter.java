package com.jazart.symphony.featured;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.jazart.symphony.R;
import com.jazart.symphony.di.App;
import com.jazart.symphony.model.Song;
import com.jazart.symphony.playback.PlayerListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

import static com.jazart.symphony.MainActivity.mMediaController;
import static com.jazart.symphony.MainActivity.playerCreated;
import static com.jazart.symphony.MainActivity.songPlaying;



public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicHolder> {
    private List<Song> mSongs;
    private final LayoutInflater mInflater;

    @Inject
    SimpleExoPlayer exoPlayer;
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

    public void setSongs(List<Song> songs) {
        mSongs = songs;
    }

    class MusicHolder extends RecyclerView.ViewHolder{
        final TextView mSongTV, mArtistTV;
        final ImageButton mPlayButton;


        MusicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mSongTV = itemView.findViewById(R.id.song_title);
            mPlayButton = itemView.findViewById(R.id.play_button);
            mArtistTV = itemView.findViewById(R.id.artist);
        }

        void bind(final Song song) {
            mSongTV.setText(song.getName());
            String artist = song.getArtists().size() == 0 ? "Unknown" : song.getArtists().size() == 1 ? song.getArtists().get(0) : TextUtils.join(",", song.getArtists());
            mArtistTV.setText(artist);
            mPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prepareExoPlayerFromURL(Uri.parse(song.getURI()));
                    exoPlayer.setPlayWhenReady(true);

                }
            });
        }

        private void prepareExoPlayerFromURL(Uri uri) {
            if (exoPlayer != null) {
                exoPlayer.stop();
            }

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(itemView.getContext(),
                    "");
            MediaSource audioSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .setExtractorsFactory(new DefaultExtractorsFactory())
                    .createMediaSource(uri);
            exoPlayer.addListener(eventListener);
            exoPlayer.prepare(audioSource);
            mMediaController.setVisibility(View.VISIBLE);
            playerCreated.setPlayerBool(true);
            songPlaying = true;
        }

    }
}
