# Repository Agent Guidance

AI-assisted contributions are accepted, but generated code is held to the same engineering bar as human-written code.

## Required properties

- Prefer small, composable typed decisions.
- Keep deterministic policy and side effects outside Jev.
- Add tests with behavior changes.
- Never invent benchmark numbers.
- Never commit credentials.
- Preserve license and attribution requirements.
- Do not copy implementation code from community repositories. Study patterns, then write an independent implementation.
- Cite upstream projects in documentation when a design pattern was materially informed by them.
- Disclose substantial AI-assisted generation.

## Review mindset

Treat model output as an input to software, not an authority. Every decision needs thresholds, fallback behavior, and a verification path where appropriate.
