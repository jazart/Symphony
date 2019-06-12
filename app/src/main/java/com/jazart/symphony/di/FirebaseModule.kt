package com.jazart.symphony.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object FirebaseModule {
    @Provides @JvmStatic
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @JvmStatic
    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides @JvmStatic @Named("uId")
    fun provideUId(): String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
}
