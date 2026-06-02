# Security Policy

## Supported Versions

| Version | Supported |
|---------|-----------|
| 1.x     | ✅ Yes    |

## Reporting a Vulnerability

**Please do NOT report security vulnerabilities through public GitHub issues.**

If you discover a security vulnerability in Stockify, please report it by emailing:
**varadmandhare924@gmail.com**

Include in your report:
- Description of the vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (if any)

You will receive a response within **48 hours**. If the issue is confirmed, a patch will be released as soon as possible.

## Security Practices in Stockify

- Passwords are hashed (never stored in plain text) using `PasswordUtils`
- No cleartext HTTP traffic (`android:usesCleartextTraffic="false"`)
- Files are shared via `FileProvider` (no direct file URI exposure)
- All activities except `LoginActivity` are `exported="false"`
- Signing credentials are never committed to the repository

## Out of Scope

- Issues in third-party libraries (report to their respective maintainers)
- Issues requiring physical access to an unlocked device
