# Changelog

All notable changes to BAI (Bundle APKs Installer) are documented in this file.

This changelog documents BAI's divergence from the upstream SAI `master` branch, verified by a direct file-by-file comparison of the two source trees (506 files in SAI vs. 478 in BAI).

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project follows [Semantic Versioning](https://semver.org/).

## [4.6.0] - 2026-08-29

BAI 4.6.0 is based on SAI `master` (versionCode 60, versionName "4.5") and establishes BAI as an independently maintained, independently branded fork, modernizes the build for current Android tooling, and removes every Google Play–specific code path.

### Added

- **CI**: four new GitHub Actions workflows — `codeql.yml` (CodeQL analysis on every push/PR plus a weekly Monday schedule), `release.yml` (manual `workflow_dispatch` release build, taking a `release_tag` input), `debug-release.yml` (manual debug-build publishing), and `Dead code check.yml` (runs on push to `main`/`master`, skipping doc-only changes).
- **Per-app language picker**: `android:localeConfig` in the manifest plus `res/xml/locales_config.xml` declaring the 20 already-translated locales, a new "Languages" preference category with an "App language" entry, and new `ic_language`/`ic_pref_language` icons.
- **Offline EULA**: `EULA.md` at the repo root, a bundled copy under `app/src/main/assets/EULA`, a new `EulaActivity` with `activity_eula.xml`, and an "EULA"/"Privacy Policy" entry on the About screen — viewable without a network request.
- **Release signing**: optional `keystore.properties`-driven `signingConfigs` block (file is gitignored; build falls back gracefully if it's absent).
- **Per-architecture release builds** via `splits { abi { ... } }`: `armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64`, with `universalApk false`.
- **Room schema export**: `room { schemaDirectory "$projectDir/schemas" }`, producing `schemas/com.sumon.bundleapp.installer.common.AppDatabase/1.json`.
- **Version catalog**: `gradle/libs.versions.toml` centralizing every dependency and plugin version; `gradle/gradle-daemon-jvm.properties` pinning the Gradle daemon's JDK.
- New user-facing status strings: `settings_main_root_unavailable`, `settings_main_shizuku_unavailable`, and `eula_load_error`.
- `CHANGELOG.md` (this file).
- README rewritten from a 23-line stub into a 335-line document (table of contents, Features, Installation Methods, Supported Android Versions/Architectures, "What's Different From Upstream SAI", Download, Building From Source for Linux/Windows/Android Studio, Release CI, Translations, EULA); `CONTRIBUTING.md` expanded 18 → 62 lines, adding a Discussions section and more detail on bug/feature/code contributions.
- Repo assets: `assets/BAI Banner.png`, `assets/Screenshots`.
- `POST_NOTIFICATIONS` and `QUERY_ALL_PACKAGES` manifest permissions (see **Fixed**).

### Changed

- **Identity**: `applicationId`/`namespace` `com.aefyr.sai` → `com.sumon.bundleapp.installer`; app name "SAI" → "BAI", full name "Split APKs Installer" → "Bundle APKs Installer". Every in-app string mentioning "SAI" was updated to "BAI" across **all 20 translated languages**, not just English.
- **Source layout**: 238 of the app's 251 Java files moved from `com/aefyr/sai/*` to `com/sumon/bundleapp/installer/*`; the two vendored libraries kept their original `com.aefyr.*` packages (see **Removed**).
- **Attribution**: About screen now reads "Maintained by Sumon Kayal / Forked from SAI by polychromaticfox"; source link now points to `github.com/Sumon-Kayal/BAI`; translation link now points to a GitHub branch instead of Crowdin.
- SAI's two Google Play/F-Droid product flavors (`normal`, `fdroid`) collapsed into a single unified build target.
- **Build tooling**: Gradle 6.5 → 9.7.1; Android Gradle Plugin 4.1.2 → 9.3.2; Java source/target compatibility 8 → 17; `compileSdk` 29 → 37; `targetSdk` 29 → 36; `minSdk` 21 → 23; `versionCode` 60 → 61; `versionName` "4.5" → "4.6".
- Dependency resolution: `jcenter()` → `mavenCentral()` (JCenter has been shut down); removed the dead `dl.bintray.com/rikkaw/Libraries` repo.
- Dependency bumps: appcompat 1.2.0→1.8.0, documentfile 1.0.1→1.1.0, preference 1.1.1→1.2.1, recyclerview 1.1.0→1.4.0, Material Components 1.3.0-rc01→1.14.0, Glide 4.11.0→5.0.9, Gson 2.8.6→2.14.0, flexbox 2.0.1→3.0.0, Room 2.2.6→2.8.4, LeakCanary 2.3→2.14, Shizuku 11.0.1→13.1.5 (dependency group also migrated `rikka.shizuku` → `dev.rikka.shizuku`, adding the `shared` and `aidl` artifacts).
- Release packaging: single universal APK → separate per-ABI APKs.
- Default ProGuard file: `proguard-android.txt` → `proguard-android-optimize.txt`.
- `gradle.properties`: daemon heap `-Xmx1536m` → `-Xmx2560m`; dropped `android.enableJetifier` (no longer needed).
- Cosmetic/attribute updates across roughly 15 layout files (icon-tint syntax moved to `app:tint`, minor gravity/spacing tweaks).

### Removed

- Google Play Billing (`com.android.billingclient:billing`), Firebase Analytics, and Firebase Crashlytics dependencies, along with the Gradle block that conditionally applied the Google Services/Crashlytics plugins.
- Donation UI in full: the About-screen donate button, the "Support SAI" Settings entry, the standalone Donate activity/fragment/layouts, and 8 donate-related drawables — removed from every supported language, not just English.
- The Firebase Analytics opt-in toggle from Settings.
- The F-Droid (`src/fdroid`) and Play Store (`src/normal`) product-flavor source sets, including their per-flavor billing/analytics/legal classes.
- Crowdin integration (`crowdin.yml`) and Fastlane metadata (`fastlane/`) — translation now happens on a GitHub branch instead.
- `flexfilter` and `pseudoapksigner` as external JitPack dependencies — both are now vendored directly in the source tree at `com/aefyr/flexfilter` and `com/aefyr/pseudoapksigner` (same functionality as in SAI; packaging only).

### Fixed

- Added `android:exported="true"` to `MainActivity` and `ApkActionViewProxyActivity` — with target SDK 36, omitting it on components with intent filters can cause manifest merging to fail and prevent installation on Android 12+.
- Added `android:foregroundServiceType="dataSync"` to the backup service plus the matching `FOREGROUND_SERVICE_DATA_SYNC` permission, required from Android 14+.
- Added the `POST_NOTIFICATIONS` permission — without it, backup-progress notifications silently never appeared on Android 13+.
- Added the `QUERY_ALL_PACKAGES` permission — without it, the app's core purpose of listing installed apps for backup silently returned almost nothing on Android 11+.
- Added the `namespace` declaration and `buildFeatures { buildConfig true }`, both required by current AGP now that the manifest `package` attribute and implicit BuildConfig generation are no longer sufficient.
- Fixed a string-formatting bug in `settings_main_auto_theme_picker_summary`: two unindexed `%s` placeholders replaced with positional `%1$s`/`%2$s`.

### Security

- Added CodeQL static analysis, running on every push/PR and weekly on a schedule.
- Added an automated dead-code-check workflow.

### Compatibility

| | SAI (`master`) | BAI 4.6.0 |
|---|---|---|
| Gradle | 6.5 | 9.7.1 |
| Android Gradle Plugin | 4.1.2 | 9.3.2 |
| Java compatibility | 8 | 17 |
| Minimum Android | API 21 | API 23 |
| Target SDK | API 29 | API 36 |
| Compile SDK | API 29 | API 37 |
| Application ID | `com.aefyr.sai` | `com.sumon.bundleapp.installer` |
| Product flavors | `normal`, `fdroid` | none (unified) |
| Release packaging | Universal APK | Per-ABI APKs (armeabi-v7a, arm64-v8a, x86, x86_64) |

### Credits

BAI is based on [SAI (Split APKs Installer)](https://github.com/Aefyr/SAI) by Aefyr/polychromaticfox, licensed GPLv3. The SAI codebase provides the foundation for BAI's split-APK installation and backup functionality; this release represents its modernization and independent maintenance going forward.
