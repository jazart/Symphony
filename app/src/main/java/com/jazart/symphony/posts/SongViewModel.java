package com.jazart.symphony.posts;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jazart.symphony.model.Song;

import java.util.List;

import static com.jazart.symphony.Constants.POSTS;
import static com.jazart.symphony.MainActivity.sDb;

/**
 * Created by kendrickgholston on 4/27/18.
 */

public class SongViewModel extends AndroidViewModel {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private LiveData<List<Song>> mSongsLiveData;
    public SongViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public LiveData<List<Song>> getSongs() {
        return mSongsLiveData;
    }

    public Task<List<Song>> getUserSongs() {


        Query query = sDb.collection(POSTS).whereEqualTo("author", mUser.getUid());
        return query.get()
                .continueWith(new Continuation<QuerySnapshot, List<Song>>() {
                    @Override
                    public List<Song> then(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot snapshot = task.getResult();
                        return snapshot.toObjects(Song.class);
                    }
                });

    }



}
