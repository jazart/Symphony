package com.jazart.symphony.signup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.jazart.symphony.MainActivity;
import com.jazart.symphony.R;
import com.jazart.symphony.model.User;

import static com.jazart.symphony.MainActivity.RC_SIGN_IN;
import static com.jazart.symphony.MainActivity.sDb;

/**
 * This fragment class displays the sign-in/up screen for the user. Once the user has been authenticated
 * they are taken back to the main activity to use the app.
 */

public class SignupFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "SignupFragment" ;
    public static final int RC_SIGN_UP = 1;
    private static final int RC_TERMS = 5;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mSignInClient;
    private String mEmail;
    private String mPassword;
    private Uri mPhoto;
    private String mName;
    private FragmentManager mFragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        mFragmentManager = getFragmentManager();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_fragment, container, false);

        //setting on click listener for buttons
        view.findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        view.findViewById(R.id.goog_sign_up_button).setOnClickListener(this);
        //view.findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        // view.findViewById(R.id.email_sign_in_button2).setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        TermsFragment termsFragment = new TermsFragment();
        termsFragment.setTargetFragment(this, RC_TERMS);
        termsFragment.show(mFragmentManager, null);

    }

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
                //signInEmail();
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
        SignUpDialog signUpDialog = new SignUpDialog();
        signUpDialog.setTargetFragment(this, RC_SIGN_UP);
        signUpDialog.show(mFragmentManager, SignUpDialog.TAG);
    }

    //TODO Fix me
//    private void signInEmail() {
//        mAuth.signInWithEmailAndPassword(new String(), new String())
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        //user signed in;
//                        if(task.isSuccessful()) {
//                            mUser = mAuth.getCurrentUser();
//                            getActivity().finish();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getContext(), "Issue signing in:" + e.getMessage(), Toast.LENGTH_LONG)
//                        .show();
//            }
//        });
//    }
//
//    private void signUpEmail() {
//        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()) {
//                            Toast.makeText(getActivity(), "Sign-Up Complete!", Toast.LENGTH_SHORT)
//                                    .show();
//                            //new user successfully added
//                            mUser = mAuth.getCurrentUser();
//                            mUser.sendEmailVerification();
//                            setUpUser(mUser, mName, mPhoto);
//                        } else {
//                            Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                    }
//                });
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_TERMS && resultCode != Activity.RESULT_OK) {
            Toast.makeText(getContext(), "You cannot use the app without agreeing to terms",
                    Toast.LENGTH_LONG)
                    .show();
            requireActivity().finish();
        }
        if(requestCode == RC_SIGN_IN && data != null) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult();
                firebaseGoogAuth(account);
            }

        }

//        if(requestCode == RC_SIGN_UP) {
//            mEmail = data.getStringExtra(SignUpDialog.EXTRA_EMAIL);
//            mPassword = data.getStringExtra(SignUpDialog.EXTRA_PASSWORD);
//            mPhoto = data.getData();
//            mName = data.getStringExtra(SignUpDialog.EXTRA_NAME);
//            signUpEmail();
//        }
    }

    private void firebaseGoogAuth(GoogleSignInAccount acct) {
        if(!isAdded()) {
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            addToDb();
                            startActivity(new Intent(requireContext(), MainActivity.class));
                            //   updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (getView() != null) {
                                Snackbar.make(getView().findViewById(R.id.activ_main_root), "Authentication Failed.", Snackbar.LENGTH_SHORT)
                                        .show();
                            }

                        }
                    }
                });
    }

    private void addToDb() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        sDb.collection("users").document(user.getUid())
                .set(new User(user));
    }

    private void setUpUser(FirebaseUser user, String name, Uri photoUrl) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photoUrl)
                .build();
        user.updateProfile(changeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            addToDb();
                            Toast.makeText(getContext(), "Successful Sign-Up!", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        requireActivity().finish();
                    }
                });
    }
}
