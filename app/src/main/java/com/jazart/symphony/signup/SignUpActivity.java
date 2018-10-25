package com.jazart.symphony.signup;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

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
