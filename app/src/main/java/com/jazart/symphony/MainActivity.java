package com.jazart.symphony;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jazart.symphony.com.featured.FeaturedMusicFragment;
import com.jazart.symphony.posts.MyMusicFragment;
import com.jazart.symphony.posts.UploadDialog;
import com.jazart.symphony.posts.UserPost;
import com.jazart.symphony.signup.SignUpActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NewPostFragment.Post, UploadDialog.SongPost {

    public static final FirebaseFirestore sDb = FirebaseFirestore.getInstance();
    public static final int RC_SIGN_IN = 0;
    public static final int UPLOAD_MP3 = 2;
    public static final String TAG = "MainActivity";
    private static final int URI_REQUEST = 1;
    protected Uri mURI;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DocumentReference mDocRef;
    private Handler mainHandler;
    private User mCurrentUser;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FeaturedMusicFragment featuredFragment = new FeaturedMusicFragment();
                    mFragmentManager.beginTransaction()
                            .replace(R.id.frag_container, featuredFragment)
                            .commit();

                    return true;
                case R.id.nav_my_music:
                    //goto music page
                    MyMusicFragment myMusicFragment = new MyMusicFragment();
                    mFragmentManager.beginTransaction()
                            .replace(R.id.frag_container, myMusicFragment)
                            .commit();
                    return true;
                case R.id.nav_events:
                    //goto events page
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        findViewById(R.id.fab_menu).bringToFront();
        findViewById(R.id.fab_upload).setOnClickListener(this);
        findViewById(R.id.fab_new_post).setOnClickListener(this);
        mFragmentManager = getSupportFragmentManager();
        //mFragmentManager.
        mFragmentManager.beginTransaction().replace(R.id.frag_container, new FeaturedMusicFragment())
                .commitAllowingStateLoss();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
            mURI = data.getData();
            UploadDialog uploadDialogFragment = UploadDialog.newInstance(mURI);
            uploadDialogFragment.show(mFragmentManager, UploadDialog.TAG);


        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_upload:
                setURI();
                break;
//            case R.id.fab_gototop:
//                //scroll to top
//                break;
//            case R.id.fab_search:
//                //search
//                break;
            case R.id.fab_new_post:
                NewPostFragment fragment = new NewPostFragment();
                mFragmentManager.beginTransaction()
                        .replace(R.id.frag_container, fragment)
                        .addToBackStack("New Post")
                        .commit();
                //new post
                break;
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
    public void onPost(Song song)  {
        sDb.collection("songs").add(song);
        Uri songPath = Uri.parse(song.getURI());
       // Uri songFile = Uri.fromFile(new File(path));
        //InputStream fileInputStream;
        try {
            //fileInputStream = openFileInput(song.getName());
            ContentResolver songResolver = getContentResolver();

            InputStream songStream = songResolver.openInputStream(songPath);
            songResolver.getType(songPath);

        //Log.d("DEBUG",songFile.toString());
        StorageReference store = FirebaseStorage.getInstance().getReference();
        //Log.d("DEBUG",store.toString());
        //StorageReference songRef = store.child("songs");
        //Lo
        StorageReference songRef = store.child("songs/" + song.getName());
        UploadTask songTask = songRef.putStream(songStream);


        songTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Toast.makeText(getApplicationContext(), "Successful Upload", Toast.LENGTH_SHORT).show();

            }
        });
        songTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Unsuccessful Upload", Toast.LENGTH_SHORT).show();
            }
        });
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onUserPost(@NonNull UserPost post) {
        post.setAuthor(mUser.getUid());
        post.setProfilePic(mUser.getPhotoUrl().toString());
        sDb.collection("posts")
                .add(post);
        Toast.makeText(this, "Post Created!", Toast.LENGTH_SHORT)
                .show();
    }
}
