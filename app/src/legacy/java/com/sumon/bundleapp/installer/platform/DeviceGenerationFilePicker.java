package com.sumon.bundleapp.installer.platform;

import androidx.fragment.app.DialogFragment;

import com.sumon.bundleapp.installer.ui.dialogs.FilePickerDialogFragment;

public class DeviceGenerationFilePicker implements PlatformFilePicker {
    @Override
    public boolean offersInternalPicker() {
        return true;
    }

    @Override
    public DialogFragment createInternalPicker(InternalPickerRequest request) {
        return FilePickerDialogFragment.newInstance(request);
    }
}

