# Changelog

All notable changes to Jev Android Toolkit are documented here.

## [Unreleased]

- Added a versioned 0.1.0 API reference and deterministic benchmark fixtures.
- Added a local HTTP contract test for `HttpJevTransport` without requiring a live credential.

- Added automatic Gradle dependency-graph submission for the main branch.
- Refreshed GitHub Actions toolchain usage to current major action lines.
- Added Maven publication smoke testing with sources and Javadoc artifacts.
- Hardened decision policies against malformed probabilities, invalid scores, and invalid NOUL probabilities.
- Preserved explicit abstention for missing or unexpected module decisions.
- Expanded the offline Compose playground to cover all decision modules.
- Added backend authentication and reproducible fixture documentation.
- Recorded the tested toolchain in `docs/compatibility.md`.

## [0.1.0] - 2026-09-23

- Initial multi-module Kotlin/Android toolkit foundation.
- Typed Jev decision models and confidence/abstention policies.
- Paywall, moderation, notification routing, semantic reranking, and verification modules.
- Offline Jetpack Compose playground.
- JVM and Android CI validation.
- Contributor, security, CODEOWNERS, citation, and discoverability metadata.
