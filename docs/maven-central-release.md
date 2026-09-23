# Maven Central Release

This repository is prepared for Maven Central publishing through Sonatype Central Publisher Portal.

## Current release coordinates

- `dev.skbindas.jev:decision-core:0.1.0`
- `dev.skbindas.jev:jev-client:0.1.0`
- `dev.skbindas.jev:decision-modules:0.1.0`

These coordinates are not claimed as publicly released until a real Central deployment has passed validation and has been published.

## Release architecture

The repository uses Gradle's built-in `maven-publish` plugin and Sonatype's OSSRH Staging API compatibility service. Sonatype documents Gradle's built-in `maven-publish` as tested with this service.

The workflow uses `publishing_type=user_managed`. A successful CI run stages the signed deployment in the Central Publisher Portal; it does not automatically make the first Maven Central publication irreversible.

## GitHub environment

Create an environment named `maven-central`.

Recommended protection:
- require manual approval before the release job can access secrets;
- optionally restrict deployment tags to `v*`.

Environment secrets:
- `CENTRAL_TOKEN`
- `MAVEN_CENTRAL_SIGNING_KEY`
- `MAVEN_CENTRAL_SIGNING_PASSWORD`

Never commit these values.

## Sonatype prerequisites

Before running the workflow:
1. Sign in to Central Publisher Portal.
2. Verify a namespace containing the repository's Maven `groupId`.
3. Generate a Central Portal user token.
4. Generate a PGP signing key and publish the public key to a supported keyserver.
5. Add the required secrets to the `maven-central` GitHub environment.

The current project group ID is `dev.skbindas.jev`. A namespace such as `dev.skbindas` must be authorized before this exact coordinate can be published. If the verified namespace is instead `io.github.skbindas`, do not run the release yet: first decide whether to migrate the unreleased Maven coordinates.

## Release procedure

1. Ensure `main` is green.
2. Update `CHANGELOG.md`.
3. Create and push the matching tag, for example `v0.1.0`.
4. Run **Maven Central Release** from that tag.
5. Enter the verified namespace prefix.
6. Approve the `maven-central` environment when prompted.
7. Inspect the deployment in Central Publisher Portal.
8. Wait for Central validation to pass.
9. Publish the validated deployment in the Portal.
10. Verify all three coordinates from a clean Gradle/Kotlin consumer.

## Why publication is manual

Maven Central treats published components as immutable. The workflow therefore stages the signed deployment and leaves the final publish decision to the Portal.

## Validation

The workflow runs:
- JVM tests
- Android Compose sample assembly
- signed Maven publication
- Central staging transfer

The repository also contains a clean published-artifact consumer smoke test in `examples/maven-consumer`.

Do not call the project Maven Central released until the deployment has actually reached `PUBLISHED` and the coordinates resolve from Maven Central.
