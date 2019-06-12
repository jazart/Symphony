package com.jazart.symphony.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jazart.symphony.featured.SongViewModel
import com.jazart.symphony.posts.PostsViewModel
import com.jazart.symphony.profile.ProfileViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER)
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
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: SimpleViewModelFactory): ViewModelProvider.Factory
}