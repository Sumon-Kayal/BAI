package com.sumon.bundleapp.installer.model.apksource;

import java.util.zip.ZipEntry;

/**
 * An ApkSource backed by a zip archive
 */
public interface ZipBackedApkSource extends ApkSource {

    /**
     * @return ZipEntry for the current APK
     */
    ZipEntry getEntry();

}
