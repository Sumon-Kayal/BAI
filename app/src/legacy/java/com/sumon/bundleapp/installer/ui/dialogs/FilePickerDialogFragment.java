package com.sumon.bundleapp.installer.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sumon.bundleapp.installer.platform.InternalPickerRequest;
import com.sumon.bundleapp.installer.platform.OnInternalFilesSelectedListener;
import com.sumon.bundleapp.installer.utils.Theme;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.ArrayList;

/**
 * Legacy-only wrapper around the vendored file-picker library. Built from an
 * {@link InternalPickerRequest} so shared (src/main) callers never need to depend on the
 * library directly — see BAI-ROADMAP-AND-TODO.md, FilePicker architecture.
 */
public class FilePickerDialogFragment extends DialogFragment {

    private static final String ARG_TAG = "tag";
    private static final String ARG_TITLE = "title";
    private static final String ARG_SELECTION_MODE = "selection_mode";
    private static final String ARG_SELECTION_TYPE = "selection_type";
    private static final String ARG_ROOT_FOLDER = "root";
    private static final String ARG_STARTING_FOLDER = "start";
    private static final String ARG_ERROR_FOLDER = "error";
    private static final String ARG_EXTENSIONS = "extensions";
    private static final String ARG_SORT_BY = "sort_by";
    private static final String ARG_SORT_ORDER = "sort_order";

    private OnInternalFilesSelectedListener mListener;

    private String mTag;
    private String mTitle = "Select files";
    private final DialogProperties mDialogProperties = new DialogProperties();

    public static FilePickerDialogFragment newInstance(InternalPickerRequest request) {
        FilePickerDialogFragment fragment = new FilePickerDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TAG, request.tag);
        args.putString(ARG_TITLE, request.title);
        args.putInt(ARG_SELECTION_MODE, request.selectionMode == InternalPickerRequest.SelectionMode.MULTI
                ? DialogConfigs.MULTI_MODE : DialogConfigs.SINGLE_MODE);
        args.putInt(ARG_SELECTION_TYPE, request.selectionType == InternalPickerRequest.SelectionType.DIRECTORY
                ? DialogConfigs.DIR_SELECT : DialogConfigs.FILE_SELECT);
        if (request.root != null)
            args.putString(ARG_ROOT_FOLDER, request.root.getAbsolutePath());
        if (request.offset != null)
            args.putString(ARG_STARTING_FOLDER, request.offset.getAbsolutePath());
        if (request.errorDir != null)
            args.putString(ARG_ERROR_FOLDER, request.errorDir.getAbsolutePath());
        args.putStringArray(ARG_EXTENSIONS, request.extensions);
        args.putInt(ARG_SORT_BY, request.sortBy);
        args.putInt(ARG_SORT_ORDER, request.sortOrder);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null)
            return;

        mTag = args.getString(ARG_TAG);
        mTitle = args.getString(ARG_TITLE);

        mDialogProperties.selection_mode = args.getInt(ARG_SELECTION_MODE, mDialogProperties.selection_mode);
        mDialogProperties.selection_type = args.getInt(ARG_SELECTION_TYPE, mDialogProperties.selection_type);
        mDialogProperties.root = new File(args.getString(ARG_ROOT_FOLDER, mDialogProperties.root.getAbsolutePath()));
        mDialogProperties.offset = new File(args.getString(ARG_STARTING_FOLDER, mDialogProperties.offset.getAbsolutePath()));
        mDialogProperties.error_dir = new File(args.getString(ARG_ERROR_FOLDER, mDialogProperties.error_dir.getAbsolutePath()));
        mDialogProperties.extensions = args.getStringArray(ARG_EXTENSIONS);
        mDialogProperties.sortBy = args.getInt(ARG_SORT_BY, mDialogProperties.sortBy);
        mDialogProperties.sortOrder = args.getInt(ARG_SORT_ORDER, mDialogProperties.sortOrder);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FilePickerDialog dialog = new FilePickerDialog(getContext(), mDialogProperties, Theme.getInstance(getContext()).getCurrentTheme().getTheme());
        dialog.setDialogSelectionListener((files) -> {
            if (mListener == null)
                return;

            ArrayList<File> selectedFiles = new ArrayList<>(files.length);

            for (String file : files)
                selectedFiles.add(new File(file));

            mListener.onFilesSelected(mTag, selectedFiles);
        });
        dialog.setTitle(mTitle);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (getParentFragment() != null)
                mListener = (OnInternalFilesSelectedListener) getParentFragment();
            else
                mListener = (OnInternalFilesSelectedListener) getActivity();
        } catch (Exception e) {
            throw new IllegalStateException("Activity/Fragment that uses FilePickerDialogFragment must implement OnInternalFilesSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
