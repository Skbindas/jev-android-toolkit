# Publishing and Release Readiness

The library modules use Gradle Maven Publish and are versioned from the root project.

## Current modules

- `dev.skbindas.jev:decision-core`
- `dev.skbindas.jev:jev-client`
- `dev.skbindas.jev:decision-modules`

Each module now generates sources and Javadoc artifacts and exposes complete POM metadata. CI also publishes to an isolated local validation repository, which checks that the publication configuration resolves without contacting an external release repository.

The project does **not** claim a public Maven Central release until signed artifacts have actually been published and consumed successfully from a clean project.

## Release checklist

1. Run the complete JVM test suite.
2. Assemble the Android sample.
3. Run the Maven publication smoke test.
4. Verify the public API and compatibility notes.
5. Confirm all release artifacts include POM, module metadata, sources, Javadoc, and required signatures.
6. Update `CHANGELOG.md`.
7. Create a version tag.
8. Publish through the configured Maven Central workflow or supported publisher.
9. Verify the published coordinates from a clean consumer project.
10. Create GitHub release notes with installation examples.

## Maven Central publishing

Sonatype's Central Publisher Portal does not currently provide an official Gradle publishing plugin. The repository therefore keeps the Gradle publication contract independent of the final publisher integration. A future release workflow can use a supported community publisher or a dedicated publishing service once credentials and signing configuration are available.

Do not commit repository credentials, signing keys, or long-lived TypeSafe API keys.
