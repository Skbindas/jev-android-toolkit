# Security Policy

## Reporting a vulnerability

Please do not open a public issue for a security vulnerability.

Use GitHub private vulnerability reporting when enabled for this repository. Include the affected version or commit, reproduction steps, impact, and a minimal proof of concept when safe.

## Secrets

Never commit a TypeSafe API key, RevenueCat secret, backend credential, signing key, or personal access token.

The Android sample must not embed production secrets in the APK. Live Jev access should be mediated by a trusted backend or proxy in production when the key cannot safely be contained on-device.

## Automated review

Pull requests are checked with repository CI. GitHub Dependency Review can be enabled after the repository Dependency Graph is enabled in Settings > Security analysis. Security-sensitive changes should include a focused regression test.
