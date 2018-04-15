package com.jazart.symphony;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import static com.jazart.symphony.SignupFragment.RC_SIGN_UP;

public class SignUpDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String TAG = "SignUpDialog";
    public static final String EXTRA_EMAIL = "com.jazart.symphony.extra_email";
    public static final String EXTRA_PASSWORD = "com.jazart.symphony.extra_password";

    private View mView;

    private void sendResult(int resultCode, String email, String password) {
        if(getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_PASSWORD, password);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getContext())
                .inflate(R.layout.sign_up_dialog, null);
        return new AlertDialog.Builder(getContext())
                .setView(mView)
                .setTitle("Sign-up")
                .setPositiveButton(android.R.string.ok, this)
                .create();
    }

/*
Implementing on click for dialog buttons. Validates email and password
then sends result back to to the fragment to sign up via firebase
 */
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        TextInputLayout emailLayout = mView.findViewById(R.id.sign_up_email);
        String email = emailLayout.getEditText().getText().toString();

        TextInputLayout passwordLayout = mView.findViewById(R.id.sign_up_password);
        String password = passwordLayout.getEditText().getText().toString();
        if(isValidEmail(email) && isValidPassword(password)) {
            sendResult(RC_SIGN_UP, email, password);
        }
    }

    private boolean isValidEmail(CharSequence target) {
            return !TextUtils.isEmpty(target) &&
                    android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isValidPassword(CharSequence target) {
        return true;
    }

}
