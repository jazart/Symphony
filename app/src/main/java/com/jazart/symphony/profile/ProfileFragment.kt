package com.jazart.symphony.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jazart.symphony.R
import com.jazart.symphony.posts.PostsFragment
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
        loadProfilePic(Uri.parse("google.com"))
    }

    private fun loadProfilePic(uri: Uri) {
        Glide.with(this)
                .load(uri)
                .apply(RequestOptions().circleCrop().placeholder(resources.getDrawable(R.drawable.ic_account_circle_black_24dp, null)))
                .into(profilePicture)

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
                0 -> UserSongsFragment()
                1 -> PostsFragment()
                2 -> UserFriendsFragment()
                else -> throw IllegalArgumentException()
            }

    override fun getCount() = 3
}