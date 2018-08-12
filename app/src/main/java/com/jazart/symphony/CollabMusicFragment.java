package com.jazart.symphony;

/**
 * Created by kendrickgholston on 4/15/18.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jazart.symphony.featured.MusicAdapter;

public class CollabMusicFragment extends Fragment {
    private MusicAdapter mMusicAdapter;


    public CollabMusicFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.collab_music_fragment, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(mMusicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

}
