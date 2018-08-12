package com.jazart.symphony.posts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.jazart.symphony.R;
import com.jazart.symphony.di.App;
import com.jazart.symphony.di.SimpleViewModelFactory;
import com.jazart.symphony.featured.SongViewModel;
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
    private Song mSong = new Song();

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
        mSongViewModel.getProgressLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer progress) {
                mUploadProgress.setVisibility(View.VISIBLE);
                mUploadProgress.setProgress(Objects.requireNonNull(progress));
                if (progress == 100) dismiss();
            }
        });
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_upload_dialog, null);

        mArtists = view.findViewById(R.id.enter_artists);
        mSongTitle = view.findViewById(R.id.enter_song_title);
        mUploadProgress = view.findViewById(R.id.upload_progress);
        Button uploadButton = view.findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> result = getArtistsFromUi();
                mSong.setName(Objects.requireNonNull(mSongTitle.getEditText()).getText().toString());
                mSong.setArtists(result);
                try {
                    mSongViewModel.addSongToStorage(mSong, convertSongUriToFile());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
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

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        List<String> result = getArtistsFromUi();
        mSong.setName(Objects.requireNonNull(mSongTitle.getEditText()).getText().toString());
        mSong.setArtists(result);
        try {
            mSongViewModel.addSongToStorage(mSong, convertSongUriToFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private FileInputStream convertSongUriToFile() throws FileNotFoundException {
        ContentResolver resolver = requireActivity().getContentResolver();
        return (FileInputStream) resolver.openInputStream(Uri.parse(mSong.getURI()));
    }

    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Images.Media.DATA;
                } else if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Audio.Media.DATA;
                } else if (uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        Objects.requireNonNull(cursor);
        cursor.close();
        return ret;
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
