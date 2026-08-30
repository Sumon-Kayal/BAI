# Bundle APKs Installer (BAI)

<a name="readme-top"></a>

<div align="center">

<img src="assets/BAI%20Banner.png" alt="BAI Banner">

<p><b>Install and back up split APKs and Android App Bundles — with rootless, root/shell, or Shizuku support.</b></p>

<p>
<a href="LICENSE"><img src="https://img.shields.io/badge/License-GPLv3-blue.svg" alt="License"></a>
<a href="https://github.com/Sumon-Kayal/BAI/releases/latest"><img src="https://img.shields.io/github/v/release/Sumon-Kayal/BAI" alt="Release"></a>
<a href="https://github.com/Sumon-Kayal/BAI/releases"><img src="https://img.shields.io/github/downloads/Sumon-Kayal/BAI/total" alt="Downloads"></a>
<a href="https://github.com/Sumon-Kayal/BAI/releases/latest"><img src="https://img.shields.io/badge/Download-GitHub_Releases-2ea44f?logo=github&logoColor=white" alt="Download"></a>
</p>

<p>
<a href="https://github.com/Sumon-Kayal/BAI/actions/workflows/Dead%20code%20check.yml"><img src="https://github.com/Sumon-Kayal/BAI/actions/workflows/Dead%20code%20check.yml/badge.svg" alt="Dead Code Check"></a>
<a href="https://github.com/Sumon-Kayal/BAI/actions/workflows/codeql.yml"><img src="https://github.com/Sumon-Kayal/BAI/actions/workflows/codeql.yml/badge.svg" alt="CodeQL"></a>
<a href="https://github.com/Sumon-Kayal/BAI/actions/workflows/debug-release.yml"><img src="https://github.com/Sumon-Kayal/BAI/actions/workflows/debug-release.yml/badge.svg" alt="Debug Release"></a>
</p>

</div>

> **Note:** BAI is distributed through this repository's [GitHub Releases](https://github.com/Sumon-Kayal/BAI/releases/latest) — it is not published on Google Play or F-Droid.

**BAI (Bundle APKs Installer)** is an Android application for installing and backing up APK packages, including split APK bundles commonly distributed through Android App Bundles.

It can install multiple APKs as a single application package and provides several installation backends, including the standard Android package installer, root/shell-based installation, and Shizuku where available.

## 📋 Table of Contents

- [Screenshots](#screenshots)
- [Features](#features)
- [Installation Methods](#installation-methods)
- [Supported Android Versions](#supported-android-versions)
- [Supported CPU Architectures](#supported-cpu-architectures)
- [State of BAI](#state-of-bai)
- [What's Different From Upstream SAI](#whats-different-from-upstream-sai)
- [Download](#download)
- [Building From Source](#building-from-source)
- [Release Signing](#release-signing)
- [Release CI](#release-ci)
- [Translations](#translations)
- [Exported `.apks` Metadata](#exported-apks-metadata)
- [Contributing](#contributing)
- [Documentation](#documentation)
- [EULA](#eula)
- [License](#license)

## Screenshots

<details>
<summary><b>App UI</b></summary><div align="center"><img src="assets/Screenshots/001_Screenshot_2026-08-30-00-56-55-251_com.sumon.bundleapp.installer.jpg" width="200" alt="Installer tab with no active sessions and an Install APKs button">
<img src="assets/Screenshots/002_Screenshot_2026-08-30-00-57-01-433_com.sumon.bundleapp.installer.jpg" width="200" alt="Install source dialog: internal file picker vs system file picker">
<img src="assets/Screenshots/003_Screenshot_2026-08-30-00-57-06-305_com.sumon.bundleapp.installer.jpg" width="200" alt="Internal file picker browsing storage to select APK files">
<img src="assets/Screenshots/004_Screenshot_2026-08-30-00-57-15-393_com.sumon.bundleapp.installer.jpg" width="200" alt="Backup tab prompting to select a directory for backups">
<img src="assets/Screenshots/005_Screenshot_2026-08-30-00-57-22-938_com.sumon.bundleapp.installer.jpg" width="200" alt="Backup directory picker dialog: internal vs system file picker">
<img src="assets/Screenshots/006_Screenshot_2026-08-30-00-57-32-053_com.sumon.bundleapp.installer.jpg" width="200" alt="System file picker for choosing the backup directory">
<img src="assets/Screenshots/007_Screenshot_2026-08-30-00-57-46-995_com.sumon.bundleapp.installer.jpg" width="200" alt="Settings: Languages and UI section with auto theme enabled">
<img src="assets/Screenshots/008_Screenshot_2026-08-30-00-57-57-238_com.sumon.bundleapp.installer.jpg" width="200" alt="Settings: UI section with BAI (F-Droid) theme and app features enabled">
<img src="assets/Screenshots/009_Screenshot_2026-08-30-00-58-04-803_com.sumon.bundleapp.installer.jpg" width="200" alt="Theme picker grid showing BAI, Ruby, Rena, Pixel, and other themes">
<img src="assets/Screenshots/010_Screenshot_2026-08-30-00-58-11-544_com.sumon.bundleapp.installer.jpg" width="200" alt="Settings screen showing the full Languages and UI section">
<img src="assets/Screenshots/011_Screenshot_2026-08-30-00-58-16-062_com.sumon.bundleapp.installer.jpg" width="200" alt="Dialog for picking separate light and dark themes">
<img src="assets/Screenshots/012_Screenshot_2026-08-30-00-58-57-339_com.sumon.bundleapp.installer.jpg" width="200" alt="Installer backend dialog: Rootless, Rooted, or Shizuku">
<img src="assets/Screenshots/013_Screenshot_2026-08-30-00-59-04-732_com.sumon.bundleapp.installer.jpg" width="200" alt="Install location dialog: system, internal, or external storage">
<img src="assets/Screenshots/014_Screenshot_2026-08-30-00-59-21-843_com.sumon.bundleapp.installer.jpg" width="200" alt="File sorting options dialog in the file picker">
<img src="assets/Screenshots/015_Screenshot_2026-08-30-00-59-34-754_com.sumon.bundleapp.installer.jpg" width="200" alt="Settings: Parser, Backup, and Other sections with About link">
<img src="assets/Screenshots/016_Screenshot_2026-08-30-00-59-39-038_com.sumon.bundleapp.installer.jpg" width="200" alt="Backup tab prompting to select a backup directory">
<img src="assets/Screenshots/017_Screenshot_2026-08-30-00-59-43-004_com.sumon.bundleapp.installer.jpg" width="200" alt="Backup directory picker dialog shown over the backup prompt">
<img src="assets/Screenshots/018_Screenshot_2026-08-30-00-59-49-248_com.sumon.bundleapp.installer.jpg" width="200" alt="About screen with app version, credits, and GitHub/Licenses links">
<img src="assets/Screenshots/019_Screenshot_2026-08-30-00-59-54-959_com.sumon.bundleapp.installer.jpg" width="200" alt="License viewer showing the GPL header and distribution terms">
<img src="assets/Screenshots/020_Screenshot_2026-08-30-01-00-06-213_com.sumon.bundleapp.installer.jpg" width="200" alt="License viewer showing additional GPL permission clauses">
<img src="assets/Screenshots/021_Screenshot_2026-08-30-01-00-10-699_com.sumon.bundleapp.installer.jpg" width="200" alt="License viewer showing project source code and contact links"></div></details><details>
<summary><b>Permission & setup screens</b></summary><div align="center"><img src="assets/Screenshots/022_Screenshot_2026-08-30-01-00-25-851_com.miui.securitycenter.jpg" width="200" alt="MIUI Security Center app info page for BAI">
<img src="assets/Screenshots/023_Screenshot_2026-08-30-01-00-34-101_com.google.android.permissioncontroller.jpg" width="200" alt="Android app permissions screen showing Storage allowed for BAI">
<img src="assets/Screenshots/024_Screenshot_2026-08-30-01-00-41-481_com.miui.securitycenter.jpg" width="200" alt="MIUI Security Center behavior settings for BAI">
<img src="assets/Screenshots/025_Screenshot_2026-08-30-01-00-47-416_com.miui.securitycenter.jpg" width="200" alt="MIUI Security Center detailed app info with version and install date"></div></details>

## Features

- Install split APKs and APK bundles as a single application.
- Install `.apk`, `.apks`, `.apkm`, and supported ZIP-based APK collections.
- Select multiple APK files or a single archive containing the required APKs.
- Rootless installation when supported by the device.
- Root/shell-based installation on devices where the required access is available.
- Shizuku-based installation support.
- Backup installed applications and their APK components.
- Export backups in `.apks` format.
- Built-in support for the `.apks` metadata format used by BAI.
- System file picker support for files stored on external storage.
- Per-app language selection from **Settings → Languages**.
- Light and dark themes.
- Edge-to-edge display that insets content properly around the system bars.
- Android TV/Leanback launcher support.

## Installation Methods

BAI can use different installation backends depending on the device and configuration:

- **Android package installer / rootless:** Uses Android's package installation APIs where possible.
- **Shizuku:** Allows package installation through Shizuku when Shizuku Server is running and BAI has been authorized.
- **Root/shell:** Available on devices where the required shell/root access is present.

The available method can vary by Android version, ROM, device configuration, and installed services.

## Supported Android Versions

- **Minimum:** Android 6.0 (API 23)
- **Target:** Android 16 (API 36)
- **Compile SDK:** Android API 37

## Supported CPU Architectures

Release and debug builds are generated separately for these ABIs:

```text
armeabi-v7a
arm64-v8a
x86
x86_64
```

There is no universal APK in the CI builds.

## State of BAI

BAI is a maintained fork of [SAI (Split APKs Installer)](https://github.com/Aefyr/SAI). Development on upstream SAI has slowed considerably, with its author indicating that future updates there will likely be limited to occasional bug fixes — BAI continues active development on top of that codebase.

BAI uses SAI's installer and backup code as its foundation while carrying its own package name, build system, translations, UI changes, and maintenance work.

If you are looking for a dedicated Android backup solution rather than an installer, the original SAI project recommends applications such as [OAndBackupX](https://f-droid.org/packages/com.machiav3lli.backup/) and [Swift Backup](https://play.google.com/store/apps/details?id=org.swiftapps.swiftbackup).

## What's Different From Upstream SAI

- **Separate package name:** `com.sumon.bundleapp.installer`, allowing BAI to coexist with the original SAI.
- **GitHub Releases distribution:** BAI is distributed from this repository instead of through Google Play or F-Droid.
- **Single build configuration:** The Google Play-specific flavor and related billing functionality have been removed.
- **Donation/billing code removed:** Donation UI and billing-status handling are not included.
- **Offline EULA:** The EULA is bundled with the application and can be displayed without a network request.
- **Per-app language selector:** Languages can be selected from the application's Settings instead of relying only on the system language.
- **Modern Android build:** BAI currently uses Android Gradle Plugin 9.3.2, Gradle 9.7.1, JDK 17, compile SDK 37, and target SDK 36 while retaining a minimum SDK of 23.
- **Faster Shizuku-based operations:** commands run through Shizuku reuse a single long-lived shell session instead of starting a new process for every command.
- **Vendored dependencies:** `flexfilter` and `pseudoapksigner` are included in the source tree instead of being fetched from JitPack.
- **CI security scanning:** CodeQL runs through `.github/workflows/codeql.yml`.
- **ABI-specific releases:** CI produces separate APKs for `armeabi-v7a`, `arm64-v8a`, `x86`, and `x86_64`.

## Download

The latest public builds are available from the repository's [GitHub Releases](https://github.com/Sumon-Kayal/BAI/releases/latest).

### Stable Releases

Stable releases are created by manually running the `release.yml` workflow in
GitHub Actions and providing an existing version tag in the `release_tag` input.
Version tags use the pattern:

```text
v*.*.*
```

For example:

```text
v4.6.0
```

The release workflow builds the four ABI-specific APKs and publishes them to the corresponding GitHub Release.

If release signing is configured with the repository's signing secrets, the release APKs are signed with that keystore. Otherwise, the workflow still completes and publishes the generated unsigned release APKs.

### Debug Builds

Debug builds are created by manually running the `debug-release.yml` workflow
in GitHub Actions against the selected branch or ref.

Debug APKs are:

- built for all four supported ABIs;
- signed with Android's debug keystore;
- uploaded as workflow artifacts with 14-day retention; and
- published to the rolling `debug-latest` GitHub pre-release.

Debug builds are intended for testing and development rather than normal public distribution.

## Building From Source

### Requirements

- JDK 17
- Android SDK with the required platform/build tools
- Git
- Internet access for the initial Gradle dependency download

The project uses the Gradle Wrapper, so you do not need to install Gradle separately.

<details>
<summary><b>🐧 Linux</b></summary>

1. Clone the repository:

```bash
git clone https://github.com/Sumon-Kayal/BAI.git
cd BAI
```

1. Make the Gradle Wrapper executable if necessary:

```bash
chmod +x gradlew
```

1. Build a release APKs:

```bash
./gradlew assembleRelease
```

1. Or build a debug APKs:

```bash
./gradlew assembleDebug
```

The generated APKs are placed under:

```text
app/build/outputs/apk/release/
app/build/outputs/apk/debug/
```

</details>

<details>
<summary><b>🪟 Windows</b></summary>

1. Clone the repository:

```powershell
git clone https://github.com/Sumon-Kayal/BAI.git
cd BAI
```

1. Build a release APK:

```powershell
.\gradlew.bat assembleRelease
```

1. Or build a debug APK:

```powershell
.\gradlew.bat assembleDebug
```

The generated APKs are placed under:

```text
app\build\outputs\apk\release\
app\build\outputs\apk\debug\
```

</details>

<details>
<summary><b>🤖 Android Studio</b></summary>

You can also open the cloned repository directly in Android Studio.

Allow Android Studio to sync the Gradle project and install any missing Android SDK components requested by the project. Then use **Build → Make Project** or the Gradle tasks to build the application.

</details>

### ABI-Specific Outputs

Release and debug builds are configured for:

```text
armeabi-v7a
arm64-v8a
x86
x86_64
```

The exact APK filenames may vary depending on the Gradle configuration. Check the corresponding `app/build/outputs/apk/` directory after the build.

## Release Signing

See the [Release Signing Guide](SIGNING.md) for creating a personal release signing key and wiring it into the GitHub Actions release workflow.

## Release CI

The repository contains separate workflows for different build and maintenance tasks:

| Workflow | Purpose |
| --- | --- |
| `release.yml` | Builds ABI-specific release APKs and publishes versioned GitHub Releases |
| `debug-release.yml` | Builds and publishes the rolling `debug-latest` pre-release |
| `test-debug-release.yml` | Builds a debug APK as a downloadable workflow artifact only, without publishing a release |
| `codeql.yml` | Runs CodeQL security analysis |
| `Dead code check.yml` | Checks the project for unused/dead code |
| `stale.yml` | Handles stale GitHub issues/PRs |

The release and debug workflows must be started manually in GitHub Actions. For a release build, provide the existing version tag to build in the `release_tag` input; the debug workflows build the selected branch or ref.

## Translations

BAI includes a per-app language selector and currently ships resources for 20 languages.

<details>
<summary>Show supported languages</summary>

- Arabic
- Azerbaijani
- Bulgarian
- Chinese (Simplified)
- Chinese (Traditional)
- English
- French
- German
- Greek
- Italian
- Japanese
- Korean
- Polish
- Portuguese (Brazil)
- Russian
- Spanish
- Swedish
- Turkish
- Ukrainian
- Vietnamese

</details>

Translations are maintained through the project's translation workflow. Please see the contribution documentation before editing translated resources manually.

## Exported `.apks` Metadata

BAI adds metadata to `.apks` files it exports.

The format is documented in [META-FORMAT.md](META-FORMAT.md).

## Contributing

Contributions are welcome.

Please read the [Contributing Guide](CONTRIBUTING.md) before opening an issue or pull request.

## Documentation

- [Changelog](CHANGELOG.md) — version history and changes for each release.
- [`.apks` Metadata Format](META-FORMAT.md) — documentation for BAI's exported `.apks` metadata.
- [Contributing Guide](CONTRIBUTING.md) — contribution and pull request guidelines.

## EULA

By using Bundle APKs Installer (BAI), you agree to the terms in the [End-User License Agreement](EULA.md).

Please read the EULA before installing or distributing the application.

## License

BAI is licensed under the [GNU General Public License v3.0](LICENSE).

The BAI name, project identity, and branding are associated with the BAI project maintained by Sumon Kayal. Logo artwork was generated using Google Gemini.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<div align="center">

</div>
