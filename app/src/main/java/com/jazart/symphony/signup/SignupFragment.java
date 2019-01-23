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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.jazart.symphony.MainActivity;
import com.jazart.symphony.R;
import com.jazart.symphony.model.User;


/**
 *
 * This fragment class displays the sign-in/up screen for the user. Once the user has been authenticated
 * they are taken back to the main activity to use the app.
*/

public class SignupFragment extends Fragment {
    private static final String TAG = "SignupFragment" ;
    public static final int RC_SIGN_UP = 1;
    private static final int RC_TERMS = 5;
    private FirebaseAuth auth;
    private GoogleSignInClient signInClient;
    private FragmentManager mFragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(requireActivity(), gso);
        mFragmentManager = getFragmentManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_fragment, container, false);

        view.findViewById(R.id.google_sign_in_button).setOnClickListener((v) -> signIn());
        view.findViewById(R.id.goog_sign_up_button).setOnClickListener((v) -> signIn());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        TermsFragment termsFragment = new TermsFragment();
        termsFragment.setTargetFragment(this, RC_TERMS);
        termsFragment.show(mFragmentManager, null);
    }

    private void signIn() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, MainActivity.getRC_SIGN_IN());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_TERMS && resultCode != Activity.RESULT_OK) {
            Toast.makeText(getContext(), "You cannot use the app without agreeing to terms",
                    Toast.LENGTH_LONG)
                    .show();
            requireActivity().finish();
        }
        if(requestCode == MainActivity.getRC_SIGN_IN() && data != null) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult();
                firebaseGoogAuth(account);
            }
        }
    }

    private void firebaseGoogAuth(GoogleSignInAccount acct) {
        if(!isAdded()) {
            return;
        }
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        addToDb();
                        startActivity(new Intent(requireContext(), MainActivity.class));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (getView() != null) {
                            Snackbar.make(getView().findViewById(R.id.activ_main_root), "Authentication Failed.", Snackbar.LENGTH_SHORT)
                                    .show();
                        }

                    }
                });
    }

    private void addToDb() {
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        MainActivity.getSDb().collection("users").document(user.getUid())
                .set(new User(user));
    }

    private void setUpUser(FirebaseUser user, String name, Uri photoUrl) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photoUrl)
                .build();
        user.updateProfile(changeRequest)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        addToDb();
                        Toast.makeText(getContext(), "Successful Sign-Up!", Toast.LENGTH_SHORT)
                                .show();
                    }
                    requireActivity().finish();
                });
    }
}