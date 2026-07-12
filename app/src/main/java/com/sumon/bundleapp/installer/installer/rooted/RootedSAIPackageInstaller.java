package com.sumon.bundleapp.installer.installer.rooted;

import android.content.Context;

import com.sumon.bundleapp.installer.R;
import com.sumon.bundleapp.installer.installer.ShellSAIPackageInstaller;
import com.sumon.bundleapp.installer.shell.Shell;
import com.sumon.bundleapp.installer.shell.SuShell;

public class RootedSAIPackageInstaller extends ShellSAIPackageInstaller {
    private static RootedSAIPackageInstaller sInstance;

    public static RootedSAIPackageInstaller getInstance(Context c) {
        synchronized (RootedSAIPackageInstaller.class) {
            return sInstance != null ? sInstance : new RootedSAIPackageInstaller(c);
        }
    }

    private RootedSAIPackageInstaller(Context c) {
        super(c);
        sInstance = this;
    }

    @Override
    protected Shell getShell() {
        return SuShell.getInstance();
    }

    @Override
    protected String getInstallerName() {
        return "Rooted";
    }

    @Override
    protected String getShellUnavailableMessage() {
        return getContext().getString(R.string.installer_error_root_no_root);
    }
}
