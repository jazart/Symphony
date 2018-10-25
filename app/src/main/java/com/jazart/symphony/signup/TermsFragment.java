package com.jazart.symphony.signup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jazart.symphony.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsFragment extends DialogFragment {

    @BindView(R.id.terms)
    TextView mTerms;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_terms, null);
        ButterKnife.bind(this, v);
        mTerms.setMovementMethod(new ScrollingMovementMethod());
        mTerms.setMovementMethod(LinkMovementMethod.getInstance());
        return new AlertDialog.Builder(requireContext())
                .setView(v)
                .setNegativeButton(getString(R.string.terms_decline), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED);
                    }
                })
                .setPositiveButton(getString(R.string.terms_accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setTitle("Terms")
                .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() != null)
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, null);
    }
}
