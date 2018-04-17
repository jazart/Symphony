package com.jazart.symphony;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.net.Uri;

import com.github.clans.fab.FloatingActionButton;

public class FeaturedMusicFragment extends Fragment {
    private MusicAdapter mMusicAdapter;
    private FloatingActionButton upFAB;
    private Uri uriSound;


    public FeaturedMusicFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.feature_music_fragment, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.featured_songs);
        recyclerView.setAdapter(mMusicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        upFAB =  v.findViewById(R.id.fab_upload);
        upFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musicIntent = new Intent();
                musicIntent.setAction(Intent.ACTION_GET_CONTENT);
                musicIntent.setType("audio/mp3");
                startActivityForResult(Intent.createChooser(
                        musicIntent, "Open Audio (mp3) file"),1);


            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK){
            //Log.d("DEBUG","RESULT OK");
            if (requestCode == 1){
                //Log.d("DEBUG","REQUEST OK");
                uriSound = data.getData();
                //Log.d("DEBUG",uriSound.toString());
                MediaMetadataRetriever musicInfoRetr = new MediaMetadataRetriever();
                musicInfoRetr.setDataSource(uriSound.toString());

            }
        }
    }




    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
