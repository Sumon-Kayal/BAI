package com.sumon.bundleapp.installer.ui.dialogs;

import com.sumon.bundleapp.installer.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sumon.bundleapp.installer.utils.AlertsUtils;
import com.sumon.bundleapp.installer.utils.PermissionsUtils;
import com.sumon.bundleapp.installer.utils.Utils;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class UriDirectoryPickerDialogFragment extends SingleChoiceListDialogFragment implements FilePickerDialogFragment.OnFilesSelectedListener {
    private static final int REQUEST_CODE_SELECT_BACKUP_DIR = 1334;
    private static final String STATE_PENDING_INTERNAL_PICK = "pending_internal_pick";

    // A Fragment reference can't survive process death, and doesn't reliably survive a plain
    // config change either — replaced with a flag plus reconstruction, both of which do.
    private boolean mPendingInternalPick;

    public static UriDirectoryPickerDialogFragment newInstance(Context context) {
        UriDirectoryPickerDialogFragment fragment = new UriDirectoryPickerDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAMS, new DialogParams(context.getText(R.string.settings_main_backup_backup_dir_dialog), R.array.backup_dir_selection_methods));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mPendingInternalPick = savedInstanceState.getBoolean(STATE_PENDING_INTERNAL_PICK);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_PENDING_INTERNAL_PICK, mPendingInternalPick);
    }

    private FilePickerDialogFragment createInternalDirPicker() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.DIR_SELECT;
        properties.root = Environment.getExternalStorageDirectory();

        return FilePickerDialogFragment.newInstance("backup_dir", getString(R.string.settings_main_pick_dir), properties);
    }

    @Override
    protected void deliverSelectionResult(String tag, int selectedItemIndex) {
        switch (selectedItemIndex) {
            case 0:
                openFilePicker(createInternalDirPicker());
                break;
            case 1:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                // Requesting the grant flags up front, not just at takePersistableUriPermission
                // time below — without FLAG_GRANT_PERSISTABLE_URI_PERMISSION here specifically,
                // that later call throws SecurityException even though the user just picked a
                // folder successfully.
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.installer_pick_apks)), REQUEST_CODE_SELECT_BACKUP_DIR);
                break;
        }
    }

    private void openFilePicker(FilePickerDialogFragment filePicker) {
        if (!PermissionsUtils.checkAndRequestStoragePermissions(this)) {
            mPendingInternalPick = true;
            return;
        }
        filePicker.show(getChildFragmentManager(), null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionsUtils.REQUEST_CODE_STORAGE_PERMISSIONS) {
            boolean permissionsGranted = grantResults.length > 0;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    permissionsGranted = false;
                    break;
                }
            }

            if (!permissionsGranted)
                AlertsUtils.showAlert(this, R.string.error, R.string.permissions_required_storage);
            else {
                if (mPendingInternalPick) {
                    mPendingInternalPick = false;
                    openFilePicker(createInternalDirPicker());
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_BACKUP_DIR) {
            if (resultCode != Activity.RESULT_OK)
                return;

            Objects.requireNonNull(data);
            Uri backupDirUri = Objects.requireNonNull(data.getData());
            requireContext().getContentResolver().takePersistableUriPermission(backupDirUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            onDirectoryPicked(backupDirUri);
        }
    }

    private void onDirectoryPicked(Uri dirUri) {
        OnDirectoryPickedListener listener = Utils.getParentAs(this, OnDirectoryPickedListener.class);
        if (listener != null)
            listener.onDirectoryPicked(getTag(), dirUri);

        dismiss();
    }

    @Override
    public void onFilesSelected(String tag, List<File> files) {
        switch (tag) {
            case "backup_dir":
                onDirectoryPicked(new Uri.Builder()
                        .scheme("file")
                        .path(files.get(0).getAbsolutePath())
                        .build());
                break;
        }
    }

    public interface OnDirectoryPickedListener {

        void onDirectoryPicked(@Nullable String tag, Uri dirUri);

    }
}
