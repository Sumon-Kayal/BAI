package com.aefyr.flexfilter.config.core;

import java.util.List;

/**
 * Vendored, purpose-built replacement for com.aefyr.flexfilter (the
 * JitPack-hosted dependency no longer resolves). Kept at the original
 * package name so all call sites work unmodified. Implements only what
 * BackupPackagesFilterConfig/BackupViewModel/BackupFragment actually use.
 */
public interface FilterConfig extends java.io.Serializable {
    String id();

    CharSequence name();
}
