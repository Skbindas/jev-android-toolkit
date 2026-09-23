# Jev Android Toolkit

[![CI](https://github.com/Skbindas/jev-android-toolkit/actions/workflows/ci.yml/badge.svg)](https://github.com/Skbindas/jev-android-toolkit/actions/workflows/ci.yml)
[![License](https://img.shields.io/github/license/Skbindas/jev-android-toolkit)](LICENSE)

**Kotlin-first Android decision modules powered by TypeSafe Jev** for paywall timing, UGC moderation, notification routing, semantic search reranking, and verification.

Jev Android Toolkit helps Android developers turn fuzzy product questions into **bounded, typed semantic decisions**. Deterministic Kotlin code remains responsible for permissions, payments, persistence, network side effects, and irreversible actions.

## Why this project

Most AI integrations stop at an untyped model wrapper. This toolkit focuses on the layer that Android applications actually need: **decision contracts, confidence gates, fallbacks, verification, and reusable Kotlin modules**.

Use it when your Android app needs an AI-assisted decision such as:

- Should this user see a paywall now, later, or not at all?
- Should user-generated content be allowed, reviewed, or rejected?
- Should a notification be delivered immediately, digested, or suppressed?
- Which search candidates are semantically most relevant?
- Does an observed result satisfy an expected outcome?

## Modules

| Module | Semantic decision | Deterministic boundary |
| --- | --- | --- |
| **Paywall** | show / defer / suppress | entitlement, pricing, purchase execution |
| **Moderation** | allow / review / reject | policy enforcement and user actions |
| **Notifications** | deliver / digest / suppress | Android notification APIs and scheduling |
| **Search reranking** | bounded relevance scores | retrieval, pagination, final result handling |
| **Verification** | observed outcome satisfies expectation | actual mutation and state verification |

## Architecture

```text
Android app state
      |
      v
Typed Jev decision
      |
      v
Confidence + policy gate
      |
      +---- low confidence ----> ABSTAIN / fallback
      |
      v
Deterministic Kotlin action
      |
      v
Verification / telemetry
```

**Rule:** Jev decides semantics. Kotlin decides consequences.

Money, dates, permissions, persistence, authentication, payment execution, account changes, and irreversible side effects stay outside the model.

## Quick start

Requirements:

- JDK 17
- Gradle 9.6.0
- Android SDK / API 37 for the sample

```bash
gradle test
gradle :sample:compose-playground:assembleDebug
```

The Compose playground runs its default decision example with a fake transport, so no API key is required for local tests.

## Live TypeSafe Jev

The client targets the TypeSafe System One API:

```text
POST https://api.typesafe.ai/v1/systemone
Authorization: Bearer <server-side-key>
```

Do **not** ship a long-lived TypeSafe API key inside an Android APK. Production apps should use a trusted backend or proxy when the credential cannot safely remain on-device.

## Maven artifacts

The three library modules are configured for Maven publication:

```text
dev.skbindas.jev:decision-core:0.1.0
dev.skbindas.jev:jev-client:0.1.0
dev.skbindas.jev:decision-modules:0.1.0
```

These coordinates are the project's intended release coordinates. A public Maven Central release is not claimed until the artifacts are signed, published, and verified from a clean consumer project.

## Backend authentication

For production Android apps, keep the long-lived TypeSafe credential behind a trusted backend or proxy. The repository includes a reference flow in [backend authentication](docs/backend-auth.md).

## Reproducible integration fixtures

Module contract tests inject `JevTransport` with deterministic fake responses. The same fixture pattern is used for paywall, moderation, notification routing, semantic reranking, and verification without requiring a live API key. See [integration fixtures](docs/integration-fixtures.md).

## Search and discovery

This repository intentionally uses the terms Android, Kotlin, Jetpack Compose, TypeSafe Jev, decision engine, AI decision modules, semantic ranking, UGC moderation, notification routing, paywall decisions, and verification because they describe the actual project.

The README is written for both developers discovering the project on GitHub and developers searching the web for reusable Android decision components. It does not use unrelated keyword stuffing.

## Community-inspired, independently implemented

The architecture was informed by public Jev Android/mobile projects and the `awesome-jev` contribution rules. Patterns studied include bounded actions, fresh-state validation, confidence thresholds, verification, durable fixtures, and human fallback.

**No implementation source was copied.** See [research notes](docs/research-notes.md).

## Documentation

- [Architecture](docs/architecture.md)
- [API reference — 0.1.0](docs/api/0.1.0.md)
- [Roadmap](ROADMAP.md)
- [Changelog](CHANGELOG.md)
- [Publishing and release readiness](docs/publishing.md)
- [Backend authentication](docs/backend-auth.md)
- [Integration fixtures](docs/integration-fixtures.md)
- [Benchmark fixtures](docs/benchmark-fixtures.md)
- [Hindi quick guide](docs/i18n/hi/README.md)

## Contributing

Focused pull requests are welcome.

Every decision module should define:

1. the exact semantic decision Jev owns;
2. the deterministic boundary;
3. confidence / abstain behavior;
4. runnable tests;
5. fallback behavior;
6. security and dependency impact.

See [CONTRIBUTING.md](CONTRIBUTING.md).

## Security

Never commit API keys, signing keys, backend credentials, or tokens. See [SECURITY.md](SECURITY.md).

## Project status

The toolkit foundation is implemented with JVM tests, Android sample validation, static CodeQL analysis, dependency review, Maven publication smoke testing, offline contract fixtures, and a deterministic benchmark harness.

## License

MIT.

