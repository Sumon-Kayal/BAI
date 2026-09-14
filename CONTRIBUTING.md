# Contributing to BAI

Thank you for your interest in contributing to BAI (Bundle APKs Installer)! Contributions of all kinds are welcome, including translations, bug reports, feature suggestions, documentation improvements, and code.

## Project structure: two platform generations

BAI builds as two Gradle product flavors — `legacy` (Android 6–10, API 23–29) and `modern` (Android 11–16, API 30–36) — sharing one `main` source set for everything that doesn't differ by Android version, with `src/legacy/` and `src/modern/` holding only the parts that do (currently: storage/file-picking permissions and a couple of manifest entries). If you're adding something that behaves differently across Android versions, it likely belongs behind a shared interface in `platform/` with one implementation per flavor, not an inline SDK_INT check — see `platform/PlatformPermissions.java` and `platform/PlatformFilePicker.java` for the existing pattern.

## Discussions

For general questions, ideas, feedback, or community discussions, please use GitHub Discussions:

https://github.com/Sumon-Kayal/BAI/discussions

Use Discussions for:
- Asking questions about BAI.
- Sharing ideas and suggestions.
- Getting help from the community.
- Discussing future plans before opening a feature request.

## Translations

Please **do not** submit translation pull requests to the main branch.

Instead, contribute translations through the dedicated **Translations** branch:

https://github.com/Sumon-Kayal/BAI/tree/Translations

If your language is not yet available, open a discussion or issue requesting its addition.

## Reporting Bugs

If you encounter a bug, please open a GitHub issue describing:

- Your device model and Android version.
- The BAI version you're using.
- Steps to reproduce the issue.
- Any relevant logs or screenshots.

Please avoid reporting installer failures as bugs unless you are reasonably certain they are caused by BAI itself. Many installation failures are caused by device, ROM, package, or archive-specific issues.

## Suggesting Features

Before opening a feature request, consider discussing your idea in GitHub Discussions.

If you've identified a well-defined enhancement, open a GitHub Issue describing:

- The feature you'd like to see.
- Why it would be useful.
- Any implementation ideas (optional).

## Code Contributions

1. Fork the BAI repository.
2. Create a new branch for your changes.
3. Implement and test your changes.
4. Submit a Pull Request.

Please keep the following in mind:

- Clearly describe what your changes do in the Pull Request description.
- Follow the existing project architecture and coding style.
- Format your code using Android Studio's **Reformat Code** feature (with **Optimize Imports** enabled) before committing.
- Keep commits focused and easy to review.

Thank you for helping improve BAI!
