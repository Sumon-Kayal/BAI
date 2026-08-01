package com.aefyr.flexfilter.builtin;

/**
 * The original library used this as a reflection target for rendering
 * filter rows. This vendored FilterDialog builds its UI directly instead
 * (see FilterDialog.buildFilterSection), so this class only needs to exist
 * as a valid Class reference for the BackupFragment.newInstance(...) call.
 */
public final class DefaultFilterConfigViewHolderFactory {
    private DefaultFilterConfigViewHolderFactory() {
    }
}
