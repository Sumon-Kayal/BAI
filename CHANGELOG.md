# Changelog

All notable changes to BAI (Bundle APKs Installer) are documented in this file.

This changelog focuses on changes made in BAI compared with the SAI 4.5 baseline.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project follows [Semantic Versioning](https://semver.org/).

## [4.6.0] - 2026-08-27 (Debug Release)

BAI 4.6.0 is based on SAI 4.5 and modernizes the project for current Android development while establishing BAI as an independently maintained application.

### Added

- Added BAI's independent application identity and packaging.
- Added architecture-specific release builds for:
  - `armeabi-v7a`
  - `arm64-v8a`
  - `x86`
  - `x86_64`
- Added dedicated debug-release automation for development builds.
- Added automated security analysis.
- Added automated dead-code checking.
- Added Linux build instructions.
- Added Windows build instructions.
- Added Android Studio build instructions.
- Added a dedicated translation guide.
- Added project-specific changelog and release documentation.
- Added/updated project localization and language-selection support.

### Changed

- Updated the Android build configuration from the SAI 4.5-era toolchain to the current BAI development stack.
- Updated the project to target Android API 36.
- Updated the project to compile against Android API 37.
- Raised the minimum supported Android version to Android 6.0 (API 23).
- Updated Gradle and Android build tooling.
- Updated application branding and project resources for BAI.
- Updated translation resources across supported languages.
- Updated dependency and build configuration from the SAI 4.5 baseline.
- Changed release packaging from a universal APK to separate APKs for supported CPU architectures.
- Updated documentation for building, contributing, translating, and releasing BAI.
- Updated EULA and legal resource handling for BAI's distribution model.

### Removed

- Removed or reworked upstream Google Play-specific project configuration that is not required for BAI's distribution model.
- Removed or reworked upstream billing and donation-related integration where applicable.
- Removed reliance on upstream project-specific release configuration.

### Fixed

- Fixed build and project-configuration issues encountered when modernizing the SAI 4.5 codebase.
- Fixed packaging issues affecting BAI's independent release builds.
- Fixed release packaging for the supported Android CPU architectures.
- Fixed documentation that no longer matched BAI's build and release setup.

### Security

- Added automated security analysis to the project.
- Added automated dead-code checking to identify potentially unnecessary or unused code.
- Updated build and release configuration for BAI's independent distribution.

### Compatibility

Compared with the SAI 4.5 baseline:

| | SAI 4.5 | BAI 4.6.0 |
|---|---|---|
| Minimum Android | API 21 | API 23 |
| Target SDK | API 29 | API 36 |
| Compile SDK | API 29 | API 37 |
| Release packaging | Universal APK | Architecture-specific APKs |
| Application identity | `com.aefyr.sai` | BAI-specific identity |
| Supported ABIs | Universal | `armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64` |

### Credits

BAI is based on [SAI (Split APKs Installer)](https://github.com/Aefyr/SAI).

The SAI 4.5 codebase provides the foundation for BAI's split-APK installation and backup functionality.

BAI 4.6.0 represents the modernization and independent development of that foundation.
