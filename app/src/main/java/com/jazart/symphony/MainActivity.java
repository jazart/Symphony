package com.jazart.symphony;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
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
import com.jazart.symphony.model.Song;
import com.jazart.symphony.posts.MyMusicFragment;
import com.jazart.symphony.posts.PostActivity;
import com.jazart.symphony.posts.UploadDialog;
import com.jazart.symphony.posts.UserPost;
import com.jazart.symphony.signup.SignUpActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jazart.symphony.Constants.POSTS;
import static com.jazart.symphony.Constants.SONGS;
import static com.jazart.symphony.Constants.USERS;
import static com.jazart.symphony.posts.PostActivity.EXTRA_POST;


public class MainActivity extends AppCompatActivity implements UploadDialog.SongPost {

    public static final FirebaseFirestore sDb = FirebaseFirestore.getInstance();
    public static final int RC_SIGN_IN = 0;
    public static final int UPLOAD_MP3 = 2;
    public static final String TAG = "MainActivity";
    private static final int URI_REQUEST = 1;
    public static final int RC_NEW_POST = 3;
    public static final int RC_LOCATION = 100;

    protected Uri mURI;

    @BindView(R.id.fab_menu)
    FloatingActionMenu mFabMenu;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;
    private FragmentManager mFragmentManager;
    private static SimpleExoPlayer mPlayer;
    private PlayerView playerView;
    private long playbackPosition = 0;
    private int currentWindow = 0;
    private boolean playWhenReady = true;



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
        //exoPlayer = ExoPlayerFactory.newInstance(RENDERER_COUNT, minBufferMs, minRebufferMs);
        ButterKnife.bind(this);
        playerView = findViewById(R.id.video_view);

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

    private void initializePlayer(String path) {
        mPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());
        playerView.setPlayer(mPlayer);
        mPlayer.setPlayWhenReady(playWhenReady);
        mPlayer.seekTo(currentWindow, playbackPosition);
        Uri uri = Uri.parse(path);
        MediaSource mediaSource = buildMediaSource(uri);
        mPlayer.prepare(mediaSource, true, false);


    }
    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //checks to see if the user is signed in or not, if not we send them to SignUpActivity
        if (mUser == null) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
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
                }
            }
        }
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
}
