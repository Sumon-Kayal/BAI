package com.sumon.bundleapp.installer.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.sumon.bundleapp.installer.platform.DeviceGenerationPermissions;

public class PermissionsUtils {
    public static final int REQUEST_CODE_STORAGE_PERMISSIONS = 322;
    public static final int REQUEST_CODE_SHIZUKU = 1337;
    public static final int REQUEST_CODE_NOTIFICATIONS = 4242;

    // Storage and notification handling now live behind PlatformPermissions (see
    // BAI-PHASE2-CORE-BOUNDARY.md) — src/legacy and src/modern each provide their own
    // DeviceGenerationPermissions, and these two methods just forward to whichever one Gradle
    // compiled in for this flavor. Callers don't need to know or care which generation they're on.
    //
    // The Activity overload of storage permissions that used to live here had no actual callers
    // (every real call site passes a Fragment) and was dropped rather than ported forward.

    public static boolean checkAndRequestStoragePermissions(Fragment f) {
        return new DeviceGenerationPermissions().requestStorageAccess(f);
    }

    public static boolean checkAndRequestNotificationPermission(Activity a) {
        return new DeviceGenerationPermissions().requestNotificationAccess(a);
    }

    public static boolean checkAndRequestNotificationPermission(Fragment f) {
        return new DeviceGenerationPermissions().requestNotificationAccess(f);
    }

    public static boolean checkAndRequestShizukuPermissions(Activity a) {
        return checkAndRequestPermissions(a, new String[]{"moe.shizuku.manager.permission.API_V23"}, REQUEST_CODE_SHIZUKU);
    }

    public static boolean checkAndRequestShizukuPermissions(Fragment f) {
        return checkAndRequestPermissions(f, new String[]{"moe.shizuku.manager.permission.API_V23"}, REQUEST_CODE_SHIZUKU);
    }

    /**
     * Checks whether all specified permissions are granted and requests them when necessary.
     * Public (not private) so the platform-package implementations of {@code PlatformPermissions}
     * can reuse it instead of duplicating the same request boilerplate.
     *
     * @param a           the activity used to check and request permissions
     * @param permissions the permissions to check
     * @param requestCode the request code for the permission request
     * @return {@code true} if all permissions are granted, {@code false} if a request was initiated
     */
    public static boolean checkAndRequestPermissions(Activity a, String[] permissions, int requestCode) {
        for (String permission : permissions) {
            if ((ActivityCompat.checkSelfPermission(a, permission)) == PackageManager.PERMISSION_DENIED) {
                a.requestPermissions(permissions, requestCode);
                return false;
            }
        }
        return true;
    }

    public static boolean checkAndRequestPermissions(Fragment f, String[] permissions, int requestCode) {
        for (String permission : permissions) {
            if ((ActivityCompat.checkSelfPermission(f.requireContext(), permission)) == PackageManager.PERMISSION_DENIED) {
                f.requestPermissions(permissions, requestCode);
                return false;
            }
        }
        return true;
    }

}
