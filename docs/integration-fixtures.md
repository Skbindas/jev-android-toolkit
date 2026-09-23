# Reproducible integration fixtures

The library tests use `JevTransport` injection so module behavior can be validated without a live TypeSafe request.

## Fixture pattern

Each fixture supplies a deterministic `JevResponse` for a known question identifier.

```kotlin
val client = JevClient(
    JevTransport {
        JevResponse(
            answers = mapOf(
                "decision_id" to /* deterministic JSON response */
            )
        )
    }
)
```

This keeps tests:

- offline;
- deterministic;
- independent of API keys;
- stable across model revisions;
- focused on the library's policy and boundary behavior.

## Covered modules

`DecisionModulesTest` exercises:

- paywall entitlement bypass;
- paywall unknown-choice abstention;
- notification priority bypass;
- notification unknown-choice abstention;
- batched semantic reranking;
- missing and low-confidence reranking abstention;
- moderation human-review routing;
- moderation unknown-choice abstention;
- verification fallback on missing answers.

`DecisionPolicyTest` covers confidence, margin, probability, and score-contract guards.

`JevClientContractTest` covers typed multi-question requests and JSON parsing.

## Live API tests

Live TypeSafe validation is deliberately separate from deterministic regression tests. When a future live integration suite is added, it should record the model alias/revision and request/result shapes without storing API keys or user data.
