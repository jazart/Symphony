package com.jazart.symphony;

/**
 * Created by kendrickgholston on 7/9/18.
 */
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.Formatter;
import java.util.Locale;

import static com.jazart.symphony.featured.MusicAdapter.exoPlayer;
import static com.jazart.symphony.MainActivity.songPlaying;

public class MusicControls {
    public TextView txtCurrentTime, txtEndTime;
    public static SeekBar playerSeek;
    private Handler handler;
    public MainActivity mA;
    public MusicControls(MainActivity musicActive){
        mA = musicActive;
    }
    public void initTxtTime() {
        txtCurrentTime = mA.findViewById(R.id.time_current);
        txtEndTime = mA.findViewById(R.id.player_end_time);
    }

    public String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public void setProgress() {
        playerSeek.setProgress(0);
        playerSeek.setMax((int) exoPlayer.getDuration()/1000);
        txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
        txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));

        if(handler == null)handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && songPlaying) {
                    playerSeek.setMax((int) exoPlayer.getDuration()/1000);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    playerSeek.setProgress(mCurrentPosition);
                    txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
                    txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }


    public void initSeekBar() {
        playerSeek = mA.findViewById(R.id.mediacontroller_progress);
        playerSeek.requestFocus();

        playerSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {

                    return;
                }

                exoPlayer.seekTo(progress*1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playerSeek.setMax(0);
        playerSeek.setMax((int) exoPlayer.getDuration()/1000);

    }
}
