package com.jazart.symphony.posts;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jazart.symphony.location.LocationHelper;

import java.util.List;

import static com.jazart.symphony.Constants.POSTS;
import static com.jazart.symphony.MainActivity.sDb;

public class PostsViewModel extends AndroidViewModel {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private LiveData<List<UserPost>> mUserPostsLiveData;
    private MutableLiveData<List<Comment>> mComments;

    public PostsViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserPostsLiveData = LocationHelper.getInstance().getNearbyPosts();
        mComments = new MutableLiveData<>();
    }

    public LiveData<List<UserPost>> getPosts() {
        return mUserPostsLiveData;
    }

    public LiveData<List<Comment>> getComments() {
        return mComments;
    }

    public Task<List<UserPost>> getUserPosts() {


        Query query = sDb.collection(POSTS)
                .whereEqualTo("author", mUser.getUid())
                .orderBy("postDate");

        return query.get()
                .continueWith(new Continuation<QuerySnapshot, List<UserPost>>() {
                    @Override
                    public List<UserPost> then(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot snapshot = task.getResult();
                        return snapshot.toObjects(UserPost.class);
                    }
                });

    }

    public void deletePost(String postId) {
    }

    public void addComment(Comment comment, String id) {
        CollectionReference reference = sDb.collection(POSTS).document(id).collection("comments");
        reference.add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

            }
        });
    }

    public void loadComments(String id) {
        CollectionReference reference = sDb.collection(POSTS).document(id).collection("comments");
        Task<QuerySnapshot> query = reference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mComments.setValue(task.getResult().toObjects(Comment.class));
                    }
                });
    }

    public Uri getUserProfilePic() {
        return mUser.getPhotoUrl();
    }

}
