# 🔐 BAI Release Signing Guide

Universal guide for **Windows, macOS, Linux, and Termux**.

This guide explains how to create, protect, prepare, and use a personal Android release signing key for bai/BAI releases and GitHub Actions.

> **Golden rule:** Generate the release key **once**. Keep the original keystore safe and reuse it for every future release.

---

## Table of Contents

- [1. What you need](#1-what-you-need)
- [2. Install Java](#2-install-java)
- [3. Create a secure key directory](#3-create-a-secure-key-directory)
- [4. Generate the release key](#4-generate-the-release-key)
- [5. Verify the key](#5-verify-the-key)
- [6. Back up the keystore](#6-back-up-the-keystore)
- [7. Create a Base64 copy](#7-create-a-base64-copy)
- [8. GitHub Actions secrets](#8-github-actions-secrets)
- [9. GitHub Actions workflow](#9-github-actions-workflow)
- [10. Gradle signing configuration](#10-gradle-signing-configuration)
- [11. Test a release](#11-test-a-release)
- [12. Verify an APK](#12-verify-an-apk)
- [13. Understanding SHA-256 values](#13-understanding-sha-256-values)
- [14. Never regenerate the key](#14-never-regenerate-the-key)
- [15. Quick reference](#15-quick-reference)
- [16. Security checklist](#16-security-checklist)

---

## 1. What you need

You need:

- Java JDK with `keytool`
- An Android project using Gradle
- Access to the GitHub repository
- A secure place to back up the release keystore
- The release keystore password
- The private-key password

The signing flow is:

```text
Release keystore
      │
      ├── .jks / .keystore
      │
      ▼
Base64 encoding
      │
      ▼
GitHub Actions Secret
      │
      ▼
release.yml
      │
      ├── Decode keystore
      ├── Create keystore.properties
      │
      ▼
Gradle
      │
      ▼
Signed APK
```

---

## 2. Install Java

`keytool` is included with the Java JDK.

### Windows

Install a JDK such as OpenJDK 17 or newer.

Then open PowerShell and check:

```powershell
java -version
keytool -help
```

If `keytool` is not found, make sure the JDK `bin` directory is in your `PATH`.

### macOS

Using Homebrew:

```bash
brew install openjdk@17
```

Check:

```bash
java -version
keytool -help
```

If required by your Homebrew installation, follow the displayed instructions to add the JDK to your `PATH`.

### Linux

For Debian/Ubuntu-based systems:

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

Check:

```bash
java -version
keytool -help
```

For other distributions, install the equivalent OpenJDK package.

### Termux

```bash
pkg update
pkg install openjdk-17
```

Check:

```bash
java -version
keytool -help
```

If you need Android shared-storage access:

```bash
termux-setup-storage
```

Grant the requested permission.

---

## 3. Create a secure key directory

Choose a location that is not inside your Git repository.

### Linux / macOS / Termux

```bash
mkdir -p ~/bai-keys
```

### Windows PowerShell

```powershell
New-Item -ItemType Directory -Force "$HOME\bai-keys"
```

The exact location is not important. The important thing is:

> **Do not store the private keystore inside the Git repository.**

---

## 4. Generate the release key

⚠️ **Run this command only once for the application signing identity.**

### Linux / macOS / Termux

```bash
keytool -genkeypair \
  -v \
  -keystore ~/bai-keys/bai-release.jks \
  -alias bai-release \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000
```

### Windows PowerShell

```powershell
keytool -genkeypair `
  -v `
  -keystore "$HOME\bai-keys\bai-release.jks" `
  -alias bai-release `
  -keyalg RSA `
  -keysize 4096 `
  -validity 10000
```

You will be asked for:

```text
Keystore password
Key password
Name
Organizational unit
Organization
City / Locality
State / Province
Country code
```

Example:

```text
Name:                 Your Name
Organizational Unit:  BAI Development
Organization:         Your Project
City:                 Your City
State:                Your State
Country:              IN
```

The certificate metadata is not the same thing as the private signing key. The passwords and the keystore itself are the critical items.

---

## 5. Verify the key

### Linux / macOS / Termux

```bash
keytool -list -v \
  -keystore ~/bai-keys/bai-release.jks \
  -alias bai-release
```

### Windows PowerShell

```powershell
keytool -list -v `
  -keystore "$HOME\bai-keys\bai-release.jks" `
  -alias bai-release
```

Look for:

```text
Entry type: PrivateKeyEntry
```

Also record the certificate SHA-256 fingerprint:

```text
SHA256: XX:XX:XX:XX:...
```

You can extract only the fingerprint on Linux/macOS/Termux with:

```bash
keytool -list -v \
  -keystore ~/bai-keys/bai-release.jks \
  -alias bai-release | grep SHA256
```

On Windows PowerShell:

```powershell
keytool -list -v `
  -keystore "$HOME\bai-keys\bai-release.jks" `
  -alias bai-release | Select-String "SHA256"
```

Keep the fingerprint as a reference for verifying future builds.

---

## 6. Back up the keystore

The `.jks` file contains the private signing key.

Make secure backups before using it for releases.

Recommended:

```text
Primary copy
    ↓
Secure offline backup
    ↓
Second secure backup
```

Do not rely on a single copy stored on one computer or phone.

#### Never commit the keystore

Do not commit `bai-release.jks` or `keystore.properties` to Git.

Add appropriate entries to `.gitignore`, for example:

```gitignore
*.jks
*.keystore
keystore.properties
```

---

## 7. Create a Base64 copy

GitHub Actions can receive the keystore as a Base64-encoded repository secret.

### Linux / macOS / Termux

```bash
base64 -w 0 ~/bai-keys/bai-release.jks > ~/bai-keys/bai-release-base64.txt
```

**macOS note:** some macOS versions use a `base64` implementation without `-w`. Use:

```bash
base64 ~/bai-keys/bai-release.jks | tr -d '\n' > ~/bai-keys/bai-release-base64.txt
```

### Termux

For a copy in Android Downloads:

```bash
mkdir -p /sdcard/Download/bai-keys
```

Then:

```bash
base64 -w 0 \
  /sdcard/Download/bai-keys/bai-release.jks \
  > /sdcard/Download/bai-keys/bai-release-base64.txt
```

### Windows PowerShell

```powershell
[Convert]::ToBase64String(
    [IO.File]::ReadAllBytes("$HOME\bai-keys\bai-release.jks")
) | Set-Content -NoNewline "$HOME\bai-keys\bai-release-base64.txt"
```

You should now have:

```text
bai-release.jks
bai-release-base64.txt
```

The Base64 file is only an encoded representation of the same keystore. It is **not a new key**.

---

## 8. GitHub Actions secrets

Open **GitHub repository → Settings → Secrets and variables → Actions** and create these four repository secrets.

### `BAI_KEYSTORE_BASE64`

Value: the complete contents of `bai-release-base64.txt`.

Linux/macOS/Termux:

```bash
cat ~/bai-keys/bai-release-base64.txt
```

Termux example:

```bash
cat /sdcard/Download/bai-keys/bai-release-base64.txt
```

Copy the entire output into the GitHub secret.

Windows PowerShell:

```powershell
Get-Content -Raw "$HOME\bai-keys\bai-release-base64.txt"
```

### `BAI_KEYSTORE_PASSWORD`

Value: your keystore password.

### `BAI_KEY_ALIAS`

Value:

```text
bai-release
```

### `BAI_KEY_PASSWORD`

Value: your private-key password.

If the keystore password and key password are identical, both password secrets can contain the same password.

#### Important

Do **not** put these values directly into `release.yml`. GitHub Actions secrets are intended to keep the private signing material out of the repository.

---

## 9. GitHub Actions workflow

The workflow should reconstruct the keystore during the GitHub Actions run.

Conceptually:

```text
BAI_KEYSTORE_BASE64
        │
        ▼
base64 --decode
        │
        ▼
release.keystore
        │
        ▼
keystore.properties
        │
        ▼
app/build.gradle
        │
        ▼
signingConfigs.release
        │
        ▼
assembleRelease
        │
        ▼
Signed APK
```

A typical decoding step looks like:

```yaml
- name: Decode signing keystore
  env:
    BAI_KEYSTORE_BASE64: ${{ secrets.BAI_KEYSTORE_BASE64 }}
    BAI_KEYSTORE_PASSWORD: ${{ secrets.BAI_KEYSTORE_PASSWORD }}
    BAI_KEY_ALIAS: ${{ secrets.BAI_KEY_ALIAS }}
    BAI_KEY_PASSWORD: ${{ secrets.BAI_KEY_PASSWORD }}
  run: |
    echo "$BAI_KEYSTORE_BASE64" | base64 -d > release.keystore

    cat > keystore.properties <<EOF
    storeFile=${{ github.workspace }}/release.keystore
    storePassword=$BAI_KEYSTORE_PASSWORD
    keyAlias=$BAI_KEY_ALIAS
    keyPassword=$BAI_KEY_PASSWORD
    EOF
```

> Adapt the command to the existing workflow rather than blindly replacing your release workflow.

For Windows runners, use the appropriate PowerShell decoding command if the workflow runs on Windows.

---

## 10. Gradle signing configuration

The Android module should read the generated `keystore.properties`.

A Groovy `build.gradle` configuration can look like:

```groovy
def keystorePropertiesFile = rootProject.file("keystore.properties")
def keystoreProperties = new Properties()
def hasSigningConfig = keystorePropertiesFile.exists()

if (hasSigningConfig) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
}

android {
    signingConfigs {
        release {
            if (hasSigningConfig) {
                storeFile file(keystoreProperties['storeFile'])
                storePassword keystoreProperties['storePassword']
                keyAlias keystoreProperties['keyAlias']
                keyPassword keystoreProperties['keyPassword']
            }
        }
    }

    buildTypes {
        release {
            if (hasSigningConfig) {
                signingConfig signingConfigs.release
            }
        }
    }
}
```

If your project already has equivalent logic, do not duplicate it.

For Kotlin DSL (`build.gradle.kts`), use the Kotlin DSL equivalent.

The important relationship is:

```text
keystore.properties
        ↓
signingConfigs.release
        ↓
release build
```

---

## 11. Test a release

If the GitHub Actions release workflow is manual-only:

```text
GitHub
  ↓
Actions
  ↓
Release workflow
  ↓
Run workflow
```

Run a test release before publishing a production release.

Check the workflow logs for:

- Keystore successfully decoded
- Gradle successfully loaded signing configuration
- Release APK successfully built
- APK successfully signed

Do not print secret values into workflow logs.

---

## 12. Verify an APK

Android's `apksigner` can display the certificate used to sign an APK.

```bash
apksigner verify \
  --verbose \
  --print-certs \
  app-release.apk
```

Look for:

```text
Signer #1 certificate SHA-256 digest:
XX:XX:XX:XX:...
```

Compare it with the SHA-256 fingerprint from your original keystore. If they match, the APK was signed by the expected certificate.

---

## 13. Understanding SHA-256 values

There are multiple SHA-256 values you may encounter.

### JKS file SHA-256

```bash
sha256sum bai-release.jks
```

This is the hash of the **entire JKS file**.

### Android certificate SHA-256

```bash
keytool -list -v \
  -keystore bai-release.jks \
  -alias bai-release | grep SHA256
```

This is the **certificate fingerprint** used to identify the signing certificate.

These values are expected to be different.

#### Example

```text
JKS file SHA-256:
eb9b8e3523eed71ed003111c0dc8da3497caf60ff43bfdf765e61b099bad7c7d

Android certificate SHA-256:
21:D3:F3:09:A3:C7:28:A2:D8:59:BB:3E:DC:A3:9B:1C:40:69:6A:68:6C:2D:B1:4D:26:1E:02:EE:96:AF:3F:4F
```

The first identifies a file. The second identifies the Android signing certificate.

---

## 14. Never regenerate the key

This command:

```bash
keytool -genkeypair ...
```

creates a new signing identity. Do **not** run it again just because you are setting up another computer.

Instead:

1. Recover your existing `bai-release.jks`.
2. Copy it to the new secure location.
3. Verify it.
4. Reuse it.

The same signing key can be used from Windows, macOS, Linux, Termux, and GitHub Actions. You do not need one signing key per operating system.

---

## 15. Quick reference

### Generate once

```bash
keytool -genkeypair \
  -v \
  -keystore ~/bai-keys/bai-release.jks \
  -alias bai-release \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000
```

### Verify

```bash
keytool -list -v \
  -keystore ~/bai-keys/bai-release.jks \
  -alias bai-release
```

### Get certificate fingerprint

```bash
keytool -list -v \
  -keystore ~/bai-keys/bai-release.jks \
  -alias bai-release | grep SHA256
```

### Base64

```bash
base64 -w 0 ~/bai-keys/bai-release.jks \
  > ~/bai-keys/bai-release-base64.txt
```

### GitHub secrets

```text
BAI_KEYSTORE_BASE64
BAI_KEYSTORE_PASSWORD
BAI_KEY_ALIAS
BAI_KEY_PASSWORD
```

### Alias

```text
bai-release
```

### Verify APK

```bash
apksigner verify --verbose --print-certs app-release.apk
```

---

## 16. Security checklist

Before publishing:

- [ ] `bai-release.jks` exists
- [ ] Keystore password is stored securely
- [ ] Key password is stored securely
- [ ] At least one additional secure backup exists
- [ ] `.jks` is excluded from Git
- [ ] `keystore.properties` is excluded from Git
- [ ] GitHub `BAI_KEYSTORE_BASE64` is configured
- [ ] GitHub `BAI_KEYSTORE_PASSWORD` is configured
- [ ] GitHub `BAI_KEY_ALIAS` is configured
- [ ] GitHub `BAI_KEY_PASSWORD` is configured
- [ ] Release workflow can decode the keystore
- [ ] Gradle uses the release signing configuration
- [ ] APK certificate fingerprint matches the original key
- [ ] No passwords or Base64 key material appear in workflow logs

---

## 🔑 Final reminder

Your signing key is part of your application's release identity. **Protect `bai-release.jks`.**

Do not publish it. Do not commit it. Do not paste it into issues, pull requests, chat, or public documentation.

If you move to another computer or phone, **copy the existing keystore** instead of generating a new one.

> **One application → one long-lived release signing identity → reuse it for future releases.**
