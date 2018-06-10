package com.jazart.symphony.posts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.jazart.symphony.R;
import com.jazart.symphony.featured.SongViewModel;
import com.jazart.symphony.model.Song;

import java.util.ArrayList;
import java.util.List;


public class UploadDialog extends DialogFragment  implements DialogInterface.OnClickListener {
    public static final String TAG = "UploadDialog";
    public static final String ARG_URI = "1";

    private SongViewModel mSongViewModel;
    private TextInputLayout mArtists;
    private TextInputLayout mSongTitle;
    private Song mSong = new Song();


    public static UploadDialog newInstance(Uri uri){
        Bundle mBundle = new Bundle();
        mBundle.putString(ARG_URI,uri.toString());
        UploadDialog upDia = new UploadDialog();
        upDia.setArguments(mBundle);
        return upDia;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongViewModel = ViewModelProviders.of(this).get(SongViewModel.class);
        if(getArguments() != null){
            mSong.setURI((getArguments().getString(ARG_URI)));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_upload_dialog, null);

        mArtists = view.findViewById(R.id.enter_artists);
        mSongTitle = view.findViewById(R.id.enter_songtitle);

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Upload")
                .setPositiveButton(android.R.string.ok, this)
                .create();

    }

    @Override
    public void show(FragmentManager manager, String tag) {
            manager.beginTransaction()
                    .add(this, tag)
                    .commitAllowingStateLoss();
    }


    /**
     * Implementing on click for dialog buttons. Validates email and password
     * then sends result back to to the fragment to sign up via firebase
     */
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Log.d("DEBUG",mSongTitle.getEditText().getText().toString());
        Log.d("DEBUG", mSong.getURI().toString());
        List<String> result = new ArrayList<>();

        if(mArtists.getEditText().getText().toString().contains(",")){
            String[] artists = mArtists.getEditText().getText().toString().split(",");
            for(int j = 0; j < artists.length;i++){
                result.add(artists[j]);
            }

        }
        else{
            result.add(mArtists.getEditText().getText().toString());
        }
        mSong.setName(mSongTitle.getEditText().getText().toString());
        mSong.setArtists(result);
        mSongViewModel.addSongToStorage(mSong);
    }
}
