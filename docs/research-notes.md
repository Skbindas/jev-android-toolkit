# Research notes

The initial design was informed by public Jev community projects and the official TypeSafe API documentation.

Patterns retained:

- dougsong/jev-android: bounded action choices, fresh-state validation, explicit verification, and package/action safeguards.
- Friedjof/jev-mobile: observe -> normalize -> decide -> mutate -> verify, durable state, recovery, and human escalation.
- SomeshSampat2/jev-android-super: accessibility-tree-driven decisions, deterministic execution, confidence thresholds, loop detection, and stop controls.
- ufec/jev-block-android-ad: asymmetric safety for notification filtering and fail-open behavior when uncertainty could swallow important alerts.
- mdwoicke/mobile-jev-android: fresh-target validation, traces, and explicit distinction between done and verified completion.
- yibie/awesome-jev: contributor quality rules, runnable checks, reproducibility, focused PRs, and no unverified performance claims.

The code in this repository is independently authored from those patterns; source code was not copied from the referenced projects.

Official API reference:
https://api.typesafe.ai/docs

The live client uses POST /v1/systemone with a Bearer key. The API exposes choice, score, and noul question types.
