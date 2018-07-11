package com.jazart.symphony;

/**
 * Created by kendrickgholston on 5/8/18.
 */

public class PlayerBoolean {
    private boolean playerBool = false;
    private ChangeListener listener;

    public boolean isPlayerBool() {
        return playerBool;
    }

    public void setPlayerBool(boolean playerBool) {
        this.playerBool = playerBool;
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}
