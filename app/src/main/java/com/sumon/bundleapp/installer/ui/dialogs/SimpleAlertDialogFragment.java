package com.sumon.bundleapp.installer.ui.dialogs;

import com.sumon.bundleapp.installer.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.fragment.app.DialogFragment;


import java.util.Objects;

public class SimpleAlertDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    private CharSequence mTitle;
    private CharSequence mMessage;

    public static SimpleAlertDialogFragment newInstance(Context c, @StringRes int title, @StringRes int message) {
        return newInstance(c.getText(title), c.getText(message));
    }

    public static SimpleAlertDialogFragment newInstance(CharSequence title, CharSequence message) {
        SimpleAlertDialogFragment fragment = new SimpleAlertDialogFragment();
        Bundle args = new Bundle();
        args.putCharSequence(ARG_TITLE, title);
        args.putCharSequence(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null)
            return;

        mTitle = args.getCharSequence(ARG_TITLE, "title");
        mMessage = args.getCharSequence(ARG_MESSAGE, "message");
    }

    /**
     * Creates the dialog displaying the configured title, message, and an OK button.
     *
     * @return the configured alert dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext())).setTitle(mTitle).setMessage(mMessage).setPositiveButton(R.string.ok, null).create();
    }

    /**
     * Notifies the parent fragment or hosting activity when the dialog is dismissed.
     *
     * @param dialog the dismissed dialog
     */
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        Object parent = getParentFragment();
        if (parent == null)
            parent = requireActivity();

        if (parent instanceof OnDismissListener && getTag() != null)
            ((OnDismissListener) parent).onDialogDismissed(getTag());

    }

    public interface OnDismissListener {

        void onDialogDismissed(@NonNull String dialogTag);

    }
}
