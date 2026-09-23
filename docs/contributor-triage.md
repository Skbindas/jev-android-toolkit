# Contributor and Issue Triage Playbook

This guide keeps contributions focused, reproducible, and easy to review.

## What belongs here

Good contribution targets include:

- a bounded semantic decision with a typed output;
- a deterministic policy or abstention improvement;
- a reusable decision module with contract tests;
- a transport or fixture test that catches a real failure mode;
- documentation that makes an existing API easier to use;
- compatibility or build fixes that are reproduced locally and in CI.

Avoid adding a module only because it is a common AI feature. Prefer a concrete Android use case with a clear deterministic boundary.

## Decision-module contribution pattern

A new module should answer these questions before implementation:

| Question | Required answer |
| --- | --- |
| What does Jev decide? | A finite choice, bounded score, or explicit semantic verification |
| What does Kotlin decide? | Permissions, money, persistence, authentication, side effects, and irreversible actions |
| What happens when confidence is insufficient? | `ABSTAINED` or another explicit safe fallback |
| How is malformed model output handled? | Reject it deterministically; never coerce it into a valid action |
| How is the result tested? | Fixed fixtures covering accepted, boundary, malformed, and abstained cases |
| How is success verified? | Observe the resulting state when the operation has a meaningful observable outcome |

### Minimal implementation shape

```kotlin
val decision = decider.evaluate(state)

when (decision) {
    is Decision.Accepted -> applyDeterministicPolicy(decision.value)
    is Decision.Abstained -> useFallback()
}
```

The model should not receive authority to execute the consequence directly.

## Test fixture pattern

For a new decision module, prefer at least:

1. a normal accepted fixture;
2. a low-confidence or low-margin fixture;
3. a malformed/unknown-value fixture;
4. a boundary fixture at the configured threshold;
5. an explicit abstention fixture when the transport returns no usable decision.

Fixtures should be deterministic and committed when they are small. Do not commit API keys, user data, or production responses containing personal information.

## Benchmark evidence

Performance claims require a reproducible harness. Include:

- fixed input fixtures;
- warmup count;
- measured iteration count;
- JVM/runtime/toolchain information;
- the exact command used;
- correctness assertions before timing output.

Do not convert a local microbenchmark into a claim about production latency, model quality, or cost.

## Pull-request review checklist

Maintainers should verify:

- [ ] The change has one clear purpose.
- [ ] The Jev/deterministic boundary is explicit.
- [ ] Uncertainty and malformed output fail safely.
- [ ] Tests exercise the new behavior and failure path.
- [ ] No secrets or private production data are included.
- [ ] Public API changes are documented.
- [ ] Compatibility impact is stated.
- [ ] Benchmark claims, if any, are reproducible.
- [ ] AI-assisted implementation is disclosed when substantial.
- [ ] CI is green before merge.

A green CI run is necessary, not sufficient: maintainers should inspect the actual diff and fixtures.

## Issue triage

Use the following categories when opening or reviewing issues:

- **bug** — an existing behavior is incorrect or fails reproducibly;
- **feature** — a concrete new capability with a defined use case;
- **documentation** — an existing behavior/API is unclear or missing from docs;
- **build/ci** — Gradle, Android, dependency, or workflow problems;
- **security** — credential exposure, unsafe model boundary, dependency vulnerability, or other security concern;
- **compatibility** — behavior tied to a specific Kotlin, AGP, Gradle, Android API, device, or runtime;
- **good first issue** — small, well-scoped work with a clear acceptance condition.

When filing a bug, include:

1. commit/tag;
2. environment;
3. exact reproduction command;
4. expected behavior;
5. actual behavior;
6. relevant fixture or sanitized log.

Do not attach API keys, access tokens, personal user data, or unredacted production traces.

## Maintainer triage order

For an incoming issue:

1. Reproduce or identify why reproduction is currently impossible.
2. Check whether the behavior violates an existing contract.
3. Classify security-sensitive reports privately where appropriate.
4. Add the narrowest useful label/category.
5. Ask for the smallest missing reproduction detail.
6. Prefer a regression test before changing implementation.
7. Close duplicates or requests that are explicitly out of scope, with a short reason.

The triage process is intentionally evidence-first. Labels describe the issue; they do not replace technical review.