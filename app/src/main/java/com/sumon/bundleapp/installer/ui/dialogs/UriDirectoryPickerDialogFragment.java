package com.sumon.bundleapp.installer.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.sumon.bundleapp.installer.R;
import com.sumon.bundleapp.installer.utils.PermissionsUtils;
import com.sumon.bundleapp.installer.utils.Utils;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Picks a directory, automatically choosing the selection strategy by Android version instead
 * of asking the user to choose (which most users have no informed basis to answer):
 * <p>
 * - Below Android 10 (API 29): the in-app "Internal" file picker (java.io.File + a runtime
 * storage permission). Scoped storage doesn't apply yet, so this works reliably here.
 * - Android 10+ (API 29+): Storage Access Framework (ACTION_OPEN_DOCUMENT_TREE). Needs no
 * special permission, and is the only approach scoped storage reliably supports for broad
 * directory access on modern Android — the in-app picker's plain READ/WRITE_EXTERNAL_STORAGE
 * permission stops being reliable for arbitrary paths from here on.
 * <p>
 * This is a headless fragment (no UI of its own) rather than a dialog: it only exists to host
 * the ActivityResultLaunchers and the child file-picker dialog for as long as picking is in
 * progress, then removes itself. Kept as a Fragment (not a DialogFragment) specifically so it
 * has no window/dialog chrome of its own to show or hide — the actual picker (the in-app dialog
 * or the system SAF UI) is the only thing the user ever sees.
 */
public class UriDirectoryPickerDialogFragment extends Fragment implements FilePickerDialogFragment.OnFilesSelectedListener {
    private static final String BACKUP_DIR_TAG = "backup_dir";
    private static final String FILE_PICKER_TAG = "directory_picker";
    private static final String PERMISSION_ALERT_TAG = "storage_permission_alert";
    private static final String STATE_RESULT_DELIVERED = "result_delivered";

    private boolean mResultDelivered;
    private FilePickerDialogFragment mPendingFilePicker;

    private final ActivityResultLauncher<Intent> safDirectoryPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    handleSafDirectoryResult(result.getData());
                } else {
                    finish();
                }
            });

    private final ActivityResultLauncher<String[]> storagePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    if (!isGranted) {
                        allGranted = false;
                        break;
                    }
                }

                if (allGranted && mPendingFilePicker != null) {
                    openFilePicker(mPendingFilePicker);
                    mPendingFilePicker = null;
                } else if (!allGranted) {
                    showPermissionDeniedAlert();
                }
            });

    // Context kept unused for call-site compatibility with the existing two callers
    // (LocalBackupStorageSetupFragment, LocalBackupStorageSettingsFragment) — no need to
    // touch either of them for this change.
    public static UriDirectoryPickerDialogFragment newInstance(Context context) {
        return new UriDirectoryPickerDialogFragment();
    }

    /** Mirrors DialogFragment.show(FragmentManager, String) so existing callers don't need to change. */
    public void show(FragmentManager manager, String tag) {
        manager.beginTransaction().add(this, tag).commitNowAllowingStateLoss();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                startInternalPicker();
            } else {
                startSafPicker();
            }
        } else {
            mResultDelivered = savedInstanceState.getBoolean(STATE_RESULT_DELIVERED);
            if (mResultDelivered) {
                finish();
                return;
            }

            observeRestoredChild(FILE_PICKER_TAG);
            observeRestoredChild(PERMISSION_ALERT_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESULT_DELIVERED, mResultDelivered);
    }

    private void startInternalPicker() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.DIR_SELECT;
        properties.root = Environment.getExternalStorageDirectory();

        openFilePicker(FilePickerDialogFragment.newInstance(BACKUP_DIR_TAG, getString(R.string.settings_main_pick_dir), properties));
    }

    private void startSafPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        safDirectoryPickerLauncher.launch(Intent.createChooser(intent, getString(R.string.settings_main_pick_dir)));
    }

    private void openFilePicker(FilePickerDialogFragment filePicker) {
        if (PermissionsUtils.checkAndRequestStoragePermissions(this, storagePermissionLauncher)) {
            mPendingFilePicker = filePicker;
            return;
        }

        observeChildUntilDestroyed(filePicker);
        filePicker.show(getChildFragmentManager(), FILE_PICKER_TAG);
    }

    private void showPermissionDeniedAlert() {
        SimpleAlertDialogFragment alert = SimpleAlertDialogFragment.newInstance(
                getString(R.string.error), getString(R.string.permissions_required_storage));
        observeChildUntilDestroyed(alert);
        alert.show(getChildFragmentManager(), PERMISSION_ALERT_TAG);
    }

    private void observeRestoredChild(String tag) {
        Fragment child = getChildFragmentManager().findFragmentByTag(tag);
        if (child != null)
            observeChildUntilDestroyed(child);
    }

    private void observeChildUntilDestroyed(Fragment child) {
        // The picker reports successful selection only, so cancellation is observed through its
        // lifecycle. The same mechanism keeps a permission alert visible until it is dismissed.
        getChildFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                if (f == child) {
                    fm.unregisterFragmentLifecycleCallbacks(this);
                    if (!mResultDelivered)
                        finish();
                }
            }
        }, false);
    }

    private void handleSafDirectoryResult(Intent data) {
        Uri backupDirUri = Objects.requireNonNull(data.getData());
        requireContext().getContentResolver().takePersistableUriPermission(backupDirUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        onDirectoryPicked(backupDirUri);
    }

    private void onDirectoryPicked(Uri dirUri) {
        mResultDelivered = true;

        OnDirectoryPickedListener listener = Utils.getParentAs(this, OnDirectoryPickedListener.class);
        if (listener != null) {
            listener.onDirectoryPicked(getTag(), dirUri);
        }
        finish();
    }

    private void finish() {
        if (isAdded()) {
            getParentFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    @Override
    public void onFilesSelected(String tag, List<File> files) {
        if (BACKUP_DIR_TAG.equals(tag) && !files.isEmpty()) {
            onDirectoryPicked(new Uri.Builder()
                    .scheme("file")
                    .path(files.get(0).getAbsolutePath())
                    .build());
        }
    }

    public interface OnDirectoryPickedListener {
        void onDirectoryPicked(@Nullable String tag, Uri dirUri);
    }
}
