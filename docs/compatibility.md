# Compatibility

The project currently validates this toolchain in CI:

| Component | Version |
| --- | --- |
| JDK | 17 |
| Gradle | 9.6.0 |
| Android Gradle Plugin | 9.4.0 |
| Android API | 37 |
| Kotlin | 2.2.10 |
| Compose BOM | 2026.09.00 |

The project intentionally keeps the Kotlin/AGP toolchain changes separate from routine action and runtime dependency updates. Dependabot pull requests that change only one Kotlin plugin were not merged because the repository uses the JVM, serialization, and Compose Kotlin plugins together.

When upgrading the Kotlin toolchain, update all related Kotlin plugins as one tested change and validate the Android and JVM targets together.
