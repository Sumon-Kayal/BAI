package com.sumon.bundleapp.installer.platform;

import androidx.fragment.app.DialogFragment;

/**
 * Whether install-file picking should offer the internal file browser (rooted at
 * {@code Environment.getExternalStorageDirectory()}) as an option at all.
 * <p>
 * Legacy: yes — {@code requestLegacyExternalStorage} keeps unrestricted external storage access
 * working through the end of Legacy's range (API 29), so the internal browser is genuinely
 * functional there.
 * <p>
 * Modern: no. Per BAI-PHASE1-PLATFORM-INVENTORY.md and BAI-PHASE2-CORE-BOUNDARY.md, main's
 * pre-split storage permissions don't grant real access on API 30+ regardless of whether they're
 * requested — the internal browser would open but not reliably see real files. SAF is the only
 * picking method that actually works on this generation, so it's the only one offered.
 * <p>
 * Resolved at compile time via flavor source sets, same as {@link PlatformPermissions} — no
 * runtime detection, one implementation per generation.
 */
public interface PlatformFilePicker {
    static PlatformFilePicker getInstance() {
        return Holder.INSTANCE;
    }

    boolean offersInternalPicker();

    /**
     * Builds the internal picker dialog for {@code request}. Only Legacy's implementation
     * actually depends on the vendored file-picker library; the caller must check
     * {@link #offersInternalPicker()} first — Modern's implementation is never meant to be
     * called and throws.
     * <p>
     * The returned fragment's host (parent fragment, or activity if there is none) must
     * implement {@link OnInternalFilesSelectedListener}.
     */
    DialogFragment createInternalPicker(InternalPickerRequest request);

    class Holder {
        private static final PlatformFilePicker INSTANCE = new DeviceGenerationFilePicker();

        private Holder() {
        }
    }
}
