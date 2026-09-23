# Jev Android Toolkit — हिंदी गाइड

**Jev Android Toolkit** एक Kotlin-first Android library है जो TypeSafe Jev के साथ bounded semantic decisions बनाने में मदद करती है।

यह toolkit इन जैसे use cases के लिए है:

- Android AI decision engine
- Paywall decisioning
- UGC moderation
- Notification routing
- Semantic search reranking
- Confidence और verification gates

## यह क्या करता है?

ऐप की deterministic business logic अपने पास रहती है। Jev केवल semantic uncertainty वाले हिस्से में decision देता है।

Flow:

`Android state → typed Jev decision → confidence/policy gate → deterministic action → verification`

अगर decision पर्याप्त confident नहीं है, toolkit **ABSTAIN** कर सकती है। इससे uncertain model output को सीधे irreversible side effect में बदलने से रोका जाता है।

## शुरुआत

Repository को clone करके offline Compose playground चलाएँ:

```bash
./gradlew :sample:compose-playground:assembleDebug
```

Live Jev API का उपयोग करते समय long-lived API key APK में embed न करें। Trusted backend/proxy के माध्यम से authentication रखें।

## मुख्य शब्द

Android Jev, Kotlin Jev, TypeSafe Jev, Android decision modules, AI decision engine, semantic search, UGC moderation, notification routing, paywall decisions, verification और confidence gates.

> यह हिंदी पेज canonical English documentation का संक्षिप्त परिचय है। API और configuration details के लिए मुख्य README और `docs/` देखें।
