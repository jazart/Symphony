package com.jazart.symphony.posts;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.jazart.symphony.BaseViewModel;

import java.util.List;
import java.util.Objects;

import static com.jazart.symphony.Constants.POSTS;
import static com.jazart.symphony.MainActivity.sDb;

/**
 * This class serves as a data manager for the Post List and Post Detail screens.
 * Here we get data from the database and have them wrapped in livedata objects for a more reactive interface
 */
public class PostsViewModel extends BaseViewModel {
    private final FirebaseUser mUser;
    private final MutableLiveData<List<Comment>> mComments;

    public PostsViewModel() {
        super();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        LiveData<List<UserPost>> userPostsLiveData = Objects.requireNonNull(Objects.requireNonNull(getLocationRepo())).getNearbyPosts();
        mComments = new MutableLiveData<>();
        PostsLiveData postsLiveData = getFirebaseRepo().getUserPosts();
    }

    public LiveData<List<UserPost>> getUserPostsLiveData() {
        return Objects.requireNonNull(getLocationRepo()).getNearbyPosts();
    }

    public LiveData<List<Comment>> getComments() {
        return mComments;
    }

    public void update() {
        refreshContent();
    }

    public void addToDb(@NonNull UserPost post) {
        getFirebaseRepo().addPostToDb(post);
    }

    public void deletePost(String postId) {
        getFirebaseRepo().deletePost(postId);
        refreshContent();
    }

    private void addComment(Comment comment, String id) {
        getFirebaseRepo().addPostComment(comment, id);
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
