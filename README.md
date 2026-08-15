![BAI Banner](https://github.com/Sumon-Kayal/BAI/blob/929fc2f7941e911501379fb3656cc68d361891e9/assets/BAI%20Banner.png?raw=true)

# Bundle APKs Installer (BAI)

BAI (Bundle APKs Installer) is an Android app that lets you install split APKs (such as ones distributed as an Android App Bundle) as if they were a single package. It supports both rooted and rootless installation methods.

[<img src="https://img.shields.io/badge/Download-GitHub_Releases-2ea44f?logo=github&logoColor=white"
     alt="Get it on GitHub Releases"
     height="40">](../../releases/latest)

[![Dead Code Check](https://github.com/Sumon-Kayal/BAI/actions/workflows/Dead%20code%20check.yml/badge.svg)](https://github.com/Sumon-Kayal/BAI/actions/workflows/Dead%20code%20check.yml)

[![CodeQL](https://github.com/Sumon-Kayal/BAI/actions/workflows/codeql.yml/badge.svg)](https://github.com/Sumon-Kayal/BAI/actions/workflows/codeql.yml)

[![Debug Release](https://github.com/Sumon-Kayal/BAI/actions/workflows/debug-release.yml/badge.svg)](https://github.com/Sumon-Kayal/BAI/actions/workflows/debug-release.yml)

BAI is not published on Google Play or F-Droid — grab the APK directly from [Releases](../../releases/latest).

## State of BAI
BAI is a fork of [SAI](https://github.com/Aefyr/SAI) by polychromaticfox, maintained by Sumon Kayal. If you're looking for an actual backup solution, you can try [OAndBackupX](https://f-droid.org/packages/com.machiav3lli.backup/) or [Swift Backup](https://play.google.com/store/apps/details?id=org.swiftapps.swiftbackup).

## What's different from upstream SAI
- **Distributed only through GitHub Releases**, built by this repo's own CI — not affiliated with Play Store or F-Droid.
- **Single build variant.** Based on SAI's F-Droid flavor (no Google Play Billing to begin with); the `normal`/Play Store flavor has been removed entirely rather than kept alongside it.
- **No donation feature.** The donate button, billing status tracking, and related UI have been removed entirely, not just hidden.
- **Offline EULA.** The End User License Agreement is bundled with the app (`app/src/main/assets/EULA/EULA.md`) and shown in-app — no network request needed to read it.
- **Per-app language switcher** under Settings → Languages, independent of your system language.
- **Modernized build:** AGP 8.8.0, Gradle 9.7.0, JDK 17, compileSdk/targetSdk 35 (Android 15) while keeping minSdk 21 (Android 5.0).
  ⚠️ *AGP 8.8.0 predates Gradle 9; Gradle's own compatibility matrix only lists tested support starting at AGP 9.0. This pairing has not been confirmed to build cleanly — verify before relying on it.*
- Two dependencies that no longer resolve on JitPack (`pseudoapksigner`, `flexfilter`) were vendored as plain Java source rather than replaced with alternatives, so behavior is unchanged.
- **CodeQL security scanning** runs on every push/PR via `.github/workflows/codeql.yml`.
- Package name changed to `com.sumon.bundleapp.installer` so it can install side-by-side with the original SAI if you still have it.

## Requirements
Android 5.0 (API 21) or newer.

## Building from source
```
git clone https://github.com/Sumon-Kayal/BAI.git
cd BAI
./gradlew assembleRelease
```
The unsigned APK will be at `app/build/outputs/apk/release/`. See `.github/workflows/release.yml` for how releases are built and published on tag push.

## Contributing
Please read [Contributing guide](/CONTRIBUTING.md)

## Exported .apks files meta
BAI adds some meta information to .apks files it exports, you can find the description of the format it uses in the [Meta format description](/META-FORMAT.md)

## EULA
By using Bundle APKs Installer (BAI), you agree to the terms outlined in the [End-User License Agreement](/EULA.md). Please ensure you read and understand it before installing or distributing split APKs.

## License
BAI is licensed under [GPLv3](/LICENSE)
