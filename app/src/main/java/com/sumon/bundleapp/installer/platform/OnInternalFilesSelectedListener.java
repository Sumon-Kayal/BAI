package com.sumon.bundleapp.installer.platform;

import java.io.File;
import java.util.List;

/**
 * Result callback for an {@link InternalPickerRequest}. The parent fragment or activity of
 * whatever {@link PlatformFilePicker#createInternalPicker} returns must implement this.
 * <p>
 * Hoisted out of the legacy-only FilePickerDialogFragment so shared (src/main) hosts can
 * implement it without depending on the vendored file-picker library.
 */
public interface OnInternalFilesSelectedListener {
    void onFilesSelected(String tag, List<File> files);
}
