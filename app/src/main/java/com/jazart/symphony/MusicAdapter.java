package com.jazart.symphony;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
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
import com.jazart.symphony.model.Song;

import java.util.List;

import butterknife.ButterKnife;

import static com.jazart.symphony.MainActivity.exoPlayerC;



public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicHolder> {
    private List<Song> mSongs;
    private LayoutInflater mInflater;
    public SimpleExoPlayer exoPlayer;
    public Player.EventListener eventListener = new PlayerListener();
    public static SeekBar seekPlayerProgress;
    public static Handler handler;
    public static ImageButton btnPlay;
    public static TextView txtCurrentTime, txtEndTime;
    public static boolean isPlaying = false;


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

        public void bind(final Song song) {
            mSongTV.setText(song.getName());
            mPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d("DEBUG",song.getURI() + " " +itemView.getContext().toString());
                    prepareExoPlayerFromURL(Uri.parse(song.getURI()));
                    exoPlayer.setPlayWhenReady(true);
//                    Intent musicStart = new Intent(itemView.getContext(),MusicService.class);
//                    musicStart.putExtra("URL", song.getURI());
//                    playerCL.setVisibility(View.VISIBLE);
//                    playerL.setVisibility(View.VISIBLE);
                    //initializePlayer(song.getURI(),itemView.getContext());
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
//            initMediaControls();
        }
//        public  void initMediaControls() {
//            initPlayButton();
//            initSeekBar();
//            initTxtTime();
//        }

//        public  void initPlayButton() {
//            btnPlay = (ImageButton) itemView.findViewById(R.id.btnPlay);
//            btnPlay.requestFocus();
//            btnPlay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    setPlayPause(!isPlaying);
//                }
//            });
//        }

//        public  void setPlayPause(boolean play){
//            isPlaying = play;
//            exoPlayer.setPlayWhenReady(play);
//            if(!isPlaying){
//                btnPlay.setImageResource(android.R.drawable.ic_media_play);
//            }else{
//                setProgress();
//                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
//            }
//        }
//
//        public  void initTxtTime() {
//            txtCurrentTime = (TextView) itemView.findViewById(R.id.time_current);
//            txtEndTime = (TextView) itemView.findViewById(R.id.player_end_time);
//        }
//
//        public  String stringForTime(int timeMs) {
//            StringBuilder mFormatBuilder;
//            Formatter mFormatter;
//            mFormatBuilder = new StringBuilder();
//            mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
//            int totalSeconds =  timeMs / 1000;
//
//            int seconds = totalSeconds % 60;
//            int minutes = (totalSeconds / 60) % 60;
//            int hours   = totalSeconds / 3600;
//
//            mFormatBuilder.setLength(0);
//            if (hours > 0) {
//                return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
//            } else {
//                return mFormatter.format("%02d:%02d", minutes, seconds).toString();
//            }
//        }
//
//        public  void setProgress() {
//            seekPlayerProgress.setProgress(0);
//            seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
//            txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
//            txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));
//
//            if(handler == null)handler = new Handler();
//            //Make sure you update Seekbar on UI thread
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (exoPlayer != null && isPlaying) {
//                        seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
//                        int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
//                        seekPlayerProgress.setProgress(mCurrentPosition);
//                        txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
//                        txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));
//
//                        handler.postDelayed(this, 1000);
//                    }
//                }
//            });
//        }
//
//        public void initSeekBar() {
//            seekPlayerProgress = (SeekBar) itemView.findViewById(R.id.mediacontroller_progress);
//            seekPlayerProgress.requestFocus();
//
//            seekPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    if (!fromUser) {
//                        // We're not interested in programmatically generated changes to
//                        // the progress bar's position.
//                        return;
//                    }
//
//                    exoPlayer.seekTo(progress*1000);
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//
//                }
//            });
//
//            seekPlayerProgress.setMax(0);
//            seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
//
//        }
    }
}
