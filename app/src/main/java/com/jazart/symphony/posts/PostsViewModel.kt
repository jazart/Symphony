package com.jazart.symphony.posts

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.jazart.symphony.common.BaseViewModel
import com.jazart.symphony.common.Event
import entities.Comment
import entities.Post
import kotlinx.coroutines.launch
import repo.PostRepository
import javax.inject.Inject
import javax.inject.Named

/**
 * This class serves as a data manager for the Post List and Post Detail screens.
 * Here we get data from the database and have them wrapped in livedata objects for a more reactive interface
 */
class PostsViewModel @Inject constructor(val repo: PostRepository, @Named("uId") val uId: String) : BaseViewModel() {

    private val commentsLiveData: MutableLiveData<List<Comment>> = MutableLiveData()
    private val _addPostResult: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val addPostResult: LiveData<Event<Boolean>> = _addPostResult
    val userPostsLiveData: LiveData<List<Post>> = liveData {
        val res = repo.loadPostsByUserId(uId)
        emit(res)
    }
    val nearbyPostsLiveData: LiveData<List<Post>> = locationRepo.nearbyPosts
    val comments: LiveData<List<Comment>> = commentsLiveData

    override fun load() {

    }

    fun update() {
        refreshContent()
    }

    fun addToDb(post: Post, postImageUri: Uri) {
        viewModelScope.launch {
            if (firebaseRepo.addPostToDb(post)) {
                _addPostResult.value = Event(true)
            } else {
                _addPostResult.value = Event(false)
            }
        }
    }

    fun deletePost(postId: String?) {
        if (postId.isNullOrEmpty()) {
            return
        }
        viewModelScope.launch {
            repo.deletePost(postId)
        }
    }

    private fun addComment(comment: Comment, id: String) {
        firebaseRepo.addPostComment(comment, id)
    }

    fun loadComments(id: String) {}

}
