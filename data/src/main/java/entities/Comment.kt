package entities

import java.io.Serializable

data class Comment(var content: String? = "",
                   var authorName: String? = "",
                   var authorId: String? = "",
                   var likes: Int = 0,
                   val mPostId: String = "",
                   var profilePic: String = "") : Serializable
