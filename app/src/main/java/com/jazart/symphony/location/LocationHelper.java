package com.jazart.symphony.location;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jazart.symphony.model.User;
import com.jazart.symphony.posts.UserPost;

import java.util.ArrayList;
import java.util.List;

import static com.jazart.symphony.Constants.POSTS;
import static com.jazart.symphony.Constants.USERS;
import static com.jazart.symphony.MainActivity.sDb;

public class LocationHelper {
    private static LocationHelper INSTANCE;
    private User mUser;
    private DocumentReference mReference;
    private MutableLiveData<List<User>> mNearbyUsers;

    private LocationHelper(String uId) {
        mReference = sDb.collection(USERS).document(uId);
        mNearbyUsers = new MutableLiveData<>();
        getUserById();
    }

    public static LocationHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocationHelper(FirebaseAuth.getInstance().getUid());
        }

        return INSTANCE;

    }

    public LiveData<List<User>> getNearbyUsers() {
        return mNearbyUsers;
    }

    private void findNearbyUsers() {
        Query query = sDb.collection(USERS).whereEqualTo("city", mUser.getCity());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mNearbyUsers.setValue(task.getResult().toObjects(User.class));
                findNearbyPosts();
            }
        });
    }

    private void getUserById() {
        mReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mUser = task.getResult().toObject(User.class);
                findNearbyUsers();
            }
        });
    }

    private void findNearbyPosts() {
        CollectionReference reference = sDb.collection(POSTS);
        final List<UserPost> posts = new ArrayList<>();

        if (getNearbyUsers().getValue() != null) {
            for (int i = 0; i < getNearbyUsers().getValue().size(); i++) {
                reference.whereEqualTo("author", getNearbyUsers().getValue().get(i).getId())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (int j = 0; j < task.getResult().toObjects(UserPost.class).size(); j++) {
                            UserPost post = task.getResult().toObjects(UserPost.class).get(j);
                            posts.add(post);
                        }
                    }
                });
            }
        }
    }


}
