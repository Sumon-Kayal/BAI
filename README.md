# Bundle APKs Installer (BAI)

BAI (Bundle APKs Installer) is an Android app that lets you install split APKs (such as ones distributed as an Android App Bundle) as if they were a single package. It supports both rooted and rootless installation methods.

[<img src="https://img.shields.io/badge/Download-GitHub_Releases-2ea44f?logo=github&logoColor=white"
     alt="Get it on GitHub Releases"
     height="40">](../../releases/latest)

BAI is not published on Google Play or F-Droid — grab the APK directly from [Releases](../../releases/latest).

## State of BAI
BAI is a fork of [SAI](https://github.com/Aefyr/SAI). The OG project is dead since 2021. If you're looking for an actual backup solution, you can try [OAndBackupX](https://f-droid.org/packages/com.machiav3lli.backup/) or [Swift Backup](https://play.google.com/store/apps/details?id=org.swiftapps.swiftbackup).

## What's different from upstream SAI
- **No Google Play Billing, no Firebase/Analytics, no F-Droid build flavor.** BAI ships as a single build variant with no telemetry and no in-app purchases; the donate screen links out to an external donation page instead.
- **Distributed only through GitHub Releases**, built by this repo's own CI — not affiliated with Play Store or F-Droid.
- **Modernized build:** AGP 9.2.0, Gradle 9.6.1, JDK 17, compileSdk/targetSdk 36, minSdk 23 (up from 21). Dependencies (AndroidX, Room, Glide, Shizuku, Material) bumped to current stable releases.
- **CodeQL security scanning** runs on every push/PR via `.github/workflows/codeql.yml`.
- Package name changed to `com.sumon.bundleapp.installer` so it can install side-by-side with the original SAI if you still have it.

## Requirements
Android 6.0 (API 23) or newer.

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
