# Non-Contact Call Blocker

Are you annoyed by unsolicited random numbers calling you? Time to fight back the spam. With this application, only numbers in your contact list will be able to reach you.

A deliberately small, offline Android app that blocks visible non-contact calls using Android's native call-screening API. Once selected for Android's call-screening role, it can block every incoming call that Android sends to its `CallScreeningService` and notify you of the blocked number. A switch in the app enables or disables blocking without changing the system role. It does not replace the system dialer, change Do Not Disturb, use networking, or collect data.

## How contact filtering works

The app intentionally does **not** request `READ_CONTACTS`. Android Telecom only sends telephone calls that are not in Contacts to a call-screening service without that permission. When blocking is enabled, the app immediately disallows every incoming call delivered by Android without marking it as manually rejected. When disabled, it immediately allows the call. Carrier and system configuration ultimately determine voicemail behavior.

Android does not deliver calls whose caller-ID presentation is restricted, unknown, unavailable, or payphone to third-party call-screening services. This app cannot block those calls and does not request sensitive permissions to work around the limitation. See the [`CallScreeningService` API documentation](https://developer.android.com/reference/android/telecom/CallScreeningService).

## Tested devices

- Google Pixel 7
- Samsung Galaxy S21 FE
- Samsung Galaxy A34

## Build and test

Install JDK 17 and Android SDK Platform 36, then set `ANDROID_HOME` or create an untracked `local.properties` containing `sdk.dir=/absolute/path/to/Android/Sdk`. The project uses AGP 8.13.2, Kotlin 2.2.21, and Gradle 8.14.5.

```bash
./gradlew test
./gradlew lint
./gradlew assembleRelease
```

GitLab CI runs the same checks for each pushed branch and merge request. It also audits the packaged APK for forbidden permissions and retains the unsigned APK, merged release manifest, lint report, and JUnit results as pipeline artifacts.

Without signing configuration, the release output is `app/build/outputs/apk/release/app-release-unsigned.apk`.

## Personal release signing

Create a key once and keep it and its passwords backed up privately:

```bash
keytool -genkeypair -v -keystore call-blocker.jks \
  -alias call-blocker -keyalg RSA -keysize 2048 -validity 10000
```

Create an untracked `keystore.properties` in the repository root:

```properties
storeFile=/absolute/path/to/call-blocker.jks
storePassword=your-store-password
keyAlias=call-blocker
keyPassword=your-key-password
```

Run `./gradlew assembleRelease`. The signed APK is `app/build/outputs/apk/release/app-release.apk`. Never commit the key, passwords, `keystore.properties`, `local.properties`, or generated APKs.

## Install, activate, and update

Enable USB debugging, connect the Pixel, and run:

```bash
adb install app/build/outputs/apk/release/app-release.apk
```

Open Non-Contact Call Blocker, tap **Grant call-screening access**, and approve the system role dialog. Once access is granted, the **Block calls from non-contacts** switch is the only blocking state shown; if Android access is later revoked, the switch is disabled and the access warning returns. Tap **Enable notifications** and grant Android's notification permission to receive a notification containing each blocked number. Once enabled, use **Manage or disable notifications** to open Android's notification settings and turn them off or adjust the blocked-calls channel. You can also verify the screening selection under **Settings > Apps > Default apps > Caller ID & spam app** (wording may vary by Android release). Google Phone remains the Phone app.

For an update, keep the same application ID and version-signing key, increment `versionCode`, rebuild, and run:

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

In **Settings > Apps > Non-Contact Call Blocker > Permissions**, verify that Notifications is the only requested permission. The service's manifest `BIND_SCREENING_SERVICE` attribute is a system binding restriction, not a permission granted to this app.

## Privacy audit

The only release runtime dependency is Kotlin's standard library (plus its JetBrains annotations metadata); there are no AndroidX or service SDK dependencies. The sole `<uses-permission>` entry is `POST_NOTIFICATIONS`, which is required on modern Android to show the blocked-number notification and can be denied without affecting call blocking. The app has no Internet, Contacts, call-log, phone-state, or SMS access. Blocked calls continue to use Android's normal call-log behavior.

## License

Copyright © 2026 Francisco de Gouveia.

This project is free software licensed under the [GNU General Public License](LICENSE), version 3 or (at your option) any later version (`GPL-3.0-or-later`). It comes with no warranty, to the extent permitted by law.
