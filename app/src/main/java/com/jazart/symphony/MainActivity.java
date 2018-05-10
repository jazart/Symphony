package com.jazart.symphony;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.jazart.symphony.featured.FeaturedMusicFragment;
import com.jazart.symphony.location.LocationIntentService;
import com.jazart.symphony.model.Song;
import com.jazart.symphony.posts.PostActivity;
import com.jazart.symphony.posts.PostsFragment;
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
import static com.jazart.symphony.featured.MusicAdapter.exoPlayer;
import static com.jazart.symphony.posts.PostActivity.EXTRA_POST;

/**
 * Main entry point into the application. Here we create a custom view holder to house our fragments
 * which are shown using bottom navigation tabs. Each tab corresponds to a different fragement in the activity.
 * We also use this class to check if a user is signed in and send them to the sign in / sign up activity.
 * Permissions are checked and intents are built out for requesting data.
 * <p>
 * Biggest issue is this class does way too much.
 * TODO: Refactor this class into a more passive view and break into manager classes to distribute it's responsibilities.
 */

public class MainActivity extends AppCompatActivity implements UploadDialog.SongPost{

    public static final FirebaseFirestore sDb = FirebaseFirestore.getInstance();
    public static final int RC_SIGN_IN = 0;
    public static final int UPLOAD_MP3 = 2;
    public static final String TAG = "MainActivity";
    private static final int URI_REQUEST = 1;
    public static final int RC_NEW_POST = 3;
    public static final int RC_LOCATION = 100;
    public static final String EXTRA_USER = "com.jazart.symphony.EXTRA_USER";
    public static  SimpleExoPlayer exoPlayerC;
    public static boolean songPlaying = false;

    @BindView(R.id.navigation)

    BottomNavigationView mNavigation;
    @BindView(R.id.fab_menu)
    FloatingActionMenu mFabMenu;

    public static PlayerBoolean playerCreated = new PlayerBoolean();
    private boolean songAlreadystarted = false;
    private long finalTime;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    public  TextView txtCurrentTime, txtEndTime;
    public static LinearLayout playerView;
    public static SeekBar playerSeek;
    private Handler handler;
    @BindView(R.id.btnPlay)
    public ImageButton playB;
    @BindView(R.id.frag_pager)
    BottomNavViewPager mNavViewPager;
    public static FragmentManager mFragmentManager;
    private Uri mURI;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (mFragmentManager.getBackStackEntryCount() > 0) {
                mFragmentManager.popBackStack();
            }
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    mNavViewPager.setCurrentItem(0);

                    return true;
                case R.id.nav_my_music:

                    mNavViewPager.setCurrentItem(1);
                    return true;
                case R.id.nav_events:

                    mNavViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        playerView = findViewById(R.id.media_controller);
        playerView.setVisibility(View.GONE);

        playerSeek = findViewById(R.id.mediacontroller_progress);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFragmentManager = getSupportFragmentManager();

        mFabMenu.bringToFront();
        playB.setImageResource(android.R.drawable.ic_media_pause);


        mNavigation.setSelectedItemId(R.id.navigation_home);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (!checkPermissions()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    RC_LOCATION);
        }
        playerCreated.setListener(new PlayerBoolean.ChangeListener() {
            @Override
            public void onChange() {
                if(playerCreated.isPlayerBool() == true) {
                    initTxtTime();
                    initSeekBar();
                    setProgress();
                }
            }
        });
        playB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v) {
                if (songPlaying == true){
                    exoPlayer.setPlayWhenReady(false);
                    songPlaying = false;
                    playB.setImageResource(android.R.drawable.ic_media_play);
                } else{

                    exoPlayer.setPlayWhenReady(true);

                    finalTime = exoPlayer.getDuration();
                    initTxtTime();
                    if(songAlreadystarted == false) {
                        initSeekBar();
                    }
                    setProgress();
                    songAlreadystarted = true;

                    songPlaying = true;
                    playB.setImageResource(android.R.drawable.ic_media_pause);


                }
            }
        });


    }

    private void initTxtTime() {
        txtCurrentTime = findViewById(R.id.time_current);
        txtEndTime = findViewById(R.id.player_end_time);
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void setProgress() {
        playerSeek.setProgress(0);
        playerSeek.setMax((int) exoPlayer.getDuration()/1000);
        txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
        txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));

        if(handler == null)handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && songPlaying) {
                    playerSeek.setMax((int) exoPlayer.getDuration()/1000);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    playerSeek.setProgress(mCurrentPosition);
                    txtCurrentTime.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
                    txtEndTime.setText(stringForTime((int)exoPlayer.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }


    private void initSeekBar() {
        playerSeek = findViewById(R.id.mediacontroller_progress);
        playerSeek.requestFocus();

        playerSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {

                    return;
                }

                exoPlayer.seekTo(progress*1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playerSeek.setMax(0);
        playerSeek.setMax((int) exoPlayer.getDuration()/1000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //checks to see if the user is signed in or not, if not we send them to SignUpActivity
        if (mUser == null) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        } else {
            //startLocationService();
            BottomNavAdapter adapter = new BottomNavAdapter(mFragmentManager);
            adapter.addFragment(new FeaturedMusicFragment());
            adapter.addFragment(new PostsFragment());
            adapter.addFragment(new LocalEventsFragment());
            mNavViewPager.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationService();
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
                    songRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
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
            try {
                sDb.collection(POSTS)
                        .add(post).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(MainActivity.this, "Post Created!", Toast.LENGTH_SHORT)
                                .show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Post Failed, have you verified your email?", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Post Failed, have you verified your email?", Toast.LENGTH_SHORT)
                        .show();
            }

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
}
