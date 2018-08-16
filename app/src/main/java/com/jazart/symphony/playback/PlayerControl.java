package com.jazart.symphony.playback;


import android.widget.MediaController.MediaPlayerControl;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * An implementation of {@link MediaPlayerControl} for controlling an {@link ExoPlayer} instance.
 * <p>
 */
class PlayerControl implements MediaPlayerControl {

    private final SimpleExoPlayer exoPlayer;

    public PlayerControl(SimpleExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    /**
     * This is an unsupported operation.
     * <p>
     * Application of audio effects is dependent on the audio renderer used. When using
     *
     *
     * @throws UnsupportedOperationException Always thrown.
     */
    @Override
    public int getAudioSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferPercentage() {
        return exoPlayer.getBufferedPercentage();
    }

    @Override
    public int getCurrentPosition() {
        return (int) exoPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return (int) exoPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return exoPlayer.getPlayWhenReady();
    }

    @Override
    public void start() {
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void seekTo(int timeMillis) {
        // MediaController arrow keys generate unbounded values.
        exoPlayer.seekTo(Math.min(Math.max(0, timeMillis), getDuration()));
    }

}
