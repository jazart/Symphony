package com.jazart.symphony.featured;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.jazart.symphony.PlayerListener;
import com.jazart.symphony.R;
import com.jazart.symphony.model.Song;

import java.util.List;

import butterknife.ButterKnife;

import static com.jazart.symphony.MainActivity.exoPlayerC;
import static com.jazart.symphony.MainActivity.playerCreated;
import static com.jazart.symphony.MainActivity.playerView;
import static com.jazart.symphony.MainActivity.songPlaying;



public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicHolder> {
    private List<Song> mSongs;
    private LayoutInflater mInflater;
    public static SimpleExoPlayer exoPlayer;

    public Player.EventListener eventListener = new PlayerListener(){
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playWhenReady && playbackState == Player.STATE_READY) {

            } else if (playWhenReady) {

            } else {

            }
        }
    };



    public MusicAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
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

    public class MusicHolder extends RecyclerView.ViewHolder{
        TextView mSongTV;
        ImageButton mPlayButton;

        public MusicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mSongTV = itemView.findViewById(R.id.songtitle);
            mPlayButton = itemView.findViewById(R.id.playbutton);
        }

        public void bind(final Song song) {
            mSongTV.setText(song.getName());
            mPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prepareExoPlayerFromURL(Uri.parse(song.getURI()));
                    exoPlayer.setPlayWhenReady(true);

                }
            });
        }
        private  void prepareExoPlayerFromURL(Uri uri){

            TrackSelector trackSelector = new DefaultTrackSelector();

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(itemView.getContext());


            if(exoPlayerC != null){
                exoPlayer.stop();
            }
            exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, new DefaultLoadControl());
            exoPlayerC = exoPlayer;

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(itemView.getContext(),
                    "exoplayer2example");
            MediaSource audioSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .setExtractorsFactory(new DefaultExtractorsFactory())
                    .createMediaSource(uri);
            exoPlayer.addListener(eventListener);
            exoPlayer.prepare(audioSource);
            playerView.setVisibility(View.VISIBLE);
            playerCreated.setPlayerBool(true);
            songPlaying = true;

        }

    }
}
