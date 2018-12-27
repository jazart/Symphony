package com.jazart.symphony.di

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jazart.symphony.featured.SongViewModel
import javax.inject.Inject
import javax.inject.Named


class SimpleViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return creator() as T
    }
}