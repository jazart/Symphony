package com.jazart.symphony;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.jazart.symphony.featured.FeaturedMusicFragment;
import com.jazart.symphony.location.LocationIntentService;
import com.jazart.symphony.model.Song;
import com.jazart.symphony.posts.MyMusicFragment;
import com.jazart.symphony.posts.PostActivity;
import com.jazart.symphony.posts.UploadDialog;
import com.jazart.symphony.posts.UserPost;
import com.jazart.symphony.signup.SignUpActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jazart.symphony.Constants.POSTS;
import static com.jazart.symphony.Constants.SONGS;
import static com.jazart.symphony.Constants.USERS;
import static com.jazart.symphony.posts.PostActivity.EXTRA_POST;
import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity implements UploadDialog.SongPost {

    public static final FirebaseFirestore sDb = FirebaseFirestore.getInstance();
    public static final int RC_SIGN_IN = 0;
    public static final int UPLOAD_MP3 = 2;
    public static final String TAG = "MainActivity";
    private static final int URI_REQUEST = 1;
    public static final int RC_NEW_POST = 3;
    public static final int RC_LOCATION = 100;
    public static final String EXTRA_USER = "com.jazart.symphony.EXTRA_USER";

    protected Uri mURI;

    @BindView(R.id.fab_menu)
    FloatingActionMenu mFabMenu;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;
    private FragmentManager mFragmentManager;
//    public static SimpleExoPlayer mPlayer;
//    public static PlayerView playerView;
//    public static long playbackPosition = 0;
//    public static int currentWindow = 0;
//    public static boolean playWhenReady = true;
    public static LinearLayout playerL;
    public static LinearLayout playerCL;

//    public static SimpleExoPlayer exoPlayer;
//    public static Player.EventListener eventListener = new Player.DefaultEventListener() {
//        @Override
//        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
//            super.onTimelineChanged(timeline, manifest, reason);
//        }
//
//        @Override
//        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//            super.onTracksChanged(trackGroups, trackSelections);
//        }
//
//        @Override
//        public void onLoadingChanged(boolean isLoading) {
//            super.onLoadingChanged(isLoading);
//        }
//
//        @Override
//        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            super.onPlayerStateChanged(playWhenReady, playbackState);
//        }
//
//        @Override
//        public void onRepeatModeChanged(int repeatMode) {
//            super.onRepeatModeChanged(repeatMode);
//        }
//
//        @Override
//        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
//            super.onShuffleModeEnabledChanged(shuffleModeEnabled);
//        }
//
//        @Override
//        public void onPlayerError(ExoPlaybackException error) {
//            super.onPlayerError(error);
//        }
//
//        @Override
//        public void onPositionDiscontinuity(int reason) {
//            super.onPositionDiscontinuity(reason);
//        }
//
//        @Override
//        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//            super.onPlaybackParametersChanged(playbackParameters);
//        }
//
//        @Override
//        public void onSeekProcessed() {
//            super.onSeekProcessed();
//        }
//    };
//    public static SeekBar seekPlayerProgress;
//    public static Handler handler;
//    public static ImageButton btnPlay;
//    public static TextView txtCurrentTime, txtEndTime;
//    public static boolean isPlaying = false;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FeaturedMusicFragment featuredFragment = new FeaturedMusicFragment();
                    mFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.frag_container, featuredFragment)
                            .commit();

                    return true;
                case R.id.nav_my_music:
                    //goto music page
                    MyMusicFragment myMusicFragment = new MyMusicFragment();
                    mFragmentManager.beginTransaction()
//                            .addToBackStack(null)
                            .replace(R.id.frag_container, myMusicFragment)
                            .commit();
                    return true;
                case R.id.nav_events:
                    //goto events pagejj
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        playerView = findViewById(R.id.music_view);
        //exoPlayer = ExoPlayerFactory.newInstance(RENDERER_COUNT, minBufferMs, minRebufferMs);
        ButterKnife.bind(this);

//        playerL = (LinearLayout) findViewById(R.id.media_controller);
//        playerCL = (LinearLayout) findViewById(R.id.controls);
//        FrameLayout frag = (FrameLayout) findViewById(R.id.frag_container);
//        frag.setBottom(playerCL.getBottom());
//        playerCL.setVisibility(View.GONE);
//        playerL.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        mFabMenu.bringToFront();

        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction().replace(R.id.frag_container, new FeaturedMusicFragment())
                .commitAllowingStateLoss();

        mNavigation.setSelectedItemId(R.id.navigation_home);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (!checkPermissions()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    RC_LOCATION);
        }

    }

//    public static void initializePlayer(String path, Context context) {
//
//        mPlayer = ExoPlayerFactory.newSimpleInstance(
//                new DefaultRenderersFactory(context),
//                new DefaultTrackSelector(), new DefaultLoadControl());
//        Log.d("DEBUG", mPlayer.toString());
//        playerView.setPlayer(mPlayer);
//        mPlayer.setPlayWhenReady(playWhenReady);
//        mPlayer.seekTo(currentWindow, playbackPosition);
//        Uri uri = Uri.parse(path);
//        MediaSource mediaSource = buildMediaSource(uri);
//        mPlayer.prepare(mediaSource, true, false);
//
//
//    }
//    public static MediaSource buildMediaSource(Uri uri) {
//        return new ExtractorMediaSource.Factory(
//                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
//                createMediaSource(uri);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        //checks to see if the user is signed in or not, if not we send them to SignUpActivity
        if (mUser == null) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        } else {
            startLocationService();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == URI_REQUEST) {
            if (data != null) {
                mURI = data.getData();
                UploadDialog uploadDialogFragment = UploadDialog.newInstance(mURI);
                uploadDialogFragment.show(mFragmentManager, UploadDialog.TAG);
            }
        } else if (requestCode == RC_NEW_POST) {
            Gson gson = new Gson();
            if (data != null) {
                UserPost post = gson.fromJson(data.getStringExtra(EXTRA_POST), UserPost.class);
                addToDb(post);
            }

        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "We need location permissions", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    private void startLocationService() {
        Intent intent = new Intent(this, LocationIntentService.class);
        intent.putExtra(EXTRA_USER, mUser.getUid());
        startService(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    private void setURI() {
        Intent musicIntent = new Intent();
        musicIntent.setAction(Intent.ACTION_GET_CONTENT);
        musicIntent.setType("audio/mpeg");
        startActivityForResult(Intent.createChooser(
                musicIntent, "Open Audio (mp3) file"), URI_REQUEST);
    }

    @Override
    public void onPost(final Song song) {

        Uri songPath = Uri.parse(song.getURI());

        try {
            ContentResolver songResolver = getContentResolver();

            InputStream songStream = songResolver.openInputStream(songPath);
            songResolver.getType(songPath);

            StorageReference store = FirebaseStorage.getInstance().getReference();

            final StorageReference songRef = store.child(USERS +
                    "/" +
                    mUser.getUid() +
                    "/" +
                    SONGS +
                    "/" +
                    song.getName());
            UploadTask songTask = songRef.putStream(songStream);


            songTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(getApplicationContext(), "Successful Upload", Toast.LENGTH_SHORT).show();
                    songRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri downloadUrl)
                        {
                            addSongToDb(downloadUrl, song);
                        }
                    });
                }
            });
            songTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Unsuccessful Upload", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void addSongToDb(Uri downloadUrl, Song song) {
        String link = downloadUrl.toString();
        song.setURI(link);
        song.setAuthor(mUser.getUid());
        sDb.collection(SONGS).add(song);
    }


    public void addToDb(@NonNull UserPost post) {
        if (mUser != null) {
            post.setAuthor(mUser.getUid());
            post.setProfilePic(mUser.getPhotoUrl().toString());
            sDb.collection(POSTS)
                    .add(post);
            Toast.makeText(this, "Post Created!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @OnClick({R.id.fab_upload, R.id.fab_new_post, R.id.fab_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab_upload:
                setURI();
                break;
            case R.id.fab_new_post:
                startActivityForResult(new Intent(MainActivity.this, PostActivity.class), RC_NEW_POST);
                break;
            case R.id.fab_menu:
                break;
        }
    }

//    private static void prepareExoPlayerFromURL(Uri uri, Context context){
//
//        contextP = context;
//        TrackSelector trackSelector = new DefaultTrackSelector();
//
//        LoadControl loadControl = new DefaultLoadControl();
//
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
//
//        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), null);
//        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
//        exoPlayer.addListener(eventListener);
//
//        exoPlayer.prepare(audioSource);
//        initMediaControls();
//    }
//    public static void initMediaControls() {
//        initPlayButton();
//        initSeekBar();
//        initTxtTime();
//    }
//
//    public static void initPlayButton() {
//        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
//        btnPlay.requestFocus();
//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setPlayPause(!isPlaying);
//            }
//        });
//    }
//
//    public static void setPlayPause(boolean play){
//        isPlaying = play;
//        exoPlayer.setPlayWhenReady(play);
//        if(!isPlaying){
//            btnPlay.setImageResource(android.R.drawable.ic_media_play);
//        }else{
//            setProgress();
//            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
//        }
//    }
//
//    public static void initTxtTime() {
//        txtCurrentTime = (TextView) findViewById(R.id.time_current);
//        txtEndTime = (TextView) findViewById(R.id.player_end_time);
//    }
//
//    public static String stringForTime(int timeMs) {
//        StringBuilder mFormatBuilder;
//        Formatter mFormatter;
//        mFormatBuilder = new StringBuilder();
//        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
//        int totalSeconds =  timeMs / 1000;
//
//        int seconds = totalSeconds % 60;
//        int minutes = (totalSeconds / 60) % 60;
//        int hours   = totalSeconds / 3600;
//
//        mFormatBuilder.setLength(0);
//        if (hours > 0) {
//            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
//        } else {
//            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
//        }
//    }
//
//    public static void setProgress() {
//        seekPlayerProgress.setProgress(0);
//        seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
//        txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
//        txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));
//
//        if(handler == null)handler = new Handler();
//        //Make sure you update Seekbar on UI thread
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (exoPlayer != null && isPlaying) {
//                    seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
//                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
//                    seekPlayerProgress.setProgress(mCurrentPosition);
//                    txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
//                    txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));
//
//                    handler.postDelayed(this, 1000);
//                }
//            }
//        });
//    }
//
//    public static void initSeekBar() {
//        seekPlayerProgress = (SeekBar) findViewById(R.id.mediacontroller_progress);
//        seekPlayerProgress.requestFocus();
//
//        seekPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (!fromUser) {
//                    // We're not interested in programmatically generated changes to
//                    // the progress bar's position.
//                    return;
//                }
//
//                exoPlayer.seekTo(progress*1000);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        seekPlayerProgress.setMax(0);
//        seekPlayerProgress.setMax((int) exoPlayer.getDuration()/1000);
//
//    }
}
