package com.sumon.bundleapp.installer.ui.fragments;

import com.sumon.bundleapp.installer.R;
import com.sumon.bundleapp.installer.BuildConfig;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.sumon.bundleapp.installer.shell.SuShell;
import com.sumon.bundleapp.installer.ui.activities.AboutActivity;
import com.sumon.bundleapp.installer.ui.activities.ApkActionViewProxyActivity;
import com.sumon.bundleapp.installer.ui.activities.BackupSettingsActivity;
import com.sumon.bundleapp.installer.ui.dialogs.DarkLightThemeSelectionDialogFragment;
import com.sumon.bundleapp.installer.ui.dialogs.FilePickerDialogFragment;
import com.sumon.bundleapp.installer.ui.dialogs.SimpleAlertDialogFragment;
import com.sumon.bundleapp.installer.ui.dialogs.SingleChoiceListDialogFragment;
import com.sumon.bundleapp.installer.ui.dialogs.ThemeSelectionDialogFragment;
import com.sumon.bundleapp.installer.ui.dialogs.base.BaseBottomSheetDialogFragment;
import com.sumon.bundleapp.installer.utils.AlertsUtils;
import com.sumon.bundleapp.installer.utils.PermissionsUtils;
import com.sumon.bundleapp.installer.utils.PreferencesHelper;
import com.sumon.bundleapp.installer.utils.PreferencesKeys;
import com.sumon.bundleapp.installer.utils.PreferencesValues;
import com.sumon.bundleapp.installer.utils.Theme;
import com.sumon.bundleapp.installer.utils.Utils;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rikka.shizuku.Shizuku;

public class PreferencesFragment extends PreferenceFragmentCompat implements FilePickerDialogFragment.OnFilesSelectedListener, SingleChoiceListDialogFragment.OnItemSelectedListener, BaseBottomSheetDialogFragment.OnDismissListener, SharedPreferences.OnSharedPreferenceChangeListener, DarkLightThemeSelectionDialogFragment.OnDarkLightThemesChosenListener, Shizuku.OnRequestPermissionResultListener {

    private PreferencesHelper mHelper;
    private PackageManager mPm;

    private Preference mHomeDirPref;
    private Preference mFilePickerSortPref;
    private Preference mInstallerPref;
    private Preference mThemePref;
    private Preference mAppLanguagePref;

    // Order must match R.array.app_language_names exactly. Empty string means
    // "System default" (clears the app-level locale override).
    private static final String[] APP_LANGUAGE_TAGS = {
            "", "en", "ar", "az", "bg", "de", "el", "es", "fr", "it", "ja",
            "ko", "pl", "pt-BR", "ru", "sv", "tr", "uk", "vi", "zh-CN", "zh-TW"
    };

    private SwitchPreference mAutoThemeSwitch;
    private Preference mAutoThemePicker;

    private FilePickerDialogFragment mPendingFilePicker;

    private final ExecutorService mBackgroundExecutor = Executors.newCachedThreadPool();
    private Boolean mShizukuAvailableCache = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mHelper = PreferencesHelper.getInstance(requireContext());
        mPm = requireContext().getPackageManager();

        //Inject some prefs
        //Inject current auto theme status since it isn't managed by PreferencesKeys.AUTO_THEME key
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                PreferencesKeys.AUTO_THEME,
                Theme.getInstance(requireContext()).getThemeMode() == Theme.Mode.AUTO_LIGHT_DARK
        ).apply();

        //Inject apk proxy activity state since there's no guarantee preference value matches actual state
        int apkProxyActivityState = mPm.getComponentEnabledSetting(
                ApkActionViewProxyActivity.getComponentName(requireContext()));

        boolean isApkProxyActivityEnabled;

        switch (apkProxyActivityState) {
            case PackageManager.COMPONENT_ENABLED_STATE_DEFAULT:
            case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                isApkProxyActivityEnabled = true;
                break;

            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED:
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER:
            case PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED:
                isApkProxyActivityEnabled = false;
                break;

            default:
                throw new IllegalStateException(
                        String.format("ApkProxyActivity state is %d", apkProxyActivityState));
        }

        prefsEditor.putBoolean(
                PreferencesKeys.ENABLE_APK_ACTION_VIEW,
                isApkProxyActivityEnabled
        );

        prefsEditor.apply();

        if (Utils.apiIsAtLeast(Build.VERSION_CODES.M)) {
            Shizuku.addRequestPermissionResultListener(this);
        }

        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey);

        mAppLanguagePref = findPreference("app_language");
        updateAppLanguageSummary();

        mAppLanguagePref.setOnPreferenceClickListener((p) -> {
            SingleChoiceListDialogFragment.newInstance(
                    getText(R.string.settings_main_app_language),
                    R.array.app_language_names,
                    getCurrentAppLanguageIndex()
            ).show(getChildFragmentManager(), "app_language");

            return true;
        });

        mHomeDirPref = findPreference("home_directory");
        updateHomeDirPrefSummary();

        mHomeDirPref.setOnPreferenceClickListener((p) -> {
            selectHomeDir();
            return true;
        });

        mFilePickerSortPref = findPreference("file_picker_sort");
        updateFilePickerSortSummary();

        mFilePickerSortPref.setOnPreferenceClickListener((p) -> {
            SingleChoiceListDialogFragment.newInstance(
                    getText(R.string.settings_main_file_picker_sort),
                    R.array.file_picker_sort_variants,
                    mHelper.getFilePickerRawSort()
            ).show(getChildFragmentManager(), "sort");

            return true;
        });

        findPreference("about").setOnPreferenceClickListener((p) -> {
            startActivity(new Intent(getContext(), AboutActivity.class));
            return true;
        });

        mInstallerPref = findPreference("installer");
        updateInstallerSummary();

        mInstallerPref.setOnPreferenceClickListener((p) -> {
            SingleChoiceListDialogFragment.newInstance(
                    getText(R.string.settings_main_installer),
                    R.array.installers,
                    mHelper.getInstaller()
            ).show(getChildFragmentManager(), "installer");

            return true;
        });

        findPreference(PreferencesKeys.BACKUP_SETTINGS).setOnPreferenceClickListener(p -> {
            startActivity(new Intent(requireContext(), BackupSettingsActivity.class));
            return true;
        });

        mThemePref = findPreference(PreferencesKeys.THEME);
        updateThemeSummary();

        mThemePref.setOnPreferenceClickListener(p -> {
            ThemeSelectionDialogFragment.newInstance(requireContext())
                    .show(getChildFragmentManager(), "theme");

            return true;
        });

        if (Theme.getInstance(requireContext()).getThemeMode() != Theme.Mode.CONCRETE) {
            mThemePref.setVisible(false);
        }

        mAutoThemeSwitch = Objects.requireNonNull(
                findPreference(PreferencesKeys.AUTO_THEME));

        mAutoThemePicker = findPreference(
                PreferencesKeys.AUTO_THEME_PICKER);

        updateAutoThemePickerSummary();

        mAutoThemeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean value = (boolean) newValue;

            if (value) {
                if (!Utils.apiIsAtLeast(Build.VERSION_CODES.Q)) {
                    SimpleAlertDialogFragment.newInstance(
                            requireContext(),
                            R.string.settings_main_auto_theme,
                            R.string.settings_main_auto_theme_pre_q_warning
                    ).show(getChildFragmentManager(), null);
                }

                Theme.getInstance(requireContext())
                        .setMode(Theme.Mode.AUTO_LIGHT_DARK);

            } else {
                Theme.getInstance(requireContext())
                        .setMode(Theme.Mode.CONCRETE);
            }

            //Hack to not mess with hiding/showing preferences manually
            requireActivity().recreate();

            return true;
        });

        mAutoThemePicker.setOnPreferenceClickListener(pref -> {
            DarkLightThemeSelectionDialogFragment.newInstance()
                    .show(getChildFragmentManager(), null);

            return true;
        });

        if (Theme.getInstance(requireContext()).getThemeMode()
                != Theme.Mode.AUTO_LIGHT_DARK) {
            mAutoThemePicker.setVisible(false);
        }

        SwitchPreference enableApkActionViewPref =
                findPreference(PreferencesKeys.ENABLE_APK_ACTION_VIEW);

        enableApkActionViewPref.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    boolean enabled = (boolean) newValue;

                    mPm.setComponentEnabledSetting(
                            ApkActionViewProxyActivity.getComponentName(requireContext()),
                            enabled
                                    ? PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                                    : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP
                    );

                    return true;
                }
        );

        SwitchPreference disableAnimationsPref =
                findPreference(PreferencesKeys.DISABLE_ANIMATIONS);

        disableAnimationsPref.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    requireActivity().recreate();
                    return true;
                }
        );

        findPreference(PreferencesKeys.USE_ROOT)
                .setOnPreferenceChangeListener((preference, newValue) -> {
                    if ((boolean) newValue) {
                        mBackgroundExecutor.execute(() -> {
                            boolean rootAvailable = SuShell.isRootAvailable();
                            requireActivity().runOnUiThread(() -> {
                                if (!isAdded()) {
                                    return;
                                }
                                if (!rootAvailable) {
                                    SimpleAlertDialogFragment.newInstance(
                                            requireContext(),
                                            R.string.error,
                                            R.string.settings_main_root_unavailable
                                    ).show(getChildFragmentManager(), null);
                                    ((SwitchPreference) preference).setChecked(false);
                                }
                            });
                        });
                    }

                    return true;
                });

        findPreference(PreferencesKeys.USE_SHIZUKU)
                .setOnPreferenceChangeListener((preference, newValue) -> {
                    if ((boolean) newValue) {
                        if (!Shizuku.pingBinder()) {
                            SimpleAlertDialogFragment.newInstance(
                                    requireContext(),
                                    R.string.error,
                                    R.string.settings_main_shizuku_unavailable
                            ).show(getChildFragmentManager(), null);

                            return false;
                        }

                        if (Shizuku.checkSelfPermission()
                                != PackageManager.PERMISSION_GRANTED) {
                            mHelper.setInstaller(PreferencesValues.INSTALLER_NORMAL);
                            updateInstallerSummary();

                            Shizuku.requestPermission(
                                    PermissionsUtils.REQUEST_CODE_SHIZUKU);
                        }
                    }

                    return true;
                });

        findPreference(PreferencesKeys.ENABLE_LEGACY_MODE)
                .setOnPreferenceChangeListener((preference, newValue) -> {
                    requireActivity().recreate();
                    return true;
                });

        findPreference(PreferencesKeys.USE_DATA_DIR)
                .setOnPreferenceChangeListener((preference, newValue) -> {
                    requireActivity().recreate();
                    return true;
                });

        findPreference(PreferencesKeys.ALLOW_ROOT)
                .setOnPreferenceChangeListener((preference, newValue) -> {
                    requireActivity().recreate();
                    return true;
                });

        findPreference(PreferencesKeys.ALLOW_SHIZUKU)
                .setOnPreferenceChangeListener((preference, newValue) -> {
                    requireActivity().recreate();
                    return true;
                });

        findPreference(PreferencesKeys.USE_SHELL)
                .setOnPreferenceChangeListener((preference, newValue) -> {
                    requireActivity().recreate();
                    return true;
                });

        updateUseRootSummary();
        updateUseShizukuSummary();
        updateUseShellSummary();
    }

    private void updateHomeDirPrefSummary() {
        String homeDir = mHelper.getHomeDirectory();

        if (homeDir == null || homeDir.isEmpty()) {
            homeDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        mHomeDirPref.setSummary(homeDir);
    }

    private void updateFilePickerSortSummary() {
        String[] sortVariants = getResources().getStringArray(
                R.array.file_picker_sort_variants
        );
        int sortIndex = mHelper.getFilePickerRawSort();

        // Clamp index to valid range
        if (sortIndex < 0 || sortIndex >= sortVariants.length) {
            sortIndex = 0;
            mHelper.setFilePickerRawSort(sortIndex);
        }

        mFilePickerSortPref.setSummary(
                getString(
                        R.string.settings_main_file_picker_sort_summary,
                        sortVariants[sortIndex]
                )
        );
    }

    private void updateInstallerSummary() {
        String[] installers = getResources().getStringArray(
                R.array.installers
        );
        int installerIndex = mHelper.getInstaller();

        // Clamp index to valid range
        if (installerIndex < 0 || installerIndex >= installers.length) {
            installerIndex = PreferencesValues.INSTALLER_ROOTLESS;
            mHelper.setInstaller(installerIndex);
        }

        mInstallerPref.setSummary(
                getString(
                        R.string.settings_main_installer_summary,
                        installers[installerIndex]
                )
        );
    }

    private void updateThemeSummary() {
        mThemePref.setSummary(
                Theme.getInstance(requireContext())
                        .getConcreteTheme()
                        .getName(requireContext())
        );
    }

    private void updateAutoThemePickerSummary() {
        Theme theme = Theme.getInstance(requireContext());

        mAutoThemePicker.setSummary(
                getString(
                        R.string.settings_main_auto_theme_picker_summary,
                        theme.getLightTheme().getName(requireContext()),
                        theme.getDarkTheme().getName(requireContext())
                )
        );
    }

    private void updateAppLanguageSummary() {
        if (mAppLanguagePref == null) {
            return;
        }

        int index = getCurrentAppLanguageIndex();

        String[] names = getResources()
                .getStringArray(R.array.app_language_names);

        if (index >= 0 && index < names.length) {
            mAppLanguagePref.setSummary(names[index]);
        }
    }

    private int getCurrentAppLanguageIndex() {
        LocaleListCompat locales =
                AppCompatDelegate.getApplicationLocales();

        if (locales.isEmpty()) {
            return 0;
        }

        Locale currentLocale = locales.get(0);
        if (currentLocale == null) {
            return 0;
        }

        for (int i = 1; i < APP_LANGUAGE_TAGS.length; i++) {
            LocaleListCompat tagLocales = LocaleListCompat.forLanguageTags(APP_LANGUAGE_TAGS[i]);
            if (!tagLocales.isEmpty()) {
                Locale tagLocale = tagLocales.get(0);
                if (tagLocale != null
                        && currentLocale.getLanguage().equals(tagLocale.getLanguage())
                        && (currentLocale.getCountry().isEmpty()
                            || tagLocale.getCountry().isEmpty()
                            || currentLocale.getCountry().equals(tagLocale.getCountry()))) {
                    return i;
                }
            }
        }

        return 0;
    }

    private void selectHomeDir() {
        if (!Utils.apiIsAtLeast(Build.VERSION_CODES.M)
                || PermissionsUtils.hasStoragePermission(requireContext())) {

            DialogProperties properties = new DialogProperties();
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.selection_type = DialogConfigs.DIR_SELECT;
            properties.root = new File(
                    mHelper.getHomeDirectory() != null
                            ? mHelper.getHomeDirectory()
                            : Environment.getExternalStorageDirectory().getAbsolutePath()
            );

            FilePickerDialogFragment.newInstance(
                    "home",
                    getString(R.string.settings_main_home_directory),
                    properties
            ).show(getChildFragmentManager(), "file_picker");

        } else {
            DialogProperties properties = new DialogProperties();
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.selection_type = DialogConfigs.DIR_SELECT;
            properties.root = new File(
                    mHelper.getHomeDirectory() != null
                            ? mHelper.getHomeDirectory()
                            : Environment.getExternalStorageDirectory().getAbsolutePath()
            );

            mPendingFilePicker = FilePickerDialogFragment.newInstance(
                    "home",
                    getString(R.string.settings_main_home_directory),
                    properties
            );

            PermissionsUtils.requestStoragePermission(
                    this,
                    PermissionsUtils.REQUEST_CODE_STORAGE_PERMISSIONS
            );
        }
    }

    private void updateUseRootSummary() {
        Preference useRootPref =
                findPreference(PreferencesKeys.USE_ROOT);

        if (Utils.apiIsAtLeast(Build.VERSION_CODES.M)) {
            mBackgroundExecutor.execute(() -> {
                String suVersion = SuShell.getSuVersion();
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) {
                        return;
                    }
                    useRootPref.setSummary(
                            getString(
                                    R.string.settings_main_use_root_summary,
                                    suVersion
                            )
                    );
                });
            });
        } else {
            useRootPref.setSummary(
                    getString(
                            R.string.settings_main_use_root_summary,
                            getString(R.string.not_available)
                    )
            );
        }
    }

    private void updateUseShizukuSummary() {
        Preference useShizukuPref =
                findPreference(PreferencesKeys.USE_SHIZUKU);

        if (mShizukuAvailableCache != null) {
            useShizukuPref.setSummary(
                    mShizukuAvailableCache
                            ? getString(R.string.settings_main_use_shizuku_summary)
                            : getString(R.string.settings_main_shizuku_unavailable)
            );
        } else {
            mBackgroundExecutor.execute(() -> {
                boolean available = Shizuku.pingBinder();
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) {
                        return;
                    }
                    mShizukuAvailableCache = available;
                    useShizukuPref.setSummary(
                            available
                                    ? getString(R.string.settings_main_use_shizuku_summary)
                                    : getString(R.string.settings_main_shizuku_unavailable)
                    );
                });
            });
        }
    }

    private void updateUseShellSummary() {
        Preference useShellPref =
                findPreference(PreferencesKeys.USE_SHELL);

        useShellPref.setSummary(
                getString(R.string.settings_main_use_shell_summary)
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == PermissionsUtils.REQUEST_CODE_STORAGE_PERMISSIONS) {
            if (grantResults.length == 0
                    || grantResults[0] == PackageManager.PERMISSION_DENIED) {

                AlertsUtils.showAlert(
                        this,
                        R.string.error,
                        R.string.permissions_required_storage
                );

            } else {
                if (mPendingFilePicker != null) {
                    openFilePicker(mPendingFilePicker);
                    mPendingFilePicker = null;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, int grantResult) {
        if (requestCode == PermissionsUtils.REQUEST_CODE_SHIZUKU) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                AlertsUtils.showAlert(
                        this,
                        R.string.error,
                        R.string.permissions_required_shizuku
                );
            } else {
                mHelper.setInstaller(
                        PreferencesValues.INSTALLER_SHIZUKU
                );

                updateInstallerSummary();
            }
        }
    }

    @Override
    public void onFilesSelected(
            String tag,
            List<File> files) {

        switch (tag) {
            case "home":
                mHelper.setHomeDirectory(
                        files.get(0).getAbsolutePath()
                );
                updateHomeDirPrefSummary();
                break;
        }
    }

    @Override
    public void onItemSelected(
            String dialogTag,
            int selectedItemIndex) {

        switch (dialogTag) {
            case "app_language":
                LocaleListCompat newLocales =
                        selectedItemIndex == 0
                                ? LocaleListCompat.getEmptyLocaleList()
                                : LocaleListCompat.forLanguageTags(
                                        APP_LANGUAGE_TAGS[selectedItemIndex]
                                );

                AppCompatDelegate.setApplicationLocales(
                        newLocales
                );

                updateAppLanguageSummary();
                break;

            case "sort":
                mHelper.setFilePickerRawSort(selectedItemIndex);

                switch (selectedItemIndex) {
                    case 0:
                        mHelper.setFilePickerSortBy(
                                DialogConfigs.SORT_BY_NAME
                        );
                        mHelper.setFilePickerSortOrder(
                                DialogConfigs.SORT_ORDER_NORMAL
                        );
                        break;

                    case 1:
                        mHelper.setFilePickerSortBy(
                                DialogConfigs.SORT_BY_NAME
                        );
                        mHelper.setFilePickerSortOrder(
                                DialogConfigs.SORT_ORDER_REVERSE
                        );
                        break;

                    case 2:
                        mHelper.setFilePickerSortBy(
                                DialogConfigs.SORT_BY_DATE
                        );
                        mHelper.setFilePickerSortOrder(
                                DialogConfigs.SORT_ORDER_NORMAL
                        );
                        break;

                    case 3:
                        mHelper.setFilePickerSortBy(
                                DialogConfigs.SORT_BY_DATE
                        );
                        mHelper.setFilePickerSortOrder(
                                DialogConfigs.SORT_ORDER_REVERSE
                        );
                        break;

                    case 4:
                        mHelper.setFilePickerSortBy(
                                DialogConfigs.SORT_BY_SIZE
                        );
                        mHelper.setFilePickerSortOrder(
                                DialogConfigs.SORT_ORDER_NORMAL
                        );
                        break;

                    case 5:
                        mHelper.setFilePickerSortBy(
                                DialogConfigs.SORT_BY_SIZE
                        );
                        mHelper.setFilePickerSortOrder(
                                DialogConfigs.SORT_ORDER_REVERSE
                        );
                        break;
                }

                updateFilePickerSortSummary();
                break;

            case "installer":
                // Validate availability before persisting
                if (selectedItemIndex == PreferencesValues.INSTALLER_ROOTED) {
                    mBackgroundExecutor.execute(() -> {
                        boolean rootAvailable = SuShell.isRootAvailable();
                        requireActivity().runOnUiThread(() -> {
                            if (!isAdded()) {
                                return;
                            }
                            if (!rootAvailable) {
                                SimpleAlertDialogFragment.newInstance(
                                        requireContext(),
                                        R.string.error,
                                        R.string.settings_main_root_unavailable
                                ).show(getChildFragmentManager(), null);
                            } else {
                                mHelper.setInstaller(selectedItemIndex);
                                updateInstallerSummary();
                            }
                        });
                    });
                } else if (selectedItemIndex == PreferencesValues.INSTALLER_SHIZUKU) {
                    mBackgroundExecutor.execute(() -> {
                        boolean shizukuAvailable = Shizuku.pingBinder();
                        requireActivity().runOnUiThread(() -> {
                            if (!isAdded()) {
                                return;
                            }
                            if (!shizukuAvailable) {
                                SimpleAlertDialogFragment.newInstance(
                                        requireContext(),
                                        R.string.error,
                                        R.string.settings_main_shizuku_unavailable
                                ).show(getChildFragmentManager(), null);
                            } else if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                                Shizuku.requestPermission(
                                        PermissionsUtils.REQUEST_CODE_SHIZUKU
                                );
                            } else {
                                mHelper.setInstaller(selectedItemIndex);
                                updateInstallerSummary();
                            }
                        });
                    });
                } else {
                    mHelper.setInstaller(selectedItemIndex);
                    updateInstallerSummary();
                }
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(
            SharedPreferences sharedPreferences,
            String key) {

        if (PreferencesKeys.THEME_MODE.equals(key)) {
            updateThemeSummary();
        }
    }

    @Override
    public void onDismiss(
            BaseBottomSheetDialogFragment dialog) {
        updateUseRootSummary();
        updateUseShizukuSummary();
        updateUseShellSummary();
    }

    @Override
    public void onDarkLightThemesChosen(
            Theme.ThemeDescriptor lightTheme,
            Theme.ThemeDescriptor darkTheme) {

        Theme.getInstance(requireContext()).setLightTheme(lightTheme);
        Theme.getInstance(requireContext()).setDarkTheme(darkTheme);

        updateAutoThemePickerSummary();
    }

    @Override
    public void onResume() {
        super.onResume();

        mShizukuAvailableCache = null;
        updateUseRootSummary();
        updateUseShizukuSummary();
        updateUseShellSummary();
    }

    private void openFilePicker(
            FilePickerDialogFragment fragment) {
        fragment.show(
                getChildFragmentManager(),
                "file_picker"
        );
    }

    @Override
    public void onDestroy() {
        if (Utils.apiIsAtLeast(Build.VERSION_CODES.M)) {
            Shizuku.removeRequestPermissionResultListener(this);
        }

        mBackgroundExecutor.shutdown();

        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .unregisterOnSharedPreferenceChangeListener(this);

        super.onStop();
    }
}
