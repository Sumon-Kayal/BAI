package com.sumon.bundleapp.installer.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class PermissionsUtils {
    public static final int REQUEST_CODE_STORAGE_PERMISSIONS = 322;
    public static final int REQUEST_CODE_SHIZUKU = 1337;

    /**
     * Checks whether the app can access the filesystem used by its legacy file browser.
     *
     * Android 13+ no longer grants READ_EXTERNAL_STORAGE/WRITE_EXTERNAL_STORAGE to
     * apps targeting modern SDKs. On Android 11+ the equivalent capability for
     * this app's broad filesystem access is MANAGE_EXTERNAL_STORAGE.
     */
    public static boolean checkAndRequestStoragePermissions(Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager())
                return true;

            openAllFilesAccessSettings(a);
            return false;
        }

        return checkAndRequestPermissions(
                a,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_STORAGE_PERMISSIONS
        );
    }

    public static boolean checkAndRequestStoragePermissions(Fragment f) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager())
                return true;

            openAllFilesAccessSettings(f.requireContext());
            return false;
        }

        return checkAndRequestPermissions(
                f,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_STORAGE_PERMISSIONS
        );
    }

    private static void openAllFilesAccessSettings(android.content.Context context) {
        Intent intent = new Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse("package:" + context.getPackageName())
        );
        context.startActivity(intent);
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
