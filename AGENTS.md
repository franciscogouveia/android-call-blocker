# Repository Guidelines

## Project Structure & Module Organization

This is a single-module Android application. Keep code and resources in the standard Gradle layout:

- `app/src/main/java/` or `app/src/main/kotlin/` for production code.
- `app/src/main/res/` for layouts, strings, icons, and other Android resources.
- `app/src/test/` for JVM unit tests.
- `app/src/androidTest/` for device and emulator tests.

The call-screening service, activity, and pure decision logic live under `app/src/main/java/com/example/contactcallblocker/`. Do not commit generated directories such as `.gradle/`, `build/`, or `app/build/`.

## Build, Test, and Development Commands

Use the committed Gradle wrapper from the repository root:

- `./gradlew assembleDebug` — build the debug APK.
- `./gradlew test` — run local JVM tests.
- `./gradlew connectedAndroidTest` — run instrumented tests on a connected device or emulator.
- `./gradlew lint` — run Android static analysis.

Run commands from the repository root. Document any new setup steps in `README.md`.

## Coding Style & Naming Conventions

Use Kotlin for new Android code unless interoperability requires Java. Follow Kotlin's standard four-space indentation and Android Studio formatting. Name classes and composables in `PascalCase`, functions and properties in `camelCase`, constants in `UPPER_SNAKE_CASE`, and resource files in `lower_snake_case` (for example, `screen_block_list.xml`). Keep user-facing text in string resources rather than source code.

## Testing Guidelines

Use JUnit for local tests and AndroidX Test/Espresso for instrumented behavior. Name test classes after the subject, such as `CallFilterTest`, and test methods by expected behavior, such as `blocksNumberOnDenyList`. Add tests with each behavior change, especially for number normalization, matching rules, permissions, and call-screening edge cases.

## Commit & Pull Request Guidelines

No commit convention exists yet. Use short, imperative subjects (for example, `Add deny-list persistence`) and keep each commit focused. Pull requests should explain the change, list verification commands, link relevant issues, and include screenshots or recordings for UI changes. Highlight permission, manifest, database-schema, and privacy-impacting changes explicitly.
