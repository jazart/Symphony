package com.jazart.symphony.repository

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.jazart.symphony.Constants
import com.jazart.symphony.MainActivity
import com.jazart.symphony.Result
import com.jazart.symphony.model.Song
import com.jazart.symphony.model.User
import com.jazart.symphony.posts.Post

class UserRepo(val uId: String, val db: FirebaseFirestore, val auth: FirebaseAuth) : Repo<User> {


    override suspend fun create(item: User): Result {
        return try{
            addToDb()
            setUpUser(auth.currentUser, item.name,item.photoURI)
            Result.Success
        }catch (e:Exception){
            Result.Failure(e,null)
        }

    }

    override suspend fun delete(id: String): Result {
        return Result.Success
    }

    override suspend fun load(): List<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private suspend fun getUserById() {

    }

    suspend fun loadUserData() {

    }

    override suspend fun update(): Result {
        return Result.Success
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