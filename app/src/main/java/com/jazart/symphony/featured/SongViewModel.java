package com.jazart.symphony.featured;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jazart.symphony.location.LocationHelper;
import com.jazart.symphony.model.Song;

import java.util.List;

import static com.jazart.symphony.Constants.SONGS;
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
        mSongsLiveData = LocationHelper.getInstance().getNearbySongs();
    }

    public LiveData<List<Song>> getSongs() {
        return mSongsLiveData;
    }

    public void update() {
        mSongsLiveData = LocationHelper.getInstance().getNearbySongs();
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


}