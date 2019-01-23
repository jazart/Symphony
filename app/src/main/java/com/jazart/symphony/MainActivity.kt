package com.jazart.symphony

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

import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity

import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import com.github.clans.fab.FloatingActionMenu
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jazart.symphony.di.App
import com.jazart.symphony.location.LocationIntentService
import com.jazart.symphony.playback.PlayerBoolean
import com.jazart.symphony.posts.PostActivity
import com.jazart.symphony.featured.UploadDialog
import com.jazart.symphony.signup.SignUpActivity
import com.tbruyelle.rxpermissions2.RxPermissions

import java.util.Formatter
import java.util.Locale
import java.util.Objects

import javax.inject.Inject

import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Main entry point into the application. Here we create a custom view holder to house our fragments
 * which are shown using bottom navigation tabs. Each tab corresponds to a different fragment in the activity.
 * We also use this class to check if a user is signed in and send them to the sign in / sign up activity.
 * Permissions are checked and intents are built out for requesting data.
 *
 *
 * TODO: Refactor this class into a more passive view and break into manager classes to distribute it's responsibilities.
 */

class MainActivity : AppCompatActivity(), CoroutineScope {
    var songPlaying = false
    private val disposables = CompositeDisposable()


    val job = Job() + Dispatchers.Main
    override val coroutineContext: CoroutineContext
        get() = job

    private var playerSeek: SeekBar? = null
    private val mFragmentManager: FragmentManager? = null

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer
    private val finalTime: Long = 0
    private var mUser: FirebaseUser? = null
    private var txtCurrentTime: TextView? = null
    private var txtEndTime: TextView? = null
    private var handler: Handler? = null
    private var hasSongStarted = false
    private lateinit var locationCallback: LocationCallback
    private lateinit var providerClient: FusedLocationProviderClient
    private val permissions: RxPermissions = RxPermissions(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        providerClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(R.layout.activity_main)
        buildLocationCallback()
        onFabClick()
        volumeControlStream = AudioManager.STREAM_MUSIC
        inject()
        val auth = FirebaseAuth.getInstance()
        mUser = auth.currentUser
        val mediaController = findViewById<LinearLayout>(R.id.media_controller)
        mediaController.visibility = View.VISIBLE
        playerSeek = findViewById(R.id.mediacontroller_progress)
        setupExoPlayerViews()
        val controller = Navigation.findNavController(findViewById(R.id.nav_host))
        NavigationUI.setupWithNavController(navigation, controller)
        createNotificationChannel()
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                val intent = Intent(this@MainActivity, LocationIntentService::class.java)
                intent.putExtra(EXTRA_USER, mUser!!.uid)
                intent.putExtra("loc", locationResult!!.lastLocation)
                LocationIntentService.enqueueWork(this@MainActivity, intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (mUser == null) {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
        job.cancel()
        providerClient.removeLocationUpdates(locationCallback)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == URI_REQUEST) {
            if (data != null) {
                val URI = data.data
                val uploadDialogFragment = UploadDialog.newInstance(Objects.requireNonNull(URI))
                uploadDialogFragment.show(mFragmentManager!!, UploadDialog.TAG)
            }
        }
    }

    private fun checkPermissions() {
        disposables.add(permissions.requestEach(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { permission ->
                    if (permission.granted) {
                        startLocationUpdates()
                    } else {
                        Toast.makeText(this, R.string.location_permission_req, Toast.LENGTH_SHORT).show()
                    }
                })
    }


    fun onFabClick() {
        fab_upload.setOnClickListener { setURI() }
        fab_new_post.setOnClickListener { startActivity(Intent(this@MainActivity, PostActivity::class.java)) }
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
                exoPlayer!!.playWhenReady = false
                songPlaying = false
                btnPlay.setImageResource(android.R.drawable.ic_media_play)
            } else {

                exoPlayer!!.playWhenReady = true

                initTxtTime()
                if (!hasSongStarted) {
                    initSeekBar()
                }
                setProgress()
                hasSongStarted = true

                songPlaying = true
                btnPlay.setImageResource(android.R.drawable.ic_media_pause)


            }
        }
    }

    private fun initTxtTime() {
        txtCurrentTime = findViewById(R.id.time_current)
        txtEndTime = findViewById(R.id.player_end_time)
    }

    private fun stringForTime(timeMs: Int): String {
        val mFormatBuilder: StringBuilder
        val mFormatter: Formatter
        mFormatBuilder = StringBuilder()
        mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
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
        playerSeek!!.progressDrawable.setColorFilter(resources.getColor(R.color.colorAccent, theme), PorterDuff.Mode.MULTIPLY)
        playerSeek!!.progress = 0
        playerSeek!!.max = exoPlayer!!.duration.toInt() / 1000
        txtCurrentTime!!.text = stringForTime(exoPlayer!!.currentPosition.toInt())
        txtEndTime!!.text = stringForTime(exoPlayer!!.duration.toInt())

        if (handler == null) handler = Handler()
        handler!!.post(object : Runnable {
            override fun run() {
                if (exoPlayer != null && songPlaying) {
                    playerSeek!!.max = exoPlayer!!.duration.toInt() / 1000
                    val mCurrentPosition = exoPlayer!!.currentPosition.toInt() / 1000
                    playerSeek!!.progress = mCurrentPosition
                    txtCurrentTime!!.text = stringForTime(exoPlayer!!.currentPosition.toInt())
                    txtEndTime!!.text = stringForTime(exoPlayer!!.duration.toInt())

                    handler!!.postDelayed(this, 1000)
                }
            }
        })
    }


    private fun initSeekBar() {
        playerSeek = findViewById(R.id.mediacontroller_progress)
        playerSeek!!.requestFocus()

        playerSeek!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!fromUser) {

                    return
                }

                exoPlayer!!.seekTo((progress * 1000).toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        playerSeek!!.max = 0
        playerSeek!!.max = exoPlayer!!.duration.toInt() / 1000
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
    }
}

