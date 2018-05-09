package com.jazart.symphony.signup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.jazart.symphony.R;

public class SignUpActivity extends AppCompatActivity {

    /**
     * Activity to house the fragment for signing the user in
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SignupFragment signupFragment = new SignupFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.sign_up_container, signupFragment)
                .commit();

    }


}
