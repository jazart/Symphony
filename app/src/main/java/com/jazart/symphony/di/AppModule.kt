package com.jazart.symphony.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.jazart.symphony.MainActivity
import com.jazart.symphony.featured.FeaturedMusicFragment
import com.jazart.symphony.featured.MusicAdapter
import com.jazart.symphony.network.FoursquareConstants
import com.jazart.symphony.network.FoursquareRepo
import com.jazart.symphony.posts.UploadDialog
import com.jazart.symphony.venues.LocalEventsFragment
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

class App : Application() {
    lateinit var component: AppComponent
    @Inject
    lateinit var viewModelFactory: SimpleViewModelFactory

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().apply {
            appModule(AppModule(this@App))
        }.build()
    }

}

@Module
class AppModule(private val app: App) {

    @Singleton
    @Provides
    fun provideApplication(): App = app

    @Singleton
    @Provides
    fun provideContext(): Context = app.applicationContext

    @Singleton
    @Provides
    fun provideResources(): Resources = app.resources

    @Singleton
    @Provides
    fun provideSimpleViewModelFactory(): SimpleViewModelFactory =
            SimpleViewModelFactory(app = app)

    @Provides
    fun provideMusicFragment(): FeaturedMusicFragment = FeaturedMusicFragment()

    @Singleton
    @Provides
    fun provideSimpleExoPlayer(): SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(app.applicationContext),
            DefaultTrackSelector(),
            DefaultLoadControl())

    @Singleton
    @Provides
    fun provideRetrofit(client: okhttp3.OkHttpClient): Retrofit = Retrofit.Builder()
            .baseUrl(FoursquareConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @Singleton
    @Provides
    fun provideOkHttp(): okhttp3.OkHttpClient {
        val cache = Cache(app.cacheDir, 10 * 1024 * 1024)
        return okhttp3.OkHttpClient.Builder()
                .cache(cache)
                .build()
    }

    @Singleton
    @Provides
    fun provideVenueRepo(service: Retrofit): FoursquareRepo = FoursquareRepo(service)

    @Provides
    fun provideLocalEventsFragment(): LocalEventsFragment = LocalEventsFragment()
}

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    fun inject(app: App)
    fun inject(activity: MainActivity)
    fun inject(fragment: FeaturedMusicFragment)
    fun inject(fragment: UploadDialog)
    fun inject(adapter: MusicAdapter)
    fun inject(fragment: LocalEventsFragment)
}