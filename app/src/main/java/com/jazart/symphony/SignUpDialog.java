package com.jazart.symphony;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class SignUpDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private void sendResult(int resultCode) {
        if(getTargetFragment() == null) {
            return;
        }

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.signup_fragment, null);
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Sign-up")
                .setMessage("New User")
                .setPositiveButton(android.R.string.ok,this)
                .setPositiveButton(android.R.string.cancel, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}
