package com.sumon.bundleapp.installer.platform;

import androidx.fragment.app.DialogFragment;

public class DeviceGenerationFilePicker implements PlatformFilePicker {
    @Override
    public boolean offersInternalPicker() {
        return false;
    }

    @Override
    public DialogFragment createInternalPicker(InternalPickerRequest request) {
        throw new UnsupportedOperationException(
                "Modern has no internal picker (offersInternalPicker() is false) — caller should have checked that first.");
    }
}

