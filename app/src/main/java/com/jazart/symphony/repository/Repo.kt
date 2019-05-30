package com.jazart.symphony.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jazart.symphony.Result
import javax.inject.Inject

//interface Repo<T> {
//    suspend fun update(): Result
//    suspend fun create(item: T): Result
//    suspend fun delete(id: String): Result
//    suspend fun load(): List<T>
//    suspend fun single(): Result
//}
//
//abstract class Repo<T> @Inject constructor(protected val db: FirebaseFirestore,
//                                           protected val auth: FirebaseAuth,
//                                           protected val storage: FirebaseStorage) {
//    abstract suspend fun update(): Result
//    suspend fun create(item: T): Result {
//
//        retur
//    }
//    suspend fun delete(id: String): Result
//    suspend fun load(): List<T>
//    suspend fun single(): Result
//}