package com.jazart.symphony.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jazart.symphony.R
import com.jazart.symphony.di.app

class UserFriendsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        app().component.inject(this)
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

}