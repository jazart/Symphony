package com.jazart.symphony.di

import android.app.Application
import android.app.Service
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.jazart.symphony.MainActivity
import com.jazart.symphony.featured.FeaturedMusicFragment
import com.jazart.symphony.featured.MusicAdapter
import com.jazart.symphony.featured.SongViewModel
import com.jazart.symphony.featured.UploadDialog
import com.jazart.symphony.posts.*
import com.jazart.symphony.profile.ProfileFragment
import com.jazart.symphony.profile.UserFriendsFragment
import com.jazart.symphony.profile.UserSongsFragment
import com.jazart.symphony.repository.*
import com.jazart.symphony.venues.LocalEventsFragment
import com.squareup.leakcanary.LeakCanary
import dagger.*
import dagger.multibindings.IntoMap
import okhttp3.Cache
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

class App : Application() {
    lateinit var component: AppComponent

    @Inject
    lateinit var player: SimpleExoPlayer

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return
        LeakCanary.install(this)
        component = DaggerAppComponent.builder().run {
            appModule(AppModule(this@App))
            build()
        }
        component.inject(this)
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
    fun provideConnectivity(context: Context): ConnectivityManager {
        return context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Singleton
    @Provides
    fun provideResources(): Resources = app.resources

    @Provides
    fun provideMusicFragment(): FeaturedMusicFragment = FeaturedMusicFragment()

    @Provides
    fun provideSimpleExoPlayer(): SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(app.applicationContext),
            DefaultTrackSelector(),
            DefaultLoadControl()).apply {
        audioAttributes = com.google.android.exoplayer2.audio.AudioAttributes.Builder().run {
            setUsage(C.USAGE_MEDIA)
            setContentType(C.CONTENT_TYPE_MUSIC)
            build()
        }
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: okhttp3.OkHttpClient): Retrofit = Retrofit.Builder()
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

    @Provides
    fun provideLocalEventsFragment(): LocalEventsFragment = LocalEventsFragment()

    @Provides
    fun provideStrategy(): FetchStrategy = FetchStrategy.NETWORK_FIRST

    @Provides
    fun providePostRepository(connection: ConnectivityManager, memory: InMemoryDataSource<Post>,
                              disk: FirebaseOfflinePostDataSource, network: FirebaseOnlinePostDataSource): PostRepository {
        return RepositoryImpl(connection, memory, disk, network)
    }
}


@Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PostsViewModel::class)
    abstract fun bindPostViewModel(postsViewModel: PostsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SongViewModel::class)
    abstract fun bindSongViewModel(SongViewModel: SongViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: SimpleViewModelFactory): ViewModelProvider.Factory
}

@Singleton
@Component(modules = [(AppModule::class), ViewModelModule::class])
interface AppComponent {
    fun inject(app: App)
    fun inject(activity: MainActivity)
    fun inject(fragment: FeaturedMusicFragment)
    fun inject(fragment: UploadDialog)
    fun inject(adapter: MusicAdapter)
    fun inject(fragment: LocalEventsFragment)
    fun inject(fragment: UserSongsFragment)
    fun inject(adapter: PostDetailFragment)
    fun inject(adapter: PostsFragment)
    fun inject(fragment: UserFriendsFragment)
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: NewPostFragment)


}

fun Fragment.app(): App = requireActivity().application as App