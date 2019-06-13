package com.jazart.symphony.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.jazart.symphony.R
import com.jazart.symphony.di.SimpleViewModelFactory
import com.jazart.symphony.di.app
import com.jazart.symphony.posts.PostPage
import com.jazart.symphony.posts.PostsFragment
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_header_view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ProfileFragment : Fragment() {
    val data = arguments?.let { ProfileFragmentArgs.fromBundle(it).user }
    @Inject
    lateinit var factory: SimpleViewModelFactory
    private val profileViewModel by viewModels<ProfileViewModel> {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ProfileAdapter(childFragmentManager)
        profileToolbar.inflateMenu(R.menu.profile_menu)
        profileToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp, null)
        profileToolbar.setNavigationContentDescription(R.string.back_action)
        profileToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        profileViewPager.adapter = adapter
        profileTabLayout.setupWithViewPager(profileViewPager)
        loadProfilePic(FirebaseAuth.getInstance().currentUser?.photoUrl)
        bindProfileInfo()
    }

    private fun loadProfilePic(uri: Uri?) {
        Glide.with(this)
                .load(uri)
                .apply(RequestOptions().circleCrop().placeholder(resources.getDrawable(R.drawable.ic_account_circle_black_24dp, null)))
                .into(profilePicture)
    }

    private fun bindProfileInfo() {
        profileViewModel.userLiveData.observe(viewLifecycleOwner, Observer { user ->
            joinDate.text = getString(R.string.member_since, SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(user.dateJoined))
            username.text = user.name
            likes.text = getString(R.string.likes_count, 0)
            songCount.text = getString(R.string.song_count, 0)
            joinDate.visibility = View.VISIBLE
            username.visibility = View.VISIBLE
            songCount.visibility = View.VISIBLE
            likes.visibility = View.VISIBLE
        })


    }

    private fun inject() {
        app().component.inject(this)
    }
}

private class ProfileAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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
                1 -> PostsFragment.newInstance(PostPage.PRIVATE)
                2 -> UserFriendsFragment()
                else -> throw IllegalArgumentException()
            }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun getCount() = 3
}