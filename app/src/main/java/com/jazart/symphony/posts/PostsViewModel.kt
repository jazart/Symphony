package com.jazart.symphony.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.net.Uri
import androidx.lifecycle.viewModelScope

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jazart.symphony.BaseViewModel
import com.jazart.symphony.Result
import kotlinx.coroutines.launch

/**
 * This class serves as a data manager for the Post List and Post Detail screens.
 * Here we get data from the database and have them wrapped in livedata objects for a more reactive interface
 */
class PostsViewModel : BaseViewModel() {
    private val mUser: FirebaseUser?
    private val commentsLiveData: MutableLiveData<List<Comment>> = MutableLiveData()
    private val _addPostResult: MutableLiveData<Result> = MutableLiveData()

    val addPostResult: LiveData<Result> = _addPostResult
    val userPostsLiveData: LiveData<List<Post>> = locationRepo.nearbyPosts
    val comments: LiveData<List<Comment>> = commentsLiveData
    val userProfilePic: Uri?
        get() = mUser!!.photoUrl

    init {
        val auth = FirebaseAuth.getInstance()
        mUser = auth.currentUser
    }

    fun update() {
        refreshContent()
    }

    fun addToDb(post: Post) {
        viewModelScope.launch {
            if(firebaseRepo.addPostToDb(post)) {
                _addPostResult.value = Result.Success
            } else {
                _addPostResult.value = Result.Failure(message = null)
            }
        }
    }

    fun deletePost(postId: String) {
        firebaseRepo.deletePost(postId)
        refreshContent()
    }

    private fun addComment(comment: Comment, id: String) {
        firebaseRepo.addPostComment(comment, id)
    }

    fun loadComments(id: String) {}

}
