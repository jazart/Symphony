package entities

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class Comment(var content: String? = "",
                   var authorName: String? = "",
                   var authorId: String? = "",
                   var likes: Int = 0,
                   val mPostId: String = "",
                   var profilePic: Uri = Uri.EMPTY) : Serializable
