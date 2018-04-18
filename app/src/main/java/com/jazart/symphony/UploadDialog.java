package com.jazart.symphony;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import com.google.firebase.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;


public class UploadDialog extends DialogFragment  implements DialogInterface.OnClickListener {
    public static final String TAG = "SignUpDialog";
    public static final String EXTRA_ARTISTS = "com.jazart.symphony.extra_artists";
    public static final String EXTRA_SONG = "com.jazart.symphony.extra_song";
    public static final String EXTRA_MP3 = "com.jazart.symphony.extra_mp3";
    private static final int MIN_PASS_LENGTH = 8;
    public static final String ARG_URI = "1";
    private DatabaseReference mDatabase;

    private View mView;
    private Uri selectedMP3;
    private TextInputLayout mArtists;
    private TextInputLayout mNameLayout;
    private TextInputLayout mSongTitle;
    private SongPost mSongPost;
    private Song msong;






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
        if(savedInstanceState != null){
            msong.setURI((URI) savedInstanceState.get(ARG_URI));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mSongPost = (SongPost)context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_upload_dialog, null);

        mArtists = mView.findViewById(R.id.enter_artists);
        mSongTitle = mView.findViewById(R.id.enter_songtitle);





        return new AlertDialog.Builder(getContext())
                .setView(mView)
                .setTitle("Upload")
                .setPositiveButton(android.R.string.ok, this)
                .create();

    }


    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        try {
            manager.beginTransaction()
                    .add(this, tag)
                    .commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /*
            Implementing on click for dialog buttons. Validates email and password
            then sends result back to to the fragment to sign up via firebase
             */
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

       // sendResult(Activity.RESULT_OK,mArtists.toString(),mSongTitle.toString());
        //msong.setArtists(mArtists.toString());
        msong.setName(mSongTitle.toString());
        mSongPost.onPOst(msong);
    }

    public interface SongPost {

        void onPOst(Song song);

    }
}