package com.jazart.symphony.posts;

import android.arch.lifecycle.LiveData;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class PostsLiveData extends LiveData {
    private final Query mQuery;

    public PostsLiveData(Query query) {
        mQuery = query;
    }

    public PostsLiveData(CollectionReference reference) {
        mQuery = reference;
    }

    @Override
    protected void onActive() {
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (snapshots != null) {
                    setValue(snapshots.toObjects(UserPost.class));
                }
            }
        });
    }

}
