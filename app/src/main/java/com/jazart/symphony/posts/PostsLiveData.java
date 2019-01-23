package com.jazart.symphony.posts;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class PostsLiveData<T> extends LiveData<T> {
    private final Query mQuery;

    public PostsLiveData(Query query) {
        mQuery = query;
    }

    public PostsLiveData(CollectionReference reference) {
        mQuery = reference;
    }

    @SuppressWarnings("unchecked Cast")
    @Override
    protected void onActive() {
        super.onActive();
        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        setValue((T) task.getResult().toObjects(Post.class));
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
