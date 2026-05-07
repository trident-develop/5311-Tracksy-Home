# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

Single-module Android project; use the Gradle wrapper (`./gradlew`) from the repo root.

- Build debug APK: `./gradlew :app:assembleDebug`
- Install on a connected device/emulator: `./gradlew :app:installDebug`
- Lint: `./gradlew :app:lint` (reports under `app/build/reports/lint-results-*.html`)
- Unit tests (JVM): `./gradlew :app:testDebugUnitTest`
- Run a single unit test: `./gradlew :app:testDebugUnitTest --tests "com.bailing.lark.roll.ExampleUnitTest.addition_isCorrect"`
- Instrumented tests (require a running device/emulator): `./gradlew :app:connectedDebugAndroidTest`
- Full check (lint + unit tests): `./gradlew :app:check`
- Clean: `./gradlew clean`

## Toolchain

- Android Gradle Plugin **9.1.1**, Kotlin **2.2.10**, Compose BOM **2026.02.01** — pinned in `gradle/libs.versions.toml`. All dependency/plugin versions live in this version catalog; add new libraries there rather than hard-coding versions in `app/build.gradle.kts`.
- `compileSdk = 36` (with `minorApiLevel = 1`), `minSdk = 28`, `targetSdk = 36`.
- Java/Kotlin source/target compatibility: **Java 11**.
- `local.properties` holds the local Android SDK path and is git-ignored — do not commit.

## Architecture

This is a freshly scaffolded Compose app — most files are stubs. Structure as it stands:

- **Package vs. project name mismatch is intentional state, not a bug to fix unprompted.** Project name is `Tracksy Home` (see `settings.gradle.kts`), but the application/namespace is `com.bailing.lark.roll` (see `app/build.gradle.kts`). All Kotlin sources live under `app/src/main/java/com/bailing/lark/roll/`.
- **Entry point is `LoadingActivity`, not `MainActivity`.** `AndroidManifest.xml` declares `LoadingActivity` as `exported="true"` with the `MAIN`/`LAUNCHER` intent filter; `MainActivity` is `exported="false"` and currently unreachable from the launcher. Both `setContent { }` blocks are empty — there is no UI yet.
- **UI stack:** Jetpack Compose + Material3 only (no Views, no Fragments, no Navigation library wired in). Theme is `TracksyHomeTheme` in `ui/theme/Theme.kt` — uses dynamic color on Android 12+ and falls back to the Purple/Pink palette in `Color.kt`. `Type.kt` defines the `Typography`. New screens should be `@Composable` functions invoked from one of the activities' `setContent { TracksyHomeTheme { ... } }`.
- **No DI, networking, persistence, or DataStore configured yet.** Dependencies declared in `app/build.gradle.kts` are limited to `core-ktx`, `lifecycle-runtime-ktx`, `activity-compose`, the Compose BOM (UI/graphics/tooling/material3), and JUnit/Espresso/Compose-test for tests. Adding any of those layers requires entries in `libs.versions.toml` first.
- **Release build does not minify** (`isMinifyEnabled = false`); `proguard-rules.pro` is empty. If you enable R8/minification, expect to add Compose/Kotlin keep rules.
- **Tests:** `app/src/test/` for JVM unit tests, `app/src/androidTest/` for instrumented tests. Both currently contain only the IDE-generated `Example*Test` stubs.
