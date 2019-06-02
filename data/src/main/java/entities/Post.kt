package entities

import java.io.Serializable
import java.util.*

data class Post @JvmOverloads constructor(var title: String = "",
                                          var body: String = "",
                                          var author: String = "",
                                          var imageUri: String? = "",
                                          var postDate: Date = Date(),
                                          var profilePic: String = "",
                                          var authorName: String? = "",
                                          var likes: Int = 0,
                                          var comments: MutableList<Comment>? = mutableListOf(),
                                          var id: String? = "") : Serializable