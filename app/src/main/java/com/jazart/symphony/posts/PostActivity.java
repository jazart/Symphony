package com.jazart.symphony.posts;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jazart.symphony.R;

/*
Container activity for the post fragment. Implemets the callback interface
and then send the information back to the main activity so the post can
be added to the database.
 */
public class PostActivity extends AppCompatActivity {
    public static final String EXTRA_POST = "com.jazart.symphony.extraPost";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_post);
        NewPostFragment fragment = new NewPostFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.post_fragment_container, fragment)
                .commit();
    }
}
