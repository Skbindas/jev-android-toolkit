# Architecture

Jev Android Toolkit follows a narrow decision-boundary pattern:

    App state
        |
        v
    Jev typed judgment
        |
        v
    Confidence / policy gate
        |
        v
    Deterministic application code
        |
        v
    UI or business action
        |
        v
    Verification / telemetry

## Jev owns semantic uncertainty

Good inputs are fuzzy questions such as routing, relevance, moderation, timing, or natural-language verification.

## Deterministic code owns consequences

Keep these out of the model:

- arithmetic and currency calculations
- date and time arithmetic
- permissions
- persistence
- network side effects
- payment execution
- account changes
- irreversible actions
- final authorization

The repository uses model output as a typed input to policy code, not as an instruction stream.
