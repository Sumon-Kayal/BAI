package com.sumon.bundleapp.installer.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;

import java.util.Objects;

public class MiuiUtils {

    public static boolean isMiui() {
        return !TextUtils.isEmpty(Utils.getSystemProperty("ro.miui.ui.version.name"));
    }

    public static String getMiuiVersionName() {
        String versionName = Utils.getSystemProperty("ro.miui.ui.version.name");
        return !TextUtils.isEmpty(versionName) ? versionName : "???";
    }

    public static int getMiuiVersionCode() {
        try {
            return Integer.parseInt(
                    Objects.requireNonNull(
                            Utils.getSystemProperty("ro.miui.ui.version.code")
                    )
            );
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getActualMiuiVersion() {
        return Build.VERSION.INCREMENTAL;
    }

    private static int[] parseVersionIntoParts(String version) {
        try {
            String[] versionParts = version.split("\\.");
            int[] intVersionParts = new int[versionParts.length];

            for (int i = 0; i < versionParts.length; i++)
                intVersionParts[i] = Integer.parseInt(versionParts[i]);

            return intVersionParts;
        } catch (Exception e) {
            return new int[]{-1};
        }
    }

    /**
     * @return 0 if versions are equal, values less than 0 if ver1 is lower than ver2,
     *         values greater than 0 if ver1 is higher than ver2
     */
    private static int compareVersions(String version1, String version2) {
        int[] version1Parts = parseVersionIntoParts(version1);
        int[] version2Parts = parseVersionIntoParts(version2);

        int length = Math.max(version1Parts.length, version2Parts.length);

        for (int i = 0; i < length; i++) {
            int part1 = i < version1Parts.length ? version1Parts[i] : 0;
            int part2 = i < version2Parts.length ? version2Parts[i] : 0;

            if (part1 < part2)
                return -1;

            if (part1 > part2)
                return 1;
        }

        return 0;
    }

    public static boolean isActualMiuiVersionAtLeast(String targetVer) {
        return compareVersions(getActualMiuiVersion(), targetVer) >= 0;
    }

    /**
     * @return true if the device's MIUI version (per ro.miui.ui.version.name, e.g. "V12.5")
     *         is at most targetVer (e.g. "12.4").
     */
    public static boolean isMiuiVersionAtMost(String targetVer) {
        String versionName = getMiuiVersionName();

        if ("???".equals(versionName))
            return true;

        if (versionName.startsWith("V") || versionName.startsWith("v"))
            versionName = versionName.substring(1);

        return compareVersions(versionName, targetVer) <= 0;
    }

    @SuppressLint("PrivateApi")
    public static boolean isMiuiOptimizationDisabled() {
        if ("0".equals(Utils.getSystemProperty("persist.sys.miui_optimization")))
            return true;

        try {
            return (boolean) Class.forName("android.miui.AppOpsUtils")
                    .getDeclaredMethod("isXOptMode")
                    .invoke(null);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFixedMiui() {
        return !isMiuiVersionAtMost("12.4") || isMiuiOptimizationDisabled();
    }
}
