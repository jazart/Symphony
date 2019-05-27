package com.jazart.symphony.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class SimpleViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return creator() as T
    }
}