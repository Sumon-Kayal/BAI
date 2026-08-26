# Changelog

All notable changes to BAI (Bundle APKs Installer) are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project follows [Semantic Versioning](https://semver.org/).

## [Unreleased]

### 4.6.0 (pending release)
#### Added

- Added BAI's independent application identity and packaging.
- Added GitHub Actions workflows for BAI's build and release process.
- Added automated CodeQL security analysis.
- Added automated dead-code checking.
- Added dedicated debug-release automation for development builds.
- Added support for architecture-specific release builds:
  - `armeabi-v7a`
  - `arm64-v8a`
  - `x86`
  - `x86_64`
- Added Linux build instructions.
- Added Windows build instructions.
- Added Android Studio build instructions.
- Added a dedicated translation guide.
- Added project-specific changelog and release documentation.
- Added/updated project localization and language-selection support.

#### Changed

- Updated the Android build configuration for the current BAI development stack.
- Updated the project to target Android API 36.
- Updated the project to compile against Android API 37.
- Retained Android 6.0 (API 23) as the minimum supported Android version.
- Updated the Gradle and Android build tooling.
- Updated application branding and project resources for BAI.
- Updated translation resources across multiple supported languages.
- Updated dependency and build configuration compared with upstream SAI.
- Updated release packaging to produce separate APKs for supported CPU architectures.
- Updated documentation for building, contributing, translating, and releasing BAI.
- Updated EULA/legal resource handling for the BAI fork.

#### Removed

- Removed/reworked upstream Google Play-specific project configuration that is not required for BAI's distribution model.
- Removed/reworked upstream billing and donation-related integration where applicable.
- Removed reliance on upstream project-specific release configuration.

#### Fixed

- Fixed project/build configuration issues inherited from or exposed by the upstream project structure.
- Fixed CI configuration for BAI's independent build and release process.
- Fixed release packaging for the supported Android CPU architectures.
- Fixed documentation that no longer matched BAI's build and release setup.

#### Security

- Added CodeQL analysis to the CI pipeline.
- Added automated dead-code checks to help identify unnecessary or unused code.
- Updated the build/release configuration for BAI's independent distribution.

#### Compatibility

- Minimum Android version: **Android 6.0 (API 23)**
- Target Android version: **API 36**
- Compile SDK: **API 37**
- Supported ABIs:
  - `armeabi-v7a`
  - `arm64-v8a`
  - `x86`
  - `x86_64`

#### Credits

BAI is based on [SAI (Split APKs Installer)](https://github.com/Aefyr/SAI).

The original SAI project provides the foundation for BAI's split-APK installation and backup functionality.

---
