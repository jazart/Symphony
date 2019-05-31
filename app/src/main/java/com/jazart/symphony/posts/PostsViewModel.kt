package com.jazart.symphony.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.net.Uri
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jazart.symphony.BaseViewModel
import com.jazart.symphony.Error
import com.jazart.symphony.Event
import com.jazart.symphony.Result
import com.jazart.symphony.Status
import com.jazart.symphony.repository.PostRepository
import com.jazart.symphony.repository.RepositoryImpl
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This class serves as a data manager for the Post List and Post Detail screens.
 * Here we get data from the database and have them wrapped in livedata objects for a more reactive interface
 */
class PostsViewModel @Inject constructor(val repo: PostRepository) : BaseViewModel() {
    private val mUser: FirebaseUser?
    private val commentsLiveData: MutableLiveData<List<Comment>> = MutableLiveData()
    private val _addPostResult: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val addPostResult: LiveData<Event<Boolean>> = _addPostResult
    val userPostsLiveData: LiveData<List<Post>> = liveData {
        val res = repo.loadPostsByUserId("vrRkPz137ecacZK4U4nm8KtfPjg1")
        emit(res)
    }
    val nearbyPostsLiveData: LiveData<List<Post>> = locationRepo.nearbyPosts
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
                _addPostResult.value = Event(true)
            } else {
                _addPostResult.value = Event(false)
            }
        }
    }

    fun deletePost(postId: String?) {
        if(postId != null) firebaseRepo.deletePost(postId)
        refreshContent()
    }

    private fun addComment(comment: Comment, id: String) {
        firebaseRepo.addPostComment(comment, id)
    }

    fun loadComments(id: String) {}

}
