package com.jazart.symphony;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.jazart.symphony.MainActivity.RC_SIGN_IN;

/*
This fragment class displays the sign-in/up screen for the user. Once the user has been authenticated
they are taken back to the main activity to use the app.
 */

public class SignupFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "SignupFragment" ;
    public static final int RC_SIGN_UP = 1;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mSignInClient;
    private String mEmail;
    private String mPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getting references to auth apis and setting up google sign in client.
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(getActivity(), gso);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_fragment, container, false);

        //setting on click listener for buttons
        view.findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        view.findViewById(R.id.goog_sign_up_button).setOnClickListener(this);
        view.findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        view.findViewById(R.id.email_sign_in_button2).setOnClickListener(this);
        return view;
    }


    //Implementing the logic for deciding what to do on each button click.
    // TODO: 4/15/2018 Implement interface for the user to enter email/password and pass it to email auth methods
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
            case R.id.goog_sign_up_button:
                signIn();
                break;
            case R.id.email_sign_in_button:
                signInEmail();
                break;
            case R.id.email_sign_in_button2:
                showSignInDialog();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void showSignInDialog() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        SignUpDialog signUpDialog = new SignUpDialog();
        signUpDialog.setTargetFragment(this, RC_SIGN_UP);
        signUpDialog.show(fragmentManager, SignUpDialog.TAG);
    }

    /*
    method for signing the user in with email/password
    generic string arguments used. These will be the email/password values the user inputs
     */
    private void signInEmail() {
        mAuth.signInWithEmailAndPassword(new String(), new String())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //user signed in;
                        getActivity().finish();
                    }
                });
    }

    private void signUpEmail() {
        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Sign-Up Complete!", Toast.LENGTH_SHORT)
                                    .show();
                            //new user successfully added
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "Authencation Failed", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN && data != null) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult();
            firebaseGoogAuth(account);

        }

        if(requestCode == RC_SIGN_UP) {
            mEmail = data.getStringExtra(SignUpDialog.EXTRA_EMAIL);
            mPassword = data.getStringExtra(SignUpDialog.EXTRA_PASSWORD);
            signUpEmail();
        }
    }

    private void firebaseGoogAuth(GoogleSignInAccount acct) {
        if(!isAdded()) {
            return;
        }
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //   showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            getActivity().finish();
                            //   updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(getView().findViewById(R.id.activ_main_root), "Authentication Failed.", Snackbar.LENGTH_SHORT)
                                    .show();

                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
}
