# Deterministic benchmark fixtures

The `benchmarks` JVM module provides a small reproducible microbenchmark harness for `DecisionPolicy`. It is intended for regression investigation, not marketing performance claims.

Run:

```bash
gradle :benchmarks:run
```

Methodology:

- 3 fixed JSON fixtures cover accepted, low-margin, and malformed-probability decisions.
- 1,000 warmup iterations are run before measurement.
- 10,000 measured iterations are executed per fixture.
- Output is CSV so results can be archived with environment metadata.
- The harness checks the expected policy outcome for every fixture before printing timing data.

Timing values are environment-specific. Compare runs only when the JDK, Gradle, machine class, and repository commit are recorded. The project does not publish a synthetic throughput claim from these fixtures.
