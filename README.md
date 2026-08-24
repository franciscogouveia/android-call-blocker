# Contact Call Blocker

A deliberately small, offline Android app for a personal Pixel. Once selected for Android's call-screening role, it blocks every incoming call that Android sends to its `CallScreeningService`. It does not replace Google Phone as the dialer, change Do Not Disturb, use networking, or collect data.

## How contact filtering works

The app intentionally does **not** request `READ_CONTACTS`. Android Telecom only sends telephone calls that are not in Contacts to a call-screening service without that permission. The app therefore performs no contact lookup: it immediately disallows every incoming call delivered by Android, without marking it as manually rejected. Carrier and system configuration ultimately determine voicemail behavior.

Android does not deliver calls whose caller-ID presentation is restricted, unknown, unavailable, or payphone to third-party call-screening services. This app cannot block those calls and does not request sensitive permissions to work around the limitation. See the [`CallScreeningService` API documentation](https://developer.android.com/reference/android/telecom/CallScreeningService).

## Build and test

Install JDK 17 and Android SDK Platform 36, then set `ANDROID_HOME` or create an untracked `local.properties` containing `sdk.dir=/absolute/path/to/Android/Sdk`. The project uses AGP 8.13.2, Kotlin 2.2.21, and Gradle 8.14.5.

```bash
./gradlew test
./gradlew lint
./gradlew assembleRelease
```

Without signing configuration, the release output is `app/build/outputs/apk/release/app-release-unsigned.apk`.

## Personal release signing

Create a key once and keep it and its passwords backed up privately:

```bash
keytool -genkeypair -v -keystore contact-call-blocker.jks \
  -alias contact-call-blocker -keyalg RSA -keysize 2048 -validity 10000
```

Create an untracked `keystore.properties` in the repository root:

```properties
storeFile=/absolute/path/to/contact-call-blocker.jks
storePassword=your-store-password
keyAlias=contact-call-blocker
keyPassword=your-key-password
```

Run `./gradlew assembleRelease`. The signed APK is `app/build/outputs/apk/release/app-release.apk`. Never commit the key, passwords, `keystore.properties`, `local.properties`, or generated APKs.

## Install, activate, and update

Enable USB debugging, connect the Pixel, and run:

```bash
adb install app/build/outputs/apk/release/app-release.apk
```

Open Contact Call Blocker and tap **Enable call screening**, then approve the system role dialog. You can also verify the selection under **Settings > Apps > Default apps > Caller ID & spam app** (wording may vary by Android release). Google Phone remains the Phone app.

For an update, keep the same application ID and version-signing key, increment `versionCode`, rebuild, and run:

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

In **Settings > Apps > Contact Call Blocker > Permissions**, verify that no permissions are allowed or requested. The service's manifest `BIND_SCREENING_SERVICE` attribute is a system binding restriction, not a permission granted to this app.

## Privacy audit

The only release runtime dependency is Kotlin's standard library (plus its JetBrains annotations metadata); there are no AndroidX or service SDK dependencies. The app declares no `<uses-permission>` entries. In particular, it has no Internet, Contacts, call-log, phone-state, or SMS access. Blocked calls use Android's normal call-log and notification behavior.
