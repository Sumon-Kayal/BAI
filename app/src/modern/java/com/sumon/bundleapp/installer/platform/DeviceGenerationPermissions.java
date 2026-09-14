package com.sumon.bundleapp.installer.platform;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.sumon.bundleapp.installer.utils.PermissionsUtils;

/**
 * BAI Modern (API 30-36).
 * <p>
 * Notifications: this is the real, already-correct logic that used to live directly in
 * {@code PermissionsUtils} — POST_NOTIFICATIONS is only a runtime permission from API 33
 * (TIRAMISU) onward, so devices on 30-32 always pass. Moved here unchanged, just relocated.
 * <p>
 * Storage: <b>NOT yet a real Modern implementation — this is main's old unconditional
 * READ/WRITE_EXTERNAL_STORAGE request, kept only so nothing regresses silently while this class
 * didn't exist yet.</b> That's the wrong long-term strategy for 30+ (see
 * BAI-PHASE1-PLATFORM-INVENTORY.md's storage/SAF section) and replacing it — SAF-first, or All
 * Files Access, or granular media permissions, whichever you decide on — is exactly what Phase 4
 * is for. Don't mistake this method compiling and running today for it being correct.
 */
public class DeviceGenerationPermissions implements PlatformPermissions {

    private static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public boolean hasStorageAccess(Context context) {
        // TODO(Phase 4): this is main's pre-split check, carried over as a placeholder only.
        for (String permission : STORAGE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean requestStorageAccess(Fragment fragment) {
        // TODO(Phase 4): replace with this generation's real storage strategy.
        return PermissionsUtils.checkAndRequestPermissions(fragment, STORAGE_PERMISSIONS,
                PermissionsUtils.REQUEST_CODE_STORAGE_PERMISSIONS);
    }

    @Override
    public boolean hasNotificationAccess(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean requestNotificationAccess(Fragment fragment) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return PermissionsUtils.checkAndRequestPermissions(fragment,
                new String[]{Manifest.permission.POST_NOTIFICATIONS}, PermissionsUtils.REQUEST_CODE_NOTIFICATIONS);
    }

    @Override
    public boolean requestNotificationAccess(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return PermissionsUtils.checkAndRequestPermissions(activity,
                new String[]{Manifest.permission.POST_NOTIFICATIONS}, PermissionsUtils.REQUEST_CODE_NOTIFICATIONS);
    }
}
