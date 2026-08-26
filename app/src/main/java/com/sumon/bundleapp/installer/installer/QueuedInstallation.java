package com.sumon.bundleapp.installer.installer;

import android.content.Context;

import com.sumon.bundleapp.installer.model.apksource.ApkSource;

public class QueuedInstallation {

    private final Context mContext;
    private final ApkSource mApkSource;
    private final long mId;

    QueuedInstallation(Context c, ApkSource apkSource, long id) {
        mContext = c;
        mApkSource = apkSource;
        mId = id;
    }

    public long getId() {
        return mId;
    }

    ApkSource getApkSource() {
        return mApkSource;
    }
}
