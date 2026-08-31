package com.sumon.bundleapp.installer.ui.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.sumon.bundleapp.installer.R;
import com.sumon.bundleapp.installer.utils.DbgPreferencesKeys;
import androidx.preference.Preference;

public class SuperSecretPreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_super_secret);

        Preference testCrash = findPreference(DbgPreferencesKeys.TEST_CRASH);
        if (testCrash != null) {
            testCrash.setOnPreferenceClickListener(p -> {
                throw new RuntimeException("Test Crash");
            });
        }
    }
}
