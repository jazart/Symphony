package com.jazart.symphony.signup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.jazart.symphony.R;

public class SignUpDialog extends DialogFragment implements DialogInterface.OnClickListener, View.OnClickListener, TextWatcher {

    public static final String TAG = "SignUpDialog";
    private static final String EXTRA_EMAIL = "com.jazart.symphony.extra_email";
    private static final String EXTRA_PASSWORD = "com.jazart.symphony.extra_password";
    private static final String EXTRA_PHOTO = "com.jazart.symphony.extra_photo";
    private static final String EXTRA_NAME = "com.jazart.symphony.extra_name";
    private static final int MIN_PASS_LENGTH = 8;

    private View mView;
    private Uri selectedImg;
    private TextInputLayout mEmailLayout;
    private TextInputLayout mNameLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputLayout mVerifyPassLayout;
    private TextInputEditText mEmailEt;
    private TextInputEditText mNameEt;
    private TextInputEditText mPassEt;
    private TextInputEditText mVerifyPassEt;


    private void sendResult(String email, String password, String name) {
        if(getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_PASSWORD, password);
        intent.putExtra(EXTRA_PHOTO, selectedImg);
        intent.putExtra(EXTRA_NAME, name);
        getTargetFragment().onActivityResult(getTargetRequestCode(), SignupFragment.RC_SIGN_UP, intent);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getContext())
                .inflate(R.layout.sign_up_dialog, null);

        mEmailLayout = mView.findViewById(R.id.sign_up_email);
        mPasswordLayout = mView.findViewById(R.id.sign_up_password);
        mVerifyPassLayout = mView.findViewById(R.id.sign_up_reenter_password);
        mNameLayout = mView.findViewById(R.id.sign_up_name_til);
        mVerifyPassEt = (TextInputEditText) mVerifyPassLayout.getEditText();
        assert mVerifyPassEt != null;
        mVerifyPassEt.addTextChangedListener(this);
        mPassEt = (TextInputEditText) mPasswordLayout.getEditText();


        mView.findViewById(R.id.sign_up_photo).setOnClickListener(this);
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
        String email = mEmailLayout.getEditText().getText().toString();


        String password = mPasswordLayout.getEditText().getText().toString();
        if (isValidPassword(password) && mNameLayout.getEditText().getText() != null) {
            String name = mNameLayout.getEditText().getText().toString();
            sendResult(email, password, name);
        }

    }

    private boolean isValidEmail(CharSequence target) {
            return !TextUtils.isEmpty(target) &&
                    android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isValidPassword(CharSequence target) {
        return target.length() >= MIN_PASS_LENGTH && !TextUtils.isDigitsOnly(target);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2) {
            selectedImg = data.getData();
            Glide.with(this).load(selectedImg)
                    .into((ImageButton)mView.findViewById(R.id.sign_up_photo));
        }
    }



    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(!editable.equals(mPassEt.getText())) {
            mVerifyPassLayout.setError(getString(R.string.sign_up_pass_error_2));
        } else {
            mVerifyPassLayout.setErrorEnabled(false);
        }
    }
}
