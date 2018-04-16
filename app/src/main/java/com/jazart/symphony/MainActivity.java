package com.jazart.symphony;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jazart.symphony.com.jazart.symphony.featured.FeaturedMusicFragment;
import com.jazart.symphony.signup.SignUpActivity;


public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 0;
    public static final String TAG = "MainActivity";
    private android.support.v4.app.FragmentManager mFragmentManager;
    private FirebaseAuth mAuth;
    private Handler mainHandler;
    private FirebaseUser mCurrentUser;


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
        mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser == null) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
