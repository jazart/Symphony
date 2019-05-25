package com.jazart.symphony

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = fragmentManager?.let { ProfileAdapter(it) } ?: return
        profileViewPager.adapter = adapter
        profileTabLayout.setupWithViewPager(profileViewPager)
    }

}

internal class ProfileAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Songs"
            1 -> "Posts"
            2 -> "Friends"
            else -> throw IllegalArgumentException()
        }
    }
    override fun getItem(position: Int): Fragment =
            when (position) {
                0 -> Fragment()
                1 -> Fragment()
                2 -> Fragment()
                else -> throw IllegalArgumentException()
            }

    override fun getCount() = 3
}