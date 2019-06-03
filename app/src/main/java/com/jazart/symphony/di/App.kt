package com.jazart.symphony.di

import android.app.Application
import androidx.fragment.app.Fragment
import com.squareup.leakcanary.LeakCanary

class App : Application() {
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return
        LeakCanary.install(this)
        component = DaggerAppComponent.factory().create(this, applicationContext)
    }
}

fun Fragment.app(): App = requireActivity().application as App
