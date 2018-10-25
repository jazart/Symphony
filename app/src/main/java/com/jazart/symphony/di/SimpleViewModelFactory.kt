package com.jazart.symphony.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jazart.symphony.featured.SongViewModel
import javax.inject.Inject
import javax.inject.Named


class SimpleViewModelFactory @Inject constructor(@param:Named("app") val app: App) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SongViewModel(app = this.app) as T
    }
}