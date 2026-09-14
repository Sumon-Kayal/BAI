package com.sumon.bundleapp.installer.platform;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

/**
 * The generation-specific half of permission handling — the parts of "can we read/write files"
 * and "can we post notifications" that genuinely differ between BAI Legacy (API 23-29) and BAI
 * Modern (API 30-36).
 * <p>
 * Resolved at compile time, not runtime: {@code src/legacy/java} and {@code src/modern/java} each
 * provide their own {@code DeviceGenerationPermissions}, and Gradle's flavor source sets pick the
 * right one automatically. There's deliberately no factory/detection method here — if you find
 * yourself wanting one, something has gone wrong, since a given APK is only ever one generation.
 * <p>
 * What's <b>not</b> here on purpose: Shizuku's permission and the generic check/request helper
 * underneath both storage and notifications have no version-dependent behavior at all (see
 * BAI-PHASE1-PLATFORM-INVENTORY.md) and stay in {@link com.sumon.bundleapp.installer.utils.PermissionsUtils}
 * as plain shared Core code.
 */
public interface PlatformPermissions {

    /** Whether the app currently has whatever access it needs to read/write user-selected files. */
    boolean hasStorageAccess(Context context);

    /**
     * Requests whatever storage access this generation needs.
     *
     * @return {@code true} if access is already granted (nothing to request), {@code false} if a
     * request was just started and the caller should wait for the result.
     */
    boolean requestStorageAccess(Fragment fragment);

    /** Whether the app currently has permission to post notifications, or doesn't need it. */
    boolean hasNotificationAccess(Context context);

    /**
     * Requests notification permission if this generation requires it.
     *
     * @return {@code true} if permitted already or not required on this generation, {@code false}
     * if a request was just started and the caller should wait for the result.
     */
    boolean requestNotificationAccess(Fragment fragment);

    /** Same as {@link #requestNotificationAccess(Fragment)}, for the one call site that's an Activity, not a Fragment. */
    boolean requestNotificationAccess(Activity activity);
}
