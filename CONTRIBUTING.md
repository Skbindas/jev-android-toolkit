# Contributing to Jev Android Toolkit

Thanks for contributing. This repository is strict about decision boundaries because model output can influence product behavior.

## Before opening a PR

Keep each pull request focused on one change. Every decision module should:

1. Use Jev for a bounded semantic judgment.
2. Keep money, dates, permissions, persistence, irreversible actions, and side effects deterministic.
3. Define an explicit fallback or abstain path.
4. Include a runnable contract test.
5. Avoid hard-coded credentials and secrets.
6. Document inputs, output type, thresholds, and failure behavior.
7. Include reproducible benchmark evidence when making performance claims.

Substantial AI-assisted development is welcome. Disclose meaningful AI generation in the PR description so reviewers can reproduce and assess the work.

## Maintainer review gate

CI passing is necessary but not sufficient. Maintainers review correctness, Jev boundary quality, fallback behavior, test coverage, security impact, documentation, and independent implementation.

Maintainers explicitly approve or request changes after this review.

## Design rule

Observe -> typed Jev judgment -> policy gate -> deterministic action -> verification.

Do not let a model directly execute shell commands, arbitrary coordinates, payment actions, account changes, or other irreversible side effects.

## Local validation

Install JDK 17 and Gradle 9.1.0.

Run:

    gradle test
    gradle :sample:compose-playground:assembleDebug
