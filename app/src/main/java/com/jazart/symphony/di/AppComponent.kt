package com.jazart.symphony.di

import android.content.Context
import com.jazart.symphony.common.MainActivity
import com.jazart.symphony.featured.FeaturedMusicFragment
import com.jazart.symphony.featured.MusicAdapter
import com.jazart.symphony.featured.UploadDialog
import com.jazart.symphony.location.LocationIntentService
import com.jazart.symphony.posts.NewPostFragment
import com.jazart.symphony.posts.PostDetailFragment
import com.jazart.symphony.posts.PostsFragment
import com.jazart.symphony.profile.ProfileFragment
import com.jazart.symphony.profile.UserFriendsFragment
import com.jazart.symphony.profile.UserSongsFragment
import com.jazart.symphony.signup.SignupFragment
import com.jazart.symphony.venues.LocalEventsFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class, DataModule::class, FirebaseModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: App, @BindsInstance context: Context): AppComponent
    }

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
    fun inject(fragment: SignupFragment)
    fun inject(service: LocationIntentService)
}
