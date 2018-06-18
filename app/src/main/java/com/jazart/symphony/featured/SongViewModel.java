package com.jazart.symphony.featured;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jazart.symphony.location.LocationHelperRepo;
import com.jazart.symphony.model.Song;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import static com.jazart.symphony.Constants.SONGS;
import static com.jazart.symphony.Constants.USERS;
import static com.jazart.symphony.MainActivity.sDb;

/**
 * Created by kendrickgholston on 4/27/18.
 * Used for helping the user
 */

public class SongViewModel extends AndroidViewModel {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private LiveData<List<Song>> mSongsLiveData;
    public SongViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mSongsLiveData = LocationHelperRepo.getInstance().getNearbySongs();
    }

    public LiveData<List<Song>> getSongs() {
        return mSongsLiveData;
    }

    public void update() {
        mSongsLiveData = LocationHelperRepo.getInstance().getNearbySongs();
    }

    public Task<List<Song>> getUserSongs() {


        Query query = sDb.collection(SONGS).whereEqualTo("author", mUser.getUid());
        Log.d("DEBUG",query.toString());
        return query.get()
                .continueWith(new Continuation<QuerySnapshot, List<Song>>() {
                    @Override
                    public List<Song> then(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot snapshot = task.getResult();
                        List<Song> songs = snapshot.toObjects(Song.class);
                        return songs;

                    }
                });
    }

    public void addSongToStorage(final Song song) {

        Uri songPath = Uri.parse(song.getURI());

        try {
            ContentResolver songResolver = getApplication().getContentResolver();

            InputStream songStream = songResolver.openInputStream(songPath);
            songResolver.getType(songPath);

            StorageReference store = FirebaseStorage.getInstance().getReference();

            final StorageReference songRef = store.child(USERS +
                    "/" +
                    mUser.getUid() +
                    "/" +
                    SONGS +
                    "/" +
                    song.getName());
            UploadTask songTask = songRef.putStream(songStream);


            songTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(getApplication(), "Successful Upload", Toast.LENGTH_SHORT).show();
                    songRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            addSongToDb(downloadUrl, song);
                        }
                    });
                }
            });
            songTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //      Toast.makeText(getApplicationContext(), "Unsuccessful Upload", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void addSongToDb(Uri downloadUrl, Song song) {
        String link = downloadUrl.toString();
        song.setURI(link);
        song.setAuthor(mUser.getUid());
        sDb.collection(SONGS).add(song);
    }


}
