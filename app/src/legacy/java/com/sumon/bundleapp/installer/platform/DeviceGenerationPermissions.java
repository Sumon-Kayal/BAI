package com.sumon.bundleapp.installer.platform;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.sumon.bundleapp.installer.utils.PermissionsUtils;

/**
 * BAI Legacy (API 23-29). Storage here is exactly what {@code PermissionsUtils} always did before
 * the platform split: request READ_EXTERNAL_STORAGE + WRITE_EXTERNAL_STORAGE unconditionally,
 * unchanged from main's pre-split behavior. Notifications never needed a runtime permission on
 * this generation — API 33 (where POST_NOTIFICATIONS was introduced) is out of Legacy's whole
 * range, so there's nothing to gate here, unlike Modern's implementation of this same interface.
 */
public class DeviceGenerationPermissions implements PlatformPermissions {

    private static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public boolean hasStorageAccess(Context context) {
        for (String permission : STORAGE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean requestStorageAccess(Fragment fragment) {
        return PermissionsUtils.checkAndRequestPermissions(fragment, STORAGE_PERMISSIONS,
                PermissionsUtils.REQUEST_CODE_STORAGE_PERMISSIONS);
    }

    @Override
    public boolean hasNotificationAccess(Context context) {
        return true;
    }

    @Override
    public boolean requestNotificationAccess(Fragment fragment) {
        return true;
    }

    @Override
    public boolean requestNotificationAccess(Activity activity) {
        return true;
    }
}
