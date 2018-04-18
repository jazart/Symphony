package com.jazart.symphony;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.FragmentManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jazart.symphony.com.featured.FeaturedMusicFragment;
import com.jazart.symphony.signup.SignUpActivity;
import com.jazart.symphony.signup.SignUpDialog;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, UploadDialog.SongPost {

    public static final FirebaseFirestore sDb = FirebaseFirestore.getInstance();
    public static final int RC_SIGN_IN = 0;
    public static final int UPLOAD_MP3 = 2;
    public static final String TAG = "MainActivity";
    private static final int URI_REQUEST = 1;
    protected Uri mURI;
    private FragmentManager mFragmentManager;
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

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.frag_container, new FeaturedMusicFragment())
                .commit();

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
            uploadDialogFragment.show(mFragmentManager, uploadDialogFragment.TAG);

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
    public void onPOst(Song song) {
        sDb.collection("songs").add(song);
    }
}
