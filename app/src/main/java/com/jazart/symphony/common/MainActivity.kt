package com.jazart.symphony.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jazart.symphony.MainFlowDirections
import com.jazart.symphony.R
import com.jazart.symphony.di.App
import com.jazart.symphony.location.LocationIntentService
import com.jazart.symphony.signup.SignupFragmentDirections
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main entry point into the application. Here we create a custom view holder to house our fragments
 * which are shown using bottom navigation tabs. Each tab corresponds to a different fragment in the activity.
 * We also use this class to check if a user is signed in and send them to the sign in / sign up activity.
 * Permissions are checked and intents are built out for requesting data.*
 */

class MainActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()
    private var mUser: FirebaseUser? = null
    private val locationCallback: LocationCallback = buildLocationCallback()
    private lateinit var providerClient: FusedLocationProviderClient
    private val permissions: RxPermissions = RxPermissions(this)
    private lateinit var controller: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        providerClient = LocationServices.getFusedLocationProviderClient(this)
        volumeControlStream = AudioManager.STREAM_MUSIC
        mUser = FirebaseAuth.getInstance().currentUser
        inject()
        createNotificationChannel()
        setupUi(savedInstanceState?.getBoolean(IS_NAV_STATE_SAVED) ?: false)
    }

    private fun setupUi(isNavStateSaved: Boolean) {
        onFabClick()
        controller = Navigation.findNavController(findViewById(R.id.nav_host))
        controller.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.main_flow) {
                updateUi()
                NavigationUI.setupWithNavController(navigation, controller)
                checkPermissions()
            }
            if(destination.id in listOf(R.id.profileFragment, R.id.newPostFragment, R.id.postDetailFragment)) {
                navigation.visibility = View.GONE
            } else {
                navigation.visibility = View.VISIBLE
            }
        }
        if (mUser == null) {
            controller.navigate(R.id.signupFragment)
        } else {
            NavigationUI.setupWithNavController(navigation, controller)
            updateUi()
            if (!isNavStateSaved) controller.navigate(SignupFragmentDirections.actionSignupFragmentToFeaturedMusicFragment())
        }
    }

    private fun updateUi() {
        navigation.visibility = View.VISIBLE
        fabMenu.visibility = View.VISIBLE
    }

    private fun buildLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                val intent = Intent(this@MainActivity, LocationIntentService::class.java)
                intent.putExtra(EXTRA_USER, mUser?.uid)
                intent.putExtra("loc", locationResult?.lastLocation)
                LocationIntentService.enqueueWork(this@MainActivity, intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    override fun onPause() {
        super.onPause()
        providerClient.removeLocationUpdates(locationCallback)
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == URI_REQUEST && data != null) {
            val uri = data.data ?: return
            controller.navigate(MainFlowDirections.actionToUploadDialog(uri))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_NAV_STATE_SAVED, true)
        super.onSaveInstanceState(outState)
    }

    private fun checkPermissions() {
        disposables.add(permissions.requestEach(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { permission ->
                    if (permission.granted && mUser != null) {
                        startLocationUpdates()
                    } else {
                        Toast.makeText(this, R.string.location_permission_req, Toast.LENGTH_SHORT).show()
                    }
                }
        )
    }

    private fun onFabClick() {
        fabMenu.inflate(R.menu.fab_menu)
        fabMenu.setOnActionSelectedListener { item ->
            when (item.id) {
                R.id.new_post -> controller.navigate(MainFlowDirections.actionToNewPostFragment())
                R.id.upload -> setURI()
                else -> return@setOnActionSelectedListener false
            }
            return@setOnActionSelectedListener false
        }
    }

    private fun setURI() {
        val musicIntent = Intent(Intent.ACTION_GET_CONTENT)
        musicIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        musicIntent.action = Intent.ACTION_GET_CONTENT
        musicIntent.type = "audio/mpeg"
        startActivityForResult(Intent.createChooser(
                musicIntent, "Open Audio (mp3) file"), URI_REQUEST)
    }

    private fun inject() {
        val app = application as App
        app.component.inject(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val request = LocationRequest()
        request.interval = 600_000L
        providerClient.requestLocationUpdates(request, locationCallback, Looper.myLooper())
    }

    companion object {
        @JvmStatic val RC_SIGN_IN = 0
        @JvmStatic private val URI_REQUEST = 1
        @JvmStatic val EXTRA_USER = "com.jazart.symphony.EXTRA_USER"
        private const val IS_NAV_STATE_SAVED = "saved state?"
    }
}
