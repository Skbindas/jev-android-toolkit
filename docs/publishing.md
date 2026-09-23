# Publishing and Release Readiness

The library modules use Gradle Maven Publish and are versioned from the root project.

## Current modules

- `dev.skbindas.jev:decision-core`
- `dev.skbindas.jev:jev-client`
- `dev.skbindas.jev:decision-modules`

Each module generates sources and Javadoc artifacts and exposes complete POM metadata. CI also publishes to an isolated local validation repository, which checks that the publication configuration resolves without contacting an external release repository.

The project does **not** claim a public Maven Central release until signed artifacts have actually been published and consumed successfully from a clean project.

## Release checklist

1. Run the complete JVM test suite.
2. Assemble the Android sample.
3. Run the Maven publication smoke test.
4. Verify the public API and compatibility notes.
5. Confirm all release artifacts include POM, module metadata, sources, Javadoc, and required signatures.
6. Update `CHANGELOG.md`.
7. Verify the Maven Central namespace and release credentials are configured.
8. Create a matching version tag, for example `v0.1.0`.
9. Run the guarded **Maven Central Release** workflow from that tag.
10. Inspect the deployment in Central Publisher Portal and wait for validation.
11. Publish the validated deployment in the Portal.
12. Verify all three coordinates from a clean consumer project.
13. Create GitHub release notes with installation examples.

## Maven Central publishing

The repository includes a guarded workflow at `.github/workflows/maven-central-release.yml`. It uses Gradle's `maven-publish` and signing plugins plus Sonatype's OSSRH Staging API compatibility service.

The workflow is deliberately manual and protected:

- it can only proceed from a version tag matching the root project version;
- it validates that the supplied Central namespace prefixes the Maven group ID;
- it requires release credentials through the `maven-central` GitHub environment;
- it runs the release test suite and Android sample build before publication;
- it stages the deployment with `publishing_type=user_managed` instead of automatically publishing it.

See [Maven Central release procedure](maven-central-release.md) for the exact namespace, token, PGP signing, GitHub environment, and Portal steps.

## Release credentials

Keep these values in the `maven-central` GitHub environment only:

- `CENTRAL_TOKEN`
- `MAVEN_CENTRAL_SIGNING_KEY`
- `MAVEN_CENTRAL_SIGNING_PASSWORD`

Never commit repository credentials, signing keys, or long-lived TypeSafe API keys.

## Validation

The repository contains a clean consumer smoke test in `examples/maven-consumer`. A release is not considered complete until the published coordinates resolve from Maven Central and the consumer can compile against them.
