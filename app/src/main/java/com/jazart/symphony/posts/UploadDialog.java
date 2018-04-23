package com.jazart.symphony.posts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import com.jazart.symphony.model.Song;


public class UploadDialog extends DialogFragment  implements DialogInterface.OnClickListener {
    public static final String TAG = "UploadDialog";
    public static final String ARG_URI = "1";

    private View mView;
    private Uri selectedMP3;
    private TextInputLayout mArtists;
    private TextInputLayout mNameLayout;
    private TextInputLayout mSongTitle;
    private SongPost mSongPost;
    private Song msong = new Song();






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
        if(getArguments() != null){
            msong.setURI((getArguments().getString(ARG_URI)));
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
        //super.show(manager, tag);
//        try {
            manager.beginTransaction()
                    .add(this, tag)
                    .commitAllowingStateLoss();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
    }

    /*
            Implementing on click for dialog buttons. Validates email and password
            then sends result back to to the fragment to sign up via firebase
             */
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

       // sendResult(Activity.RESULT_OK,mArtists.toString(),mSongTitle.toString());
        //msong.setArtists(mArtists.toString());
        Log.d("DEBUG",mSongTitle.getEditText().getText().toString());
        Log.d("DEBUG",msong.getURI().toString());
        if(mArtists.getEditText().getText().toString().contains(",")){
            String[] artists = mArtists.getEditText().getText().toString().split(",");
            for(int j = 0; j < artists.length;i++){

            }

        }
        msong.setName(mSongTitle.getEditText().getText().toString());
        mSongPost.onPost(msong);

    }

    public interface SongPost {

        void onPost(Song song);

    }
}
