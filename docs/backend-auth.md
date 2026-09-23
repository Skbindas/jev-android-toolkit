# Backend authentication

The Android client should not carry a long-lived TypeSafe API credential when the application cannot safely protect it.

## Reference flow

```text
Android app
   |
   | semantic state + typed questions
   v
Trusted backend / proxy
   |
   | Authorization: Bearer <server-side-key>
   v
TypeSafe Jev
   |
   v
Typed Jev response
   |
   v
Backend response
   |
   v
Android policy gate
   |
   v
Deterministic action + verification
```

## Responsibilities

The Android app owns user-visible context, deterministic policy, confidence thresholds, fallbacks, and the final side effect.

The trusted backend owns the long-lived TypeSafe credential, request authentication, rate limiting, tenant isolation, and server-side request logging appropriate for the application's privacy requirements.

The TypeSafe request should remain narrowly scoped to semantic decisions. Do not send unnecessary personal data, credentials, payment details, or secrets as model state.

## Client configuration

`HttpJevTransport` accepts an API key for live calls because the library also supports controlled server-side use. In a production Android application, prefer a backend-mediated transport rather than embedding the key in the APK.

For local development, the offline Compose playground and `JevTransport` fake fixtures do not require any live credential.

## Security checklist

- Keep TypeSafe credentials in server-side secret storage.
- Authenticate Android clients to your backend before forwarding requests.
- Apply request size and rate limits at the backend.
- Avoid logging raw user content when it is not required.
- Treat Jev output as untrusted input to deterministic policy code.
- Keep irreversible operations behind explicit application-side guards.
