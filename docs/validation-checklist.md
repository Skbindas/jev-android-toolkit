# Validation checklist

Initial release validation is intentionally evidence-driven.

## Required before public release

- JVM unit and contract tests pass.
- Compose sample assembles successfully.
- No credentials or signing material are committed.
- Jev requests remain bounded and typed.
- Low-confidence choices have an abstain path.
- Critical deterministic guards run before semantic suppression.
- Search reranking stays bounded and batches candidates.
- Documentation matches the actual source tree.
- Public benchmark claims are backed by reproducible fixtures.

## Live API validation

Live TypeSafe calls are optional for the offline build. A future release validation run should record the requested model alias, returned model revision, timestamp, input shape, and result shape without storing the API key or user data.

Do not use a live model response as a substitute for deterministic regression tests.
