package com.jazart.symphony.location;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jazart.symphony.model.Song;
import com.jazart.symphony.model.User;
import com.jazart.symphony.posts.UserPost;

import java.util.ArrayList;
import java.util.List;

import static com.jazart.symphony.Constants.POSTS;
import static com.jazart.symphony.Constants.SONGS;
import static com.jazart.symphony.Constants.USERS;
import static com.jazart.symphony.MainActivity.TAG;
import static com.jazart.symphony.MainActivity.sDb;

/*
singleton class that serves as a hub for querying and propagating localized data to our other fragment classes
to render them in the ui
Here we make several calls to find users in the same city and expose their posts and data to other users
 */
public class LocationHelper {
    private static LocationHelper INSTANCE;
    private User mUser;
    private DocumentReference mReference;
    private MutableLiveData<List<User>> mNearbyUsers;
    private MutableLiveData<List<UserPost>> mPosts;
    private MutableLiveData<List<Song>> mSongs;

    private LocationHelper(String uId) {
        mReference = sDb.collection(USERS).document(uId);
        mNearbyUsers = new MutableLiveData<>();
        mSongs = new MutableLiveData<>();
        mPosts = new MutableLiveData<>();
        getUserById();
    }

    public static LocationHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocationHelper(FirebaseAuth.getInstance().getUid());
        }

        return INSTANCE;

    }

    public void update() {
        findNearbyUsers();
        findNearbyPosts();
        findNearbySongs();
    }

    public String getUserLocation() {
        GeoPoint geoPoint = mUser.getLocation();
        String latitude = String.valueOf(geoPoint.getLatitude());
        String longitude = String.valueOf(geoPoint.getLongitude());
        return latitude + "," + longitude;
    }
    public LiveData<List<Song>> getNearbySongs() {
        return mSongs;
    }

    public LiveData<List<User>> getNearbyUsers() {
        return mNearbyUsers;
    }

    public LiveData<List<UserPost>> getNearbyPosts() {
        return mPosts;
    }

    private void findNearbyUsers() {
        Query query = sDb.collection(USERS).whereEqualTo("city", mUser.getCity());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mNearbyUsers.setValue(task.getResult().toObjects(User.class));
                findNearbyPosts();
                findNearbySongs();
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
                            post.setId(task.getResult().getDocuments().get(j).getId());
                            posts.add(post);

                        }
                        mPosts.setValue(posts);
                    }
                });
            }
        }
    }

    private void findNearbySongs() {
        CollectionReference reference = sDb.collection(SONGS);
        final List<Song> songs = new ArrayList<>();

        for (int i = 0; i < getNearbyUsers().getValue().size(); i++) {
            reference.whereEqualTo("author", getNearbyUsers().getValue().get(i).getId())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (int j = 0; j < task.getResult().size(); j++) {
                        songs.add(task.getResult().toObjects(Song.class).get(j));
                    }
                    mSongs.setValue(songs);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.wtf(TAG, "WHY THE FUKC WONT YOU LOADDDD");
                        }
                    });

        }
    }


}
