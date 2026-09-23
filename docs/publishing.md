# Publishing and Release Readiness

The library modules use Gradle Maven Publish and are versioned from the root project.

## Current modules

- `dev.skbindas.jev:decision-core`
- `dev.skbindas.jev:jev-client`
- `dev.skbindas.jev:decision-modules`

The project currently validates publishing configuration locally/through CI but does not claim a public Maven Central release until a signed, reproducible release has actually been published.

## Release checklist

1. Run the complete JVM test suite.
2. Assemble the Android sample.
3. Verify the public API and compatibility notes.
4. Update `CHANGELOG.md`.
5. Create a version tag.
6. Publish through the configured Maven repository.
7. Verify the published coordinates from a clean consumer project.
8. Create GitHub release notes with installation examples.

Do not commit repository credentials, signing keys, or long-lived API keys.
