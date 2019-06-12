package com.jazart.symphony.di

import android.net.ConnectivityManager
import com.google.firebase.firestore.FirebaseFirestore
import com.jazart.data.repo.PostRepository
import com.jazart.data.repo.UserRepository
import com.jazart.symphony.repository.InMemoryDataSource
import com.jazart.symphony.repository.posts.FirebaseOfflinePostDataSource
import com.jazart.symphony.repository.posts.FirebaseOnlinePostDataSource
import com.jazart.symphony.repository.posts.PostRepositoryImpl
import com.jazart.symphony.repository.users.FetchStrategy
import com.jazart.symphony.repository.users.FirebaseOfflineUserDataSource
import com.jazart.symphony.repository.users.FirebaseOnlineUserDataSource
import com.jazart.symphony.repository.users.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import entities.Post
import entities.User

@Module
object DataModule {
    @JvmStatic
    @Provides
    fun provideStrategy(): FetchStrategy = FetchStrategy.NETWORK_FIRST

    @JvmStatic
    @Provides
    fun provideFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @JvmStatic
    @Provides
    fun providePostRepository(connection: ConnectivityManager, memory: InMemoryDataSource<Post>,
                              disk: FirebaseOfflinePostDataSource, network: FirebaseOnlinePostDataSource): PostRepository {
        return PostRepositoryImpl(connection, memory, disk, network)
    }

    @JvmStatic
    @Provides
    fun provideUserRepository(connection: ConnectivityManager, memory: InMemoryDataSource<User>,
                              disk: FirebaseOfflineUserDataSource, network: FirebaseOnlineUserDataSource): UserRepository {
        return UserRepositoryImpl(connection, memory, disk, network)
    }
}