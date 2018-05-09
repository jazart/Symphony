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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


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


        Log.d("DEBUG",mSongTitle.getEditText().getText().toString());
        Log.d("DEBUG",msong.getURI().toString());
        List<String> result = new List<String>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] a) {
                return null;
            }

            @Override
            public boolean add(String s) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, @NonNull Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public String get(int index) {
                return null;
            }

            @Override
            public String set(int index, String element) {
                return null;
            }

            @Override
            public void add(int index, String element) {

            }

            @Override
            public String remove(int index) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<String> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<String> listIterator(int index) {
                return null;
            }

            @NonNull
            @Override
            public List<String> subList(int fromIndex, int toIndex) {
                return null;
            }
        };
        if(mArtists.getEditText().getText().toString().contains(",")){
            String[] artists = mArtists.getEditText().getText().toString().split(",");
            for(int j = 0; j < artists.length;i++){
                result.add(artists[j]);
            }

        }
        else{
            result.add(mArtists.getEditText().getText().toString());
        }
        msong.setName(mSongTitle.getEditText().getText().toString());
        msong.setArtists(result);
        mSongPost.onPost(msong);

    }

    public interface SongPost {

        void onPost(Song song);

    }
}
