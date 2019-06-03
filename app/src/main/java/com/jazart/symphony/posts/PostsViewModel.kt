package com.jazart.symphony.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.jazart.symphony.common.BaseViewModel
import com.jazart.symphony.common.Event
import entities.Comment
import entities.Post
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This class serves as a data manager for the Post List and Post Detail screens.
 * Here we get data from the database and have them wrapped in livedata objects for a more reactive interface
 */
class PostsViewModel @Inject constructor(val repo: com.jazart.data.repo.PostRepository) : BaseViewModel() {

    private val commentsLiveData: MutableLiveData<List<Comment>> = MutableLiveData()
    private val _addPostResult: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val addPostResult: LiveData<Event<Boolean>> = _addPostResult
    val userPostsLiveData: LiveData<List<Post>> = liveData {
        val res = repo.loadPostsByUserId("vrRkPz137ecacZK4U4nm8KtfPjg1")
        emit(res)
    }
    val nearbyPostsLiveData: LiveData<List<Post>> = locationRepo.nearbyPosts
    val comments: LiveData<List<Comment>> = commentsLiveData

    fun update() {
        refreshContent()
    }

    fun addToDb(post: Post) {
        viewModelScope.launch {
            if (firebaseRepo.addPostToDb(post)) {
                _addPostResult.value = Event(true)
            } else {
                _addPostResult.value = Event(false)
            }
        }
    }

    fun deletePost(postId: String?) {
        if (postId != null) firebaseRepo.deletePost(postId)
        refreshContent()
    }

    private fun addComment(comment: Comment, id: String) {
        firebaseRepo.addPostComment(comment, id)
    }

    fun loadComments(id: String) {}

}
