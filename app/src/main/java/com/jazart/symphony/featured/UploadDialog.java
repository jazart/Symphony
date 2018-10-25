package com.jazart.symphony.featured;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.jazart.symphony.R;
import com.jazart.symphony.di.App;
import com.jazart.symphony.di.SimpleViewModelFactory;
import com.jazart.symphony.model.Song;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;


public class UploadDialog extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String TAG = "UploadDialog";
    private static final String ARG_URI = "1";

    private SongViewModel mSongViewModel;
    private TextInputLayout mArtists;
    private TextInputLayout mSongTitle;
    private ProgressBar mUploadProgress;
    private final Song mSong = new Song();

    @Inject
    SimpleViewModelFactory mViewModelFactory;

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
        inject();
        mSongViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SongViewModel.class);
        if(getArguments() != null){
            mSong.setURI((getArguments().getString(ARG_URI)));
        }
        updateProgress();
    }

    private void updateProgress() {
        mSongViewModel.getPercentageLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer progress) {
                mUploadProgress.setVisibility(View.VISIBLE);
                mUploadProgress.setProgress(progress, true);
                if (progress == 100) dismiss();
            }
        });
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_upload_dialog, null);

        mArtists = view.findViewById(R.id.enter_artists);
        mSongTitle = view.findViewById(R.id.enter_song_title);
        mUploadProgress = view.findViewById(R.id.upload_progress);
        view.findViewById(R.id.upload_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> result = getArtistsFromUi();
                mSong.setName(Objects.requireNonNull(mSongTitle.getEditText()).getText().toString());
                mSong.setArtists(result);

                String size = getFileSize();
                try {
                    mSongViewModel.addSongToStorage(mSong, convertSongUriToFile(), size);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Upload")
                .create();

    }

    @Override
    public void show(FragmentManager manager, String tag) {
        manager.beginTransaction()
                .add(this, tag)
                .commitAllowingStateLoss();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        List<String> result = getArtistsFromUi();
        mSong.setName(Objects.requireNonNull(mSongTitle.getEditText()).getText().toString());
        mSong.setArtists(result);
        try {
            String size = getFileSize();
            Log.d("TAG/ UPLOAD", size);
            mSongViewModel.addSongToStorage(mSong, convertSongUriToFile(), size);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private FileInputStream convertSongUriToFile() throws FileNotFoundException {
        ContentResolver resolver = requireActivity().getContentResolver();
        return (FileInputStream) resolver.openInputStream(Uri.parse(mSong.getURI()));
    }

    private String getFileSize() {
        ContentResolver resolver = requireActivity().getContentResolver();
        String [] projection = {MediaStore.Audio.Media.SIZE};
        Cursor cursor = resolver.query(Uri.parse(mSong.getURI()), projection, null, null, null);
        String fileSize = "";

        assert cursor != null;
        if(cursor.moveToFirst()) {
            int size = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            fileSize = cursor.getString(size);
            cursor.close();
        }
        return fileSize;
    }

    @NonNull
    private List<String> getArtistsFromUi() {
        List<String> result = new ArrayList<>();

        if (Objects.requireNonNull(mArtists.getEditText()).getText().toString().contains(",")) {
            String[] artists = mArtists.getEditText().getText().toString().split(",");
            result = Arrays.asList(artists);
        } else {
            result.add(mArtists.getEditText().getText().toString());
        }
        return result;
    }

    private void inject() {
        App app = (App) requireActivity().getApplication();
        app.component.inject(this);
    }
}
