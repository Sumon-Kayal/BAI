package com.sumon.bundleapp.installer.installer.shizuku;

import android.content.Context;

import com.sumon.bundleapp.installer.R;
import com.sumon.bundleapp.installer.installer.ShellSAIPackageInstaller;
import com.sumon.bundleapp.installer.shell.Shell;
import com.sumon.bundleapp.installer.shell.ShizukuShell;

public class ShizukuSAIPackageInstaller extends ShellSAIPackageInstaller {
    private static ShizukuSAIPackageInstaller sInstance;

    public static ShizukuSAIPackageInstaller getInstance(Context c) {
        synchronized (ShizukuSAIPackageInstaller.class) {
            return sInstance != null ? sInstance : new ShizukuSAIPackageInstaller(c);
        }
    }

    private ShizukuSAIPackageInstaller(Context c) {
        super(c);
        sInstance = this;
    }

    @Override
    protected Shell getShell() {
        return ShizukuShell.getInstance();
    }

    @Override
    protected String getInstallerName() {
        return "Shizuku";
    }

    @Override
    protected String getShellUnavailableMessage() {
        return getContext().getString(R.string.installer_error_shizuku_unavailable);
    }
}
