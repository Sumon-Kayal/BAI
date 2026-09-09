package com.sumon.bundleapp.installer.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionsUtils {
    public static final int REQUEST_CODE_STORAGE_PERMISSIONS = 322;
    public static final int REQUEST_CODE_SHIZUKU = 1337;

    public static boolean checkAndRequestStoragePermissions(Activity a) {
        return checkAndRequestPermissions(a, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSIONS);
    }

    public static boolean checkAndRequestStoragePermissions(Fragment f) {
        return checkAndRequestPermissions(f, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSIONS);
    }

    /**
     * ActivityResultLauncher-based counterpart of {@link #checkAndRequestStoragePermissions(Fragment)},
     * for callers migrated off the deprecated onRequestPermissionsResult path (see
     * UriDirectoryPickerDialogFragment, Installer2Fragment).
     * <p>
     * Return value is intentionally the OPPOSITE sense of the requestCode-based overloads above:
     * {@code true} means permission is not yet available and a request was just launched — the
     * caller should defer and wait for the launcher's callback. {@code false} means permission is
     * already granted and the caller should proceed immediately. Both call sites in this codebase
     * are written against this polarity ({@code if (checkAndRequest...) { defer } else { proceed }}).
     * <p>
     * Kept deliberately simple (same READ/WRITE_EXTERNAL_STORAGE check as the overload above) rather
     * than branching on Environment.isExternalStorageManager() for API 30+, since that check alone
     * doesn't grant the permission — it requires a Settings redirect flow (ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
     * that hasn't been decided on for BAI. Wire that in deliberately if/when this needs revisiting.
     */
    public static boolean checkAndRequestStoragePermissions(Fragment f, ActivityResultLauncher<String[]> launcher) {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT < 23)
            return false;

        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(f.requireContext(), permission) == PackageManager.PERMISSION_DENIED) {
                permissionsToRequest.add(permission);
            }
        }

        if (permissionsToRequest.isEmpty())
            return false;

        launcher.launch(permissionsToRequest.toArray(new String[0]));
        return true;
    }

    public static boolean checkAndRequestShizukuPermissions(Activity a) {
        return checkAndRequestPermissions(a, new String[]{"moe.shizuku.manager.permission.API_V23"}, REQUEST_CODE_SHIZUKU);
    }

    public static boolean checkAndRequestShizukuPermissions(Fragment f) {
        return checkAndRequestPermissions(f, new String[]{"moe.shizuku.manager.permission.API_V23"}, REQUEST_CODE_SHIZUKU);
    }

    public static final int REQUEST_CODE_NOTIFICATIONS = 4242;

    /**
     * Checks whether notification permission is available and requests it when needed.
     *
     * @param a the activity used to request notification permission
     * @return {@code true} if notifications are permitted or the platform does not require runtime notification permission,
     *         {@code false} otherwise
     */
    public static boolean checkAndRequestNotificationPermission(Activity a) {
        if (Build.VERSION.SDK_INT < 33)
            return true;

        return checkAndRequestPermissions(a, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATIONS);
    }

    /**
     * Checks whether notification permission is granted and requests it when required.
     *
     * @param f the fragment requesting notification permission
     * @return {@code true} if notification permission is granted or the device runs Android below API 33, {@code false} otherwise
     */
    public static boolean checkAndRequestNotificationPermission(Fragment f) {
        if (Build.VERSION.SDK_INT < 33)
            return true;

        return checkAndRequestPermissions(f, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATIONS);
    }

    /**
     * Checks whether all specified permissions are granted and requests them when necessary.
     *
     * @param a           the activity used to check and request permissions
     * @param permissions the permissions to check
     * @param requestCode the request code for the permission request
     * @return {@code true} if all permissions are granted or the platform does not require runtime permissions, {@code false} if a request was initiated
     */
    private static boolean checkAndRequestPermissions(Activity a, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        for (String permission : permissions) {
            if ((ActivityCompat.checkSelfPermission(a, permission)) == PackageManager.PERMISSION_DENIED) {
                a.requestPermissions(permissions, requestCode);
                return false;
            }
        }
        return true;
    }

    private static boolean checkAndRequestPermissions(Fragment f, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        for (String permission : permissions) {
            if ((ActivityCompat.checkSelfPermission(f.requireContext(), permission)) == PackageManager.PERMISSION_DENIED) {
                f.requestPermissions(permissions, requestCode);
                return false;
            }
        }
        return true;
    }

}
