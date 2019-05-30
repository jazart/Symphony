package com.jazart.symphony.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.jazart.symphony.Constants
import com.jazart.symphony.Result
import com.jazart.symphony.model.Song
import com.jazart.symphony.model.User
import javax.inject.Inject

class UserRepo @Inject constructor(private val uId: String,
                                   private val db: FirebaseFirestore,
                                   private val auth: FirebaseAuth) : Repo<User> {

    override suspend fun create(item: User): Result {
        return try {
            addToDb()
            setUpUser(auth.currentUser, item.name, item.photoURI)
            Result.Success
        } catch (e: Exception) {
            Result.Failure(e, null)
        }
    }

    override suspend fun delete(id: String): Result {
        return Result.Success
    }

    override suspend fun load(): List<User> {
        return emptyList()
    }

    override suspend fun update(): Result {
        return Result.Success
    }

    override suspend fun single(): Result {
        return Result.SuccessWithData(Song())
    }

    private suspend fun addToDb() {
        val user = db.collection(Constants.USERS).document(uId).get().await().toObject(User::class.java)
        db.collection(Constants.USERS).document(user!!.id)
                .set(User(auth.currentUser))
    }

    private fun setUpUser(user: FirebaseUser?, name: String, photoUrl: Uri) {
        val changeRequest = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photoUrl)
                .build()
        user?.updateProfile(changeRequest)
    }
}