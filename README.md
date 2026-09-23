# Jev Android Toolkit

Kotlin-first decision modules for Android, powered by TypeSafe Jev.

Jev Android Toolkit turns fuzzy product decisions into bounded typed judgments that normal application code can gate, verify, and act on. The toolkit is built around a strict boundary: Jev handles semantic uncertainty; application code owns consequences and side effects.

## Included

| Module | Jev decides | Deterministic code owns |
| --- | --- | --- |
| Paywall | show / defer / suppress | entitlement checks, pricing, purchases |
| Moderation | allow / review / reject | policy storage, user actions, enforcement |
| Notifications | deliver / digest / suppress | scheduling and notification APIs |
| Search reranking | candidate relevance scores | retrieval and final list handling |
| Verification | whether an observed result satisfies an expectation | actual mutation/action |

A Jetpack Compose playground demonstrates the modules without requiring a live API key.

## Design

    Observe
      |
      v
    Typed Jev judgment
      |
      v
    Confidence / policy gate
      |
      v
    Deterministic action
      |
      v
    Verification

Jev handles semantic judgment. Code handles side effects.

## Live Jev

The client targets the TypeSafe System One API.

    POST https://api.typesafe.ai/v1/systemone

Authentication uses a Bearer API key. The repository does not store keys and the sample's default demo uses a fake transport.

For production Android apps, do not ship a long-lived server credential inside the APK. Put live Jev access behind a trusted backend or proxy when the key cannot safely be contained on-device.

## Quick start

Install JDK 17 and Gradle 9.6.0.

    gradle test
    gradle :sample:compose-playground:assembleDebug

The libraries are intentionally small and use standard Maven publishing configuration so they can later be released independently.

## Why this is not an agent wrapper

The toolkit does not ask Jev for free-form instructions and then execute them. The decision surface is typed and bounded, followed by a local policy gate and deterministic application behavior.

The semantic modules also batch multi-question work where it is useful. For example, search reranking evaluates a bounded candidate set in one Jev request instead of making one network request per candidate.

## Research basis

The initial design was informed by public Jev Android/mobile projects and the awesome-jev contribution rules. Useful patterns include bounded action spaces, fresh state before acting, explicit confidence thresholds, verification, durable fixtures, and human fallback.

The implementation in this repository is independently authored. No implementation source was copied from the referenced projects. See docs/research-notes.md.

## Contribution quality bar

A PR needs more than a green build. Reviewers check:

- decision boundary quality
- fallback and abstain behavior
- tests and regression fixtures
- dependency and security impact
- documentation
- reproducibility of benchmark claims
- independent implementation

AI-assisted development is welcome and substantial AI generation should be disclosed.

## Project status

The initial implementation and CI validation are complete. Repository visibility is managed separately from the codebase.

## License

MIT.
