package com.jazart.symphony.di

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import com.jazart.symphony.featured.FeaturedMusicFragment
import com.jazart.symphony.venues.LocalEventsFragment
import dagger.Module
import dagger.Provides


@Module
object AppModule {
    @JvmStatic
    @Provides
    fun provideConnectivity(context: Context): ConnectivityManager {
        return context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @JvmStatic
    @Provides
    fun provideMusicFragment(): FeaturedMusicFragment = FeaturedMusicFragment()

    @JvmStatic
    @Provides
    fun provideLocalEventsFragment(): LocalEventsFragment = LocalEventsFragment()

}
