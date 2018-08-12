package com.jazart.symphony.posts;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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
        super.onActive();
        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    setValue(task.getResult().toObjects(UserPost.class));
                }
            }
        });
    }
}
