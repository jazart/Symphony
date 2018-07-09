package com.jazart.symphony.di

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.jazart.symphony.featured.FeaturedMusicFragment
import dagger.Component
import dagger.Module
import dagger.Provides
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

}

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    fun inject(app: App)
    fun inject(activity: Activity)
    fun inject(fragment: FeaturedMusicFragment)
}