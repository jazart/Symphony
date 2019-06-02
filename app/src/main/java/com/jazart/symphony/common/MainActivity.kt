package com.jazart.symphony.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.PorterDuff
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jazart.symphony.MainFlowDirections
import com.jazart.symphony.R
import com.jazart.symphony.di.App
import com.jazart.symphony.location.LocationIntentService
import com.jazart.symphony.playback.PlayerBoolean
import com.jazart.symphony.signup.SignupFragmentDirections
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.player.*
import kotlinx.coroutines.Runnable
import java.util.*
import javax.inject.Inject

/**
 * Main entry point into the application. Here we create a custom view holder to house our fragments
 * which are shown using bottom navigation tabs. Each tab corresponds to a different fragment in the activity.
 * We also use this class to check if a user is signed in and send them to the sign in / sign up activity.
 * Permissions are checked and intents are built out for requesting data.*
 */

class MainActivity : AppCompatActivity() {
    var songPlaying = false
    private val disposables = CompositeDisposable()
    private lateinit var playerSeek: SeekBar
    @Inject
    lateinit var exoPlayer: SimpleExoPlayer
    private val finalTime: Long = 0
    private var mUser: FirebaseUser? = null
    private var txtCurrentTime: TextView? = null
    private var txtEndTime: TextView? = null
    private var handler: Handler? = null
    private var hasSongStarted = false
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
        val mediaController = findViewById<LinearLayout>(R.id.media_controller)
        mediaController.visibility = View.VISIBLE
        playerSeek = findViewById(R.id.mediacontroller_progress)
        setupExoPlayerViews()
        onFabClick()
        controller = Navigation.findNavController(findViewById(R.id.nav_host))
        controller.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.main_flow) {
                updateUi()
                NavigationUI.setupWithNavController(navigation, controller)
                checkPermissions()
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
        media_controller.visibility = View.VISIBLE
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
            controller.navigate(MainFlowDirections.actionToUploadDialog(uri.toString()))
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
        val musicIntent = Intent()
        musicIntent.action = Intent.ACTION_GET_CONTENT
        musicIntent.type = MimeTypes.AUDIO_MPEG
        startActivityForResult(Intent.createChooser(
                musicIntent, "Open Audio (mp3) file"), URI_REQUEST)
    }

    private fun inject() {
        val app = application as App
        app.component.inject(this)
    }

    private fun setupExoPlayerViews() {
        playerCreated.setListener {
            if (playerCreated.isPlayerBool) {
                initTxtTime()
                initSeekBar()
                setProgress()
            }
        }
        btnPlay.setOnClickListener { v ->
            if (songPlaying) {
                exoPlayer.playWhenReady = false
                songPlaying = false
                btnPlay.setThumbResource(android.R.drawable.ic_media_play)
            } else {

                exoPlayer.playWhenReady = true

                initTxtTime()
                if (!hasSongStarted) {
                    initSeekBar()
                }
                setProgress()
                hasSongStarted = true

                songPlaying = true
                btnPlay.setThumbResource(android.R.drawable.ic_media_pause)


            }
        }
    }

    private fun initTxtTime() {
        txtCurrentTime = findViewById(R.id.time_current)
        txtEndTime = findViewById(R.id.player_end_time)
    }

    private fun stringForTime(timeMs: Int): String {
        val mFormatBuilder = StringBuilder()
        val mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
        val totalSeconds = timeMs / 1000

        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600

        mFormatBuilder.setLength(0)
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    private fun setProgress() {
        playerSeek.progressDrawable.setColorFilter(resources.getColor(R.color.colorAccent, theme), PorterDuff.Mode.MULTIPLY)
        playerSeek.progress = 0
        playerSeek.max = exoPlayer.duration.toInt() / 1000
        txtCurrentTime!!.text = stringForTime(exoPlayer.currentPosition.toInt())
        txtEndTime!!.text = stringForTime(exoPlayer.duration.toInt())

        if (handler == null) handler = Handler()
        handler!!.post(object : Runnable {
            override fun run() {
                if (songPlaying) {
                    playerSeek.max = exoPlayer.duration.toInt() / 1000
                    val mCurrentPosition = exoPlayer.currentPosition.toInt() / 1000
                    playerSeek.progress = mCurrentPosition
                    txtCurrentTime!!.text = stringForTime(exoPlayer.currentPosition.toInt())
                    txtEndTime!!.text = stringForTime(exoPlayer.duration.toInt())

                    handler!!.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun initSeekBar() {
        playerSeek = findViewById(R.id.mediacontroller_progress) ?: return
        playerSeek.requestFocus()
        playerSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!fromUser) {

                    return
                }
                exoPlayer.seekTo((progress * 1000).toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
        playerSeek.max = 0
        playerSeek.max = exoPlayer.duration.toInt() / 1000
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
//        request.setInterval(900_000L);
        request.interval = 3000L // Test duration
        providerClient.requestLocationUpdates(request, locationCallback, Looper.myLooper())
    }

    companion object {
        @JvmStatic
        val sDb = FirebaseFirestore.getInstance()

        @JvmStatic
        val RC_SIGN_IN = 0

        @JvmStatic
        private val URI_REQUEST = 1

        @JvmStatic
        private val RC_LOCATION = 100

        @JvmStatic
        val EXTRA_USER = "com.jazart.symphony.EXTRA_USER"

        @JvmStatic
        val playerCreated = PlayerBoolean()

        const val IS_NAV_STATE_SAVED = "saved state?"
    }
}