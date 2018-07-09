package com.jazart.symphony.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.jazart.symphony.featured.SongViewModel
import javax.inject.Inject
import javax.inject.Named


class SimpleViewModelFactory @Inject constructor(@param:Named("app") val app: App) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SongViewModel(app = this.app) as T
    }
}