package com.sumon.bundleapp.installer.installer;

import android.content.Context;

import com.sumon.bundleapp.installer.installer.rooted.RootedSAIPackageInstaller;
import com.sumon.bundleapp.installer.installer.rootless.RootlessSAIPackageInstaller;
import com.sumon.bundleapp.installer.installer.shizuku.ShizukuSAIPackageInstaller;
import com.sumon.bundleapp.installer.utils.PreferencesHelper;
import com.sumon.bundleapp.installer.utils.PreferencesValues;

public class PackageInstallerProvider {
    public static SAIPackageInstaller getInstaller(Context c) {

        switch (PreferencesHelper.getInstance(c).getInstaller()) {
            case PreferencesValues.INSTALLER_ROOTLESS:
                return RootlessSAIPackageInstaller.getInstance(c);
            case PreferencesValues.INSTALLER_ROOTED:
                return RootedSAIPackageInstaller.getInstance(c);
            case PreferencesValues.INSTALLER_SHIZUKU:
                return ShizukuSAIPackageInstaller.getInstance(c);
        }

        return RootlessSAIPackageInstaller.getInstance(c);
    }
}
