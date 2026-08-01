package com.sumon.bundleapp.installer.backup2;

import com.sumon.bundleapp.installer.model.common.PackageMeta;

public interface BackupApp {

    PackageMeta packageMeta();

    boolean isInstalled();

    BackupStatus backupStatus();
}
